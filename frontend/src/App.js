import { useEffect, useState } from "react";
import {jwtDecode} from "jwt-decode";
import ChatRoom from "./ChatRoom";
import SignupPage from "./SignupPage";
import SigninPage from "./SigninPage";

function App() {
  const [username, setUsername] = useState("");
  const [page, setPage] = useState("signin");

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    setUsername("");
    setPage("signin");
  };

  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    const storedUsername = localStorage.getItem("username");

    if (storedToken) {
      try {
        const decoded = jwtDecode(storedToken); // {exp:..., sub:...}
        const now = Date.now() / 1000;

        if (decoded.exp < now) {
          console.log("⏳ Token expired, logout");
          handleLogout();
        } else {
          setUsername(storedUsername);
          setPage("chat");
        }
      } catch (err) {
        console.error("❌ Invalid token");
        handleLogout();
      }
    }
  }, []);

  return (
    <>
      {page === "signup" && (
        <SignupPage onSignupSuccess={() => setPage("signin")} />
      )}

      {page === "signin" && (
        <SigninPage
          onLoginSuccess={(username) => {
            setUsername(username);
            setPage("chat");
          }}
        />
      )}

      {page === "chat" && (
        <ChatRoom username={username} onLogout={handleLogout} />
      )}
      {/* bottom link to switch between signup and signin when not in chat */}
      {page !== "chat" && (
        <div
          style={{
            textAlign: "center",
            marginTop: "20px",
            fontFamily: "Arial",
            fontSize: "14px",
          }}
        >
          {page === "signin" ? (
            <>
              Don’t have an account?{" "}
              <button
                style={{
                  border: "none",
                  background: "none",
                  color: "#2563eb",
                  cursor: "pointer",
                  textDecoration: "underline",
                }}
                onClick={() => setPage("signup")}
              >
                Sign Up
              </button>
            </>
          ) : (
            <>
              Already have an account?{" "}
              <button
                style={{
                  border: "none",
                  background: "none",
                  color: "#2563eb",
                  cursor: "pointer",
                  textDecoration: "underline",
                }}
                onClick={() => setPage("signin")}
              >
                Sign In
              </button>
            </>
          )}
        </div>
      )}
    </>
  );
}

export default App;
