import React, { useState } from "react";
import backendHost from "./Config.js";

function SigninPage({ onLoginSuccess }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleSignin = async () => {
    const res = await fetch(`${backendHost}/api/auth/signin`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });
    const text = await res.text();
    setMsg(text);
    if (text.includes("✅")) {
      onLoginSuccess(username);
    }
  };

  return (
    <div style={styles.container}>
      <h2>🔐 Login</h2>
      <input
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        style={styles.input}
      />
            <div style={{ display: "flex", alignItems: "center" }}>

      <input type={showPassword ? "text" : "password"}
    placeholder="Password"
    value={password}
    onChange={(e) => setPassword(e.target.value)}
    style={{ ...styles.input, width: "200px" }}
  />

  <button
    onMouseDown={() => setShowPassword(true)}
  onMouseUp={() => setShowPassword(false)}
  onMouseLeave={() => setShowPassword(false)}  // in case cursor leaves button
  style={{
    marginLeft: "-40px",
    background: "none",
    border: "none",
    cursor: "pointer",
    fontSize: "18px"
  }}
>
  👁
  </button>
  </div>
      <button onClick={handleSignin} style={styles.button}>
        Sign In
      </button>
      {msg && <p>{msg}</p>}
    </div>
  );
}

const styles = {
  container: {
    display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center",
    minHeight: "calc(100vh - 80px)", // leave space for the app's bottom controls so no page scroll
    paddingBottom: 20,
    boxSizing: 'border-box',
    fontFamily: "Arial"
  },
  input: {
    padding: "10px", margin: "8px", width: "200px", borderRadius: "5px", border: "1px solid #ccc"
  },
  button: {
    padding: "10px 20px", marginTop: "10px", backgroundColor: "#2563eb", color: "#fff",
    border: "none", borderRadius: "5px", cursor: "pointer"
  }
};

export default SigninPage;
