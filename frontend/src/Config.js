// src/config.js
const backendHost =
  process.env.REACT_APP_BACKEND_URL ||
  `${window.location.protocol}//${window.location.hostname}:8080`;

export default backendHost;
