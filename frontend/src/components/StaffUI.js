import React, { useState } from 'react';
import axios from 'axios';

function StaffUI() {
  const [staffId] = useState('staff_' + Math.floor(Math.random() * 1000));
  const [orderId, setOrderId] = useState('');
  const [validationResult, setValidationResult] = useState(null);
  const [validationHistory, setValidationHistory] = useState([]);

  const handleValidateOrder = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post('http://localhost:8080/api/staff/validate', null, {
        params: {
          orderId,
          staffId
        }
      });
      setValidationResult({ success: true, data: res.data });
      setValidationHistory([...validationHistory, { orderId, ...res.data, timestamp: new Date() }]);
      setOrderId('');
    } catch (err) {
      setValidationResult({
        success: false,
        data: err.response?.data || { message: err.message }
      });
    }
  };

  return (
    <div className="staff-section">
      <h2>✓ Staff Ticket Validator</h2>
      <p>Staff ID: <strong>{staffId}</strong></p>

      <form onSubmit={handleValidateOrder} className="validation-form">
        <input
          type="text"
          placeholder="Enter Order ID for validation"
          value={orderId}
          onChange={(e) => setOrderId(e.target.value)}
          required
        />
        <button type="submit" className="btn-validate">✓ Validate Order</button>
      </form>

      {validationResult && (
        <div className={`validation-result ${validationResult.success ? 'success' : 'error'}`}>
          <h3>{validationResult.success ? '✅ VALID' : '❌ INVALID'}</h3>
          <p><strong>Status:</strong> {validationResult.data.status}</p>
          <p><strong>Message:</strong> {validationResult.data.message}</p>
          {validationResult.data.orderId && (
            <p><strong>Order ID:</strong> {validationResult.data.orderId}</p>
          )}
        </div>
      )}

      {validationHistory.length > 0 && (
        <div className="validation-history">
          <h3>📜 Validation History</h3>
          {validationHistory.map((entry, idx) => (
            <div key={idx} className="history-entry">
              <p><strong>Order ID:</strong> {entry.orderId}</p>
              <p><strong>Status:</strong> {entry.status}</p>
              <p><strong>Time:</strong> {entry.timestamp.toLocaleTimeString()}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default StaffUI;
