import React, { useState, useEffect } from 'react';
import axios from 'axios';

function AttendeeUI({ userId }) {
  const [concerts, setConcerts] = useState([]);
  const [userOrders, setUserOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedConcert, setSelectedConcert] = useState(null);
  const [editingId, setEditingId] = useState(false);
  const [newUserId, setNewUserId] = useState(userId);

  useEffect(() => {
    loadConcerts();
    loadUserOrders();
  }, []);

  const loadConcerts = async () => {
    setLoading(true);
    try {
      const res = await axios.get('http://localhost:8080/api/attendee/concerts');
      setConcerts(res.data);
    } catch (err) {
      alert('❌ Error loading concerts: ' + err.message);
    }
    setLoading(false);
  };

  const loadUserOrders = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/api/attendee/orders/${userId}`);
      setUserOrders(res.data);
    } catch (err) {
      console.error('Error loading orders:', err);
    }
  };

  const handleBuyTicket = async (concertId) => {
    try {
      const res = await axios.post('http://localhost:8080/api/attendee/buy', null, {
        params: {
          concertId,
          userId
        }
      });
      const ticketCode = res.data.ticketCode;
      alert('✅ Ticket purchased!\n\n' +
            'Order ID: ' + res.data.orderId + '\n' +
            'Ticket Code: ' + ticketCode + '\n\n' +
            '🔑 Share this code with staff for validation: ' + ticketCode);
      loadConcerts();
      loadUserOrders();
    } catch (err) {
      if (err.response?.data?.error === 'SOLD_OUT') {
        alert('❌ SOLD OUT! No tickets available.');
      } else {
        alert('❌ Error: ' + (err.response?.data?.error || err.message));
      }
    }
  };

  const handleUpdateUserId = () => {
    if (!newUserId.trim()) {
      alert('❌ Please enter a valid User ID');
      return;
    }
    localStorage.setItem('attendeeUserId', newUserId);
    alert('✅ User ID updated to: ' + newUserId + '\n\nPlease refresh to continue with the new ID');
    window.location.reload();
  };

  return (
    <div className="attendee-section">
      <h2>🎫 Attendee Ticker Marketplace</h2>
      <div className="user-id-section">
        <p>Your ID: <strong>{userId}</strong></p>
        <button onClick={() => setEditingId(!editingId)} className="btn-warning">
          {editingId ? '✕ Cancel' : '✏️ Edit ID'}
        </button>
      </div>

      {editingId && (
        <div className="edit-id-form">
          <input
            type="text"
            placeholder="Enter new User ID"
            value={newUserId}
            onChange={(e) => setNewUserId(e.target.value)}
          />
          <button onClick={handleUpdateUserId} className="btn-success">Update ID</button>
        </div>
      )}

      <button onClick={loadConcerts} className="btn-secondary">🔄 Refresh Concerts</button>

      {loading ? (
        <p>Loading concerts...</p>
      ) : (
        <div className="concerts-grid">
          <h3>Available Concerts</h3>
          {concerts.length === 0 ? (
            <p>No concerts available right now.</p>
          ) : (
            concerts.map(concert => (
              <div key={concert.id} className="concert-card-attendee">
                <h4>{concert.title}</h4>
                <p>📍 {concert.venue}</p>
                <p>💰 ${concert.price}</p>
                <p className={concert.availableTickets > 0 ? 'available' : 'sold-out'}>
                  Available: {concert.availableTickets} / {concert.totalTickets}
                </p>
                <button
                  onClick={() => handleBuyTicket(concert.id)}
                  disabled={concert.availableTickets <= 0}
                  className="btn-buy"
                >
                  {concert.availableTickets > 0 ? '🛒 Buy Now' : 'SOLD OUT'}
                </button>
              </div>
            ))
          )}
        </div>
      )}

      {userOrders.length > 0 && (
        <div className="orders-section">
          <h3>📋 Your Purchases</h3>
          {userOrders.map(order => (
            <div key={order.id} className="order-card">
              <p><strong>Order ID:</strong> {order.id}</p>
              <p><strong>Status:</strong> {order.status}</p>
              <p><strong>Amount:</strong> ${order.amount}</p>
              <p><strong>Purchased:</strong> {new Date(order.createdAt).toLocaleString()}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default AttendeeUI;
