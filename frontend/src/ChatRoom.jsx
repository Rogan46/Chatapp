import React, { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import "./ChatRoom.css";
import backendHost from "./Config";

function ChatRoom({ username ,onLogout}) {
  const [publicMsg, setPublicMsg] = useState("");
  const [privateMsg, setPrivateMsg] = useState("");
  const [publicMessages, setPublicMessages] = useState([]);
  const [privateMessages, setPrivateMessages] = useState([]);
  const [onlineUsers, setOnlineUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [stompClient, setStompClient] = useState(null);
  const [connected, setConnected] = useState(false);
  const [userActivity, setUserActivity] = useState("");
  const [allUsers, setAllUsers] = useState([]);
  const [unreadCounts, setUnreadCounts] = useState({});

  useEffect(() => {
    async function loadUsers() {
      try {
        const res = await fetch(`${backendHost}/api/auth/users`);
        const users = await res.json();
        setAllUsers(users);
      } catch (err) {
        console.error("❌ Failed to load users", err);
      }
    }

    loadUsers();
  }, []);  // ← runs only once on page load

let client = null;
  useEffect(() => {

    if ("Notification" in window && Notification.permission === "default") {
      Notification.requestPermission();
    }

    // const backendHost =
    //   process.env.REACT_APP_BACKEND_URL ||
    //   `${window.location.protocol}//${window.location.hostname}:8080`;

    console.log(`Backend host: ${backendHost}`);
    console.log("✅ Backend Host:", backendHost);
    if (stompClient) {
    stompClient.deactivate();   // 🔥 cleanup previous connection
  }



     client = new Client({
      webSocketFactory: () => new SockJS(`${backendHost}/ws?username=${username}`),
      reconnectDelay: 5000,

      onConnect: () => {
        console.log("✅ Connected to WebSocket");
        setConnected(true);

        client.subscribe("/topic/users", (payload) => {
          const users = JSON.parse(payload.body);
          setOnlineUsers(users);
        });


        client.subscribe("/topic/public", (payload) => {
          const msg = JSON.parse(payload.body);
          setPublicMessages((prev) => [...prev, msg]);
          if (msg.type === "JOIN" || msg.type === "LEAVE") {
            setUserActivity(msg.content);
            setTimeout(() => setUserActivity(""), 5000);

          }
        });

        client.subscribe("/user/queue/messages", (payload) => {
          const msg = JSON.parse(payload.body);
          console.log("📌 Private message arrived:", msg);
          const fromUser = msg.sender;
          const toUser = msg.receiver;

          // 🔔 Show notification ONLY if it's actually sent to me AND not by me
          if (toUser === username && fromUser !== username && Notification.permission === "granted") {
            new Notification(`New Message from ${fromUser}`, {
              body: msg.content,
            });
          }

          // 💬 If this message belongs to the currently open chat, show in privateMessages
          setPrivateMessages((prev) =>
            (fromUser === selectedUser || toUser === selectedUser)
              ? [...prev, msg]
              : prev
          );

          // 🔴 Unread count: if message is sent TO ME and I'm NOT currently viewing that user
          if (toUser === username && fromUser !== selectedUser) {
            setUnreadCounts((prev) => {
  const current = prev[fromUser] || 0;
  return {
    ...prev,
    [fromUser]: current + 1
  };
});
          } setTimeout(() => {
            const list = document.querySelector(".messages-list");
            if (list) list.scrollTop = list.scrollHeight;
          }, 100);
        });


        client.publish({
          destination: "/app/user-status",
          body: JSON.stringify({
            sender: username,
            type: "JOIN",
            content: `${username} joined the chat`,
          }),
        });
      },
      onStompError: (err) => console.error("❌ STOMP error", err),
      onWebSocketClose: () => setConnected(false),
    });

    client.activate();
    setStompClient(client);


    const handleUnload = () => {
      if (client.connected) {
        client.publish({
          destination: "/app/user-status",
          body: JSON.stringify({
            sender: username,
            type: "LEAVE",
            content: `${username} left the chat`,
          }),
        });
      }
    };

    window.addEventListener("beforeunload", handleUnload);
    return () => {
      handleUnload();
     
  if (client) client.deactivate();
      window.removeEventListener("beforeunload", handleUnload);
    };
  }, [username]);

  const sendPublicMessage = () => {
    if (connected && stompClient && publicMsg.trim() !== "") {
      const chatMessage = { sender: username, content: publicMsg, type: "CHAT" };
      stompClient.publish({
        destination: "/app/public-message",
        body: JSON.stringify(chatMessage),
      });
      setPublicMsg("");
    }
  };

  const sendPrivateMessage = (targetUser) => {
    if (!connected || !stompClient || !privateMsg.trim() || !targetUser) return;
    const chatMessage = {
      sender: username,
      receiver: targetUser,
      content: privateMsg,
      type: "CHAT",
    };
    stompClient.publish({
      destination: "/app/private-message",
      body: JSON.stringify(chatMessage),
    });
    setPrivateMessages((prev) => [...prev, chatMessage]);

    setPrivateMsg("");

  };
  const loadHistory = async (user) => {
    const token = localStorage.getItem("token");
    try {
      const res = await fetch(`${backendHost}/messages/${username}/${user}`, {
      headers: {
        "Authorization": `Bearer ${token}`
      }});
      if (!res.ok) {
        console.error("❌ History API error:", res.status);
        return;
      }
      const history = await res.json();
      setPrivateMessages(history);
    } catch (err) {
      console.error("❌ Failed to load history:", err);
    }
  };

  return (
    <div className="chat-container">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-header">🟢 Online Users</div>

        <div className="online-list">
          {allUsers.map((user) => {const count = unreadCounts[user] || 0;
          return (
            
            <div
              key={user}
              onClick={() => {
                setSelectedUser(user);
                loadHistory(user);
                setUnreadCounts((prev) => ({ ...prev, [user]: 0 }));

              }}
               className="online-item"
      style={{ background: selectedUser === user ? "#e0e0e0" : undefined }}
    >
      <span>
        {user === username ? `You (${user})` : user}
      </span>

      <span style={{ display: "flex", alignItems: "center", gap: "6px" }}>
        {/* online dot */}
        <span
          style={{
            color: onlineUsers.includes(user) ? "#0ea5a0" : "#ccc",
            fontSize: 12,
          }}
        >
          ●
        </span>

        {/* 🔴 unread badge */}
        {count > 0 && (
          <span
            style={{
              backgroundColor: "#ef4444",
              color: "white",
              borderRadius: "999px",
              padding: "0 6px",
              fontSize: "11px",
              minWidth: "18px",
              textAlign: "center",
            }}
          >
            {count}
          </span>
        )}
      </span>
            </div>
          )})}
        </div>

        <div className="sidebar-footer">
          <div style={{ fontSize: 13, color: '#555', fontStyle: 'italic' }}>
            {userActivity && (userActivity.includes('joined') ? `🟢 ${userActivity}` : `🔴 ${userActivity}`)}
          </div>
        </div>
      </aside>
      

      {/* Main chat area */}
      <main className="main">
        <div className="topbar">
          <h2 style={{ margin: 0 }}>💬 Chat Room</h2>
          <div className="status-line">Status: {connected ? '🟢 Connected' : '🔴 Disconnected'} · Logged in as: <b>{username}</b></div>
          <button
  style={{ marginLeft: "20px" }}
  onClick={() => {
    localStorage.removeItem("username");
    localStorage.removeItem("token");
    window.location.reload();
  }}
>
  Logout
</button>
        </div>



        <div className="panels">
          <section className="panel">
            <div className="panel-header">Public Chat</div>
            <div className="messages-list">
              {publicMessages.map((msg, i) => (
                <div key={i} style={{ marginBottom: 8 }}>
                  {msg.type === 'CHAT' && (
                    <div>
                      <b>{msg.sender}:</b> {msg.content}
                    </div>
                  )}
                </div>
              ))}
            </div>
            <div className="panel-inputs">
              <input value={publicMsg} onChange={(e) => setPublicMsg(e.target.value)} placeholder="Type public message..." />
              <button onClick={sendPublicMessage}>Send</button>
            </div>
          </section>

          <section className="panel">
            <div className="panel-header">Private Chat</div>
            <div className="messages-list">
              {!selectedUser ? (
                <p style={{ color: "#888", textAlign: "center", marginTop: "40px" }}>
                  👈 Select a user from the left to start chatting
                </p>
              ) : (
                privateMessages.map((msg, i) => (
                  <div key={i} style={{ marginBottom: 8 }}>
                    <b>{msg.sender}:</b> {msg.content}
                  </div>
                ))
              )}
            </div>

            <div className="panel-inputs">
              <input value={selectedUser || ''} onChange={() => { }} placeholder="Receiver username" disabled style={{ width: 140 }} />
              <input value={privateMsg} onChange={(e) => setPrivateMsg(e.target.value)} placeholder="Type private message..." />
              <button
                disabled={!selectedUser}
                onClick={() => sendPrivateMessage(selectedUser)}
              >
                Send
              </button>

            </div>
          </section>
        </div>
      </main>
    </div>
  );
}
export default ChatRoom;
