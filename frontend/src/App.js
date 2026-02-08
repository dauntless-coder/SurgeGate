import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import OrganizerUI from './components/OrganizerUI';
import AttendeeUI from './components/AttendeeUI';
import StaffUI from './components/StaffUI';
import LoadTester from './components/LoadTester';

function App() {
  const [role, setRole] = useState(null);
  const [userId, setUserId] = useState('');
  const [editingUserId, setEditingUserId] = useState('');
  const [showLoadTester, setShowLoadTester] = useState(false);

  useEffect(() => {
    // Try to load userId from localStorage, otherwise generate
    const savedUserId = localStorage.getItem('attendeeUserId');
    if (savedUserId) {
      setUserId(savedUserId);
      setEditingUserId(savedUserId);
    } else {
      const id = 'user_' + Math.floor(Math.random() * 100000);
      setUserId(id);
      setEditingUserId(id);
      localStorage.setItem('attendeeUserId', id);
    }
  }, []);

  const handleSetAttendeeId = () => {
    if (!editingUserId.trim()) {
      alert('❌ Please enter a valid User ID');
      return;
    }
    setUserId(editingUserId);
    localStorage.setItem('attendeeUserId', editingUserId);
    alert('✅ User ID updated: ' + editingUserId);
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>⚡ SurgeGate - High Concurrency Event Ticketing System</h1>
        
        {!role ? (
          <div className="role-selection">
            <h2>Select Your Role</h2>
            <div className="role-buttons">
              <button className="btn-organizer" onClick={() => setRole('organizer')}>
                🎭 Organizer
              </button>
              <button className="btn-attendee" onClick={() => setRole('attendee')}>
                🎫 Attendee
              </button>
              <button className="btn-staff" onClick={() => setRole('staff')}>
                ✓ Staff Validator
              </button>
            </div>
            <button className="btn-load-test" onClick={() => setShowLoadTester(true)}>
              ⚙️ Load Tester (Concurrency Demo)
            </button>
          </div>
        ) : role === 'attendee' && !editingUserId.trim() ? (
          <div className="role-selection">
            <h2>🎫 Create Your Attendee ID</h2>
            <p>Enter a unique ID to use for all your ticket purchases</p>
            <input
              type="text"
              placeholder="e.g., john_doe_123"
              value={editingUserId}
              onChange={(e) => setEditingUserId(e.target.value)}
              className="user-id-input"
            />
            <button className="btn-success" onClick={handleSetAttendeeId}>
              Create ID & Continue
            </button>
            <button className="btn-back" onClick={() => {
              setRole(null);
              setEditingUserId(userId);
            }}>
              ← Back
            </button>
          </div>
        ) : (
          <div className="role-section">
            <button className="btn-back" onClick={() => setRole(null)}>
              ← Back to Role Selection
            </button>
            
            {role === 'organizer' && <OrganizerUI />}
            {role === 'attendee' && <AttendeeUI userId={userId} />}
            {role === 'staff' && <StaffUI />}
          </div>
        )}
      </header>

      {showLoadTester && (
        <div className="modal-overlay">
          <div className="modal">
            <button className="modal-close" onClick={() => setShowLoadTester(false)}>✕</button>
            <LoadTester />
          </div>
        </div>
      )}
    </div>
  );
}

export default App;