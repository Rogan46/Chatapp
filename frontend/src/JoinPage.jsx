// import React, { useState } from "react";

// function JoinPage({ onJoin }) {
//   const [username, setUsername] = useState("");

  
//   const handleJoin = () => {
//     if (username.trim() !== "") {
//       onJoin(username.trim());
//     } else {
//       alert("Please enter a username");
//     }
//   };

//   return (
//     <div style={{
//       display: "flex",
//       flexDirection: "column",
//       alignItems: "center",
//       justifyContent: "center",
//       height: "100vh",
//       fontFamily: "Arial"
//     }}>
//       <h2>💬 Welcome to ChatApp</h2>
//       <input
//         type="text"
//         value={username}
//         placeholder="Enter your username"
//         onChange={(e) => setUsername(e.target.value)}
//         style={{ padding: "10px", marginBottom: "15px", width: "220px" }}
//       />
//       <button
//         onClick={handleJoin}
//         style={{
//           padding: "10px 20px",
//           backgroundColor: "#007bff",
//           color: "white",
//           border: "none",
//           borderRadius: "5px",
//           cursor: "pointer"
//         }}
//       >
//         Enter Chat
//       </button>
//     </div>
//   );
// }

// export default JoinPage;
