import { useState } from 'react';
import ChatRoom from './ChatRoom';
import SignupPage from './SignupPage';
import SigninPage from './SigninPage';

function App() {
  const [username, setUsername] = useState("");
  const [page, setPage] = useState("signin"); // signin | signup | chat

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

      {page === "chat" && <ChatRoom username={username} />}

      {/* bottom link to switch between signup and signin */}
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
