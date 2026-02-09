import React, { useState } from 'react';
import axios from 'axios';

function OrganizerUI() {
  const [organizerId, setOrganzerId] = useState('org_' + Math.floor(Math.random() * 1000));
  const [tempOrganzerId, setTempOrganzerId] = useState(organizerId);
  const [concerts, setConcerts] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingOrganzerId, setEditingOrganzerId] = useState(false);
  const [editingConcertId, setEditingConcertId] = useState(null);
  const [formData, setFormData] = useState({
    title: 'React Conference 2026',
    venue: 'Tech Arena',
    description: 'A comprehensive React event',
    totalTickets: 5,
    price: 99.99
  });
  const [selectedConcertStats, setSelectedConcertStats] = useState(null);

  const handleCreateConcert = async (e) => {
    e.preventDefault();
    try {
      if (editingConcertId) {
        // Update existing concert
        const params = {};
        if (formData.title) params.title = formData.title;
        if (formData.venue) params.venue = formData.venue;
        if (formData.description) params.description = formData.description;
        if (formData.price) params.price = parseFloat(formData.price);
        
        const res = await axios.put(
          `http://localhost:8080/api/organizer/concert/${editingConcertId}`,
          null,
          { params }
        );
        
        setConcerts(concerts.map(c => c.id === editingConcertId ? res.data : c));
        setEditingConcertId(null);
        alert('✅ Concert updated successfully!');
      } else {
        // Create new concert
        const res = await axios.post('http://localhost:8080/api/organizer/concert', null, {
          params: {
            organizerId,
            title: formData.title,
            venue: formData.venue,
            description: formData.description,
            totalTickets: parseInt(formData.totalTickets),
            price: parseFloat(formData.price)
          }
        });
        
        setConcerts([...concerts, res.data]);
        alert('✅ Concert created successfully!\n\n' +
              'Concert ID: ' + res.data.id + '\n\n' +
              '🆔 Share this ID for the Load Tester or with attendees');
      }
      
      setShowForm(false);
      setFormData({
        title: 'React Conference 2026',
        venue: 'Tech Arena',
        description: 'A comprehensive React event',
        totalTickets: 5,
        price: 99.99
      });
    } catch (err) {
      alert('❌ Error: ' + (err.response?.data || err.message));
    }
  };

  const handleEditConcert = (concert) => {
    setEditingConcertId(concert.id);
    setFormData({
      title: concert.title,
      venue: concert.venue,
      description: concert.description,
      totalTickets: concert.totalTickets,
      price: concert.price
    });
    setShowForm(true);
  };

  const handleDeleteConcert = async (concertId) => {
    if (!window.confirm('Are you sure? This action cannot be undone!')) {
      return;
    }
    
    try {
      await axios.delete(`http://localhost:8080/api/organizer/concert/${concertId}`);
      setConcerts(concerts.filter(c => c.id !== concertId));
      alert('✅ Concert deleted successfully!');
    } catch (err) {
      alert('❌ Error: ' + (err.response?.data || err.message));
    }
  };



  const handleGetStats = async (concertId) => {
    try {
      const res = await axios.get(`http://localhost:8080/api/organizer/concert/${concertId}/stats`);
      setSelectedConcertStats(res.data);
    } catch (err) {
      alert('❌ Error fetching stats: ' + err.message);
    }
  };

  const handleLoadConcerts = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/api/organizer/concerts/${organizerId}`);
      setConcerts(res.data);
    } catch (err) {
      alert('❌ Error loading concerts: ' + err.message);
    }
  };

  const handleSaveOrganzerId = async () => {
    if (!tempOrganzerId.trim()) {
      alert('❌ Organizer ID cannot be empty!');
      return;
    }
    
    setOrganzerId(tempOrganzerId);
    setEditingOrganzerId(false);
    setConcerts([]);
    alert('✅ Organizer ID updated! Load concerts to view your events.');
  };

  return (
    <div className="organizer-section">
      <h2>🎭 Organizer Dashboard</h2>
      
      <div className="organizer-id-section">
        {!editingOrganzerId ? (
          <div>
            <p>Organizer ID: <strong>{organizerId}</strong></p>
            <button onClick={() => setEditingOrganzerId(true)} className="btn-secondary">
              ✏️ Edit Organizer ID
            </button>
          </div>
        ) : (
          <div className="organizer-id-edit">
            <input
              type="text"
              value={tempOrganzerId}
              onChange={(e) => setTempOrganzerId(e.target.value)}
              placeholder="Enter your organizer ID"
              autoFocus
            />
            <button onClick={handleSaveOrganzerId} className="btn-success">
              💾 Save
            </button>
            <button onClick={() => {
              setTempOrganzerId(organizerId);
              setEditingOrganzerId(false);
            }} className="btn-secondary">
              ❌ Cancel
            </button>
          </div>
        )}
      </div>

      <button onClick={() => setShowForm(!showForm)} className="btn-primary">
        {showForm ? 'Cancel' : '➕ Create New Concert'}
      </button>

      {showForm && (
        <form onSubmit={handleCreateConcert} className="form-container">
          {editingConcertId && <h4>✏️ Edit Concert</h4>}
          <input
            type="text"
            placeholder="Concert Title"
            value={formData.title}
            onChange={(e) => setFormData({...formData, title: e.target.value})}
            required
          />
          <input
            type="text"
            placeholder="Venue"
            value={formData.venue}
            onChange={(e) => setFormData({...formData, venue: e.target.value})}
            required
          />
          <textarea
            placeholder="Description"
            value={formData.description}
            onChange={(e) => setFormData({...formData, description: e.target.value})}
          />
          {!editingConcertId && (
            <input
              type="number"
              placeholder="Total Tickets"
              value={formData.totalTickets}
              onChange={(e) => setFormData({...formData, totalTickets: e.target.value})}
              required
            />
          )}
          <input
            type="number"
            placeholder="Price"
            step="0.01"
            value={formData.price}
            onChange={(e) => setFormData({...formData, price: e.target.value})}
            required
          />
          <button type="submit" className="btn-success">
            {editingConcertId ? 'Update Concert' : 'Create Concert'}
          </button>
        </form>
      )}

      <button onClick={handleLoadConcerts} className="btn-secondary">🔄 Load My Concerts</button>

      {concerts.length > 0 && (
        <div className="concerts-list">
          <h3>Your Concerts</h3>
          {concerts.map(concert => (
            <div key={concert.id} className="concert-card">
              <h4>{concert.title}</h4>
              <p className="concert-id">🆔 ID: <code>{concert.id}</code></p>
              <p>📍 {concert.venue}</p>
              <p>🎫 {concert.totalTickets} tickets @ ${concert.price}</p>
              <p>Status: {concert.status}</p>
              <div className="concert-buttons">
                <button onClick={() => handleGetStats(concert.id)} className="btn-info">
                  📊 Stats
                </button>
                <button onClick={() => handleEditConcert(concert)} className="btn-warning">
                  ✏️ Edit
                </button>
                <button onClick={() => handleDeleteConcert(concert.id)} className="btn-danger">
                  🗑️ Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {selectedConcertStats && (
        <div className="stats-panel">
          <h3>📊 Concert Statistics</h3>
          <p><strong>Concert:</strong> {selectedConcertStats.title}</p>
          <p><strong>Total Tickets:</strong> {selectedConcertStats.totalTickets}</p>
          <p><strong>Sold:</strong> {Math.max(0, selectedConcertStats.soldTickets)} ✅</p>
          <p><strong>Available:</strong> {Math.max(0, selectedConcertStats.availableTickets)} 🔓</p>
          <p><strong>Revenue:</strong> ${(Math.max(0, selectedConcertStats.soldTickets) * Math.max(0, selectedConcertStats.availableTickets === 0 ? selectedConcertStats.revenue / selectedConcertStats.totalTickets : 1)).toFixed(2)}</p>
        </div>
      )}
    </div>
  );
}

export default OrganizerUI;
