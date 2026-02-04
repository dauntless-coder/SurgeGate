import React, { useState, useEffect } from "react";
import axios from "axios";
import { BrowserRouter as Router, Route, Routes, Link } from "react-router-dom";
import "./App.css";

const API_URL = "http://localhost:8080/api";

function App() {
  return (
    <Router>
      <div className="App">
        <nav>
          <Link to="/">Attendee</Link> | <Link to="/organizer">Organizer</Link> | <Link to="/staff">Staff</Link>
        </nav>
        <Routes>
          <Route path="/" element={<AttendeeView />} />
          <Route path="/organizer" element={<OrganizerView />} />
          <Route path="/staff" element={<StaffView />} />
        </Routes>
      </div>
    </Router>
  );
}

// --- 1. ORGANIZER VIEW ---
function OrganizerView() {
  const [event, setEvent] = useState({ name: "", venue: "", date: "" });
  const [ticket, setTicket] = useState({ typeName: "General", price: 0, totalAllocation: 100 });

  const createEvent = async () => {
    const payload = { ...event, ticketTypes: [ticket] };
    await axios.post(`${API_URL}/events`, payload);
    alert("Event Created & Stock Loaded to Redis!");
  };

  return (
    <div className="card">
      <h2>Organizer Dashboard</h2>
      <input placeholder="Event Name" onChange={(e) => setEvent({ ...event, name: e.target.value })} />
      <input placeholder="Venue" onChange={(e) => setEvent({ ...event, venue: e.target.value })} />
      <div className="ticket-box">
        <h4>Ticket Config</h4>
        <input placeholder="Type (e.g. VIP)" onChange={(e) => setTicket({ ...ticket, typeName: e.target.value })} />
        <input placeholder="Qty" type="number" onChange={(e) => setTicket({ ...ticket, totalAllocation: e.target.value })} />
      </div>
      <button onClick={createEvent}>Create Event</button>
    </div>
  );
}

// --- 2. ATTENDEE VIEW ---
function AttendeeView() {
  const [events, setEvents] = useState([]);

  useEffect(() => {
    axios.get(`${API_URL}/events`).then((res) => setEvents(res.data));
  }, []);

  const buyTicket = async (eventId, type) => {
    try {
      const res = await axios.post(`${API_URL}/buy`, {
        eventId, ticketType: type, userId: "User_" + Math.floor(Math.random() * 1000)
      });
      alert(`Success! Ticket ID: ${res.data}`);
    } catch (err) {
      alert(err.response.data);
    }
  };

  return (
    <div className="card">
      <h2>Upcoming Events</h2>
      {events.map((ev) => (
        <div key={ev.id} className="event-row">
          <h3>{ev.name}</h3>
          <p>{ev.venue}</p>
          {ev.ticketTypes.map((t) => (
            <button key={t.typeName} onClick={() => buyTicket(ev.id, t.typeName)}>
              Buy {t.typeName} (${t.price})
            </button>
          ))}
        </div>
      ))}
    </div>
  );
}

// --- 3. STAFF VIEW ---
function StaffView() {
  const [ticketId, setTicketId] = useState("");
  const [status, setStatus] = useState("");

  const scanTicket = async () => {
    try {
      const res = await axios.post(`${API_URL}/validate/${ticketId}`);
      setStatus(res.data);
    } catch (err) {
      setStatus(err.response.data);
    }
  };

  return (
    <div className="card">
      <h2>Staff Gate Scanner</h2>
      <input placeholder="Enter Ticket ID" onChange={(e) => setTicketId(e.target.value)} />
      <button onClick={scanTicket}>Validate Entry</button>
      <h1>{status}</h1>
    </div>
  );
}

export default App;