import React, { useState } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [status, setStatus] = useState("IDLE");
  const [orderId, setOrderId] = useState("");

  const handleBuy = async () => {
    setStatus("PROCESSING");
    try {
      // 1. Request Purchase
      const userId = "user_" + Math.floor(Math.random() * 1000);
      const res = await axios.post(`http://localhost:8080/api/buy?userId=${userId}`);
      setOrderId(res.data);
      
      // 2. Start Polling for Confirmation
      pollStatus(res.data);
    } catch (err) {
      if (err.response && err.response.data === "SOLD_OUT") {
        setStatus("SOLD_OUT");
      } else {
        setStatus("ERROR");
      }
    }
  };

  const pollStatus = (id) => {
    const interval = setInterval(async () => {
      try {
        const res = await axios.get(`http://localhost:8080/api/status/${id}`);
        if (res.data === "CONFIRMED") {
          setStatus("SUCCESS");
          clearInterval(interval);
        }
      } catch (e) { console.error(e); }
    }, 1000); 
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>⚡ SurgeGate Tickets</h1>
        {status === "IDLE" && <button onClick={handleBuy}>BUY TICKET</button>}
        {status === "PROCESSING" && <p>Processing...</p>}
        {status === "SUCCESS" && <p style={{color: "lime"}}>✅ Ticket Confirmed! ID: {orderId}</p>}
        {status === "SOLD_OUT" && <p style={{color: "red"}}>❌ SOLD OUT</p>}
      </header>
    </div>
  );
}

export default App;