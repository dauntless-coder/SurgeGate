import React, { useState } from 'react';
import axios from 'axios';

function LoadTester() {
  const [concertId, setConcertId] = useState('');
  const [numUsers, setNumUsers] = useState(10);
  const [isRunning, setIsRunning] = useState(false);
  const [results, setResults] = useState(null);
  const [progress, setProgress] = useState(0);

  const handleLoadTest = async () => {
    if (!concertId.trim()) {
      alert('Please enter a Concert ID first. Create a concert as an Organizer.');
      return;
    }

    setIsRunning(true);
    setResults(null);
    setProgress(0);

    try {
      const startTime = Date.now();
      const promises = [];
      const results = { success: 0, failed: 0, errors: [] };

      // Simulate thundering herd: All users try to buy at the same time
      for (let i = 0; i < numUsers; i++) {
        const userPromise = (async () => {
          try {
            const userId = `stress_user_${i}_${Date.now()}`;
            const res = await axios.post('http://localhost:8080/api/attendee/buy', null, {
              params: {
                concertId,
                userId
              }
            });
            results.success++;
          } catch (err) {
            results.failed++;
            results.errors.push(err.response?.data?.error || err.message);
          }
          setProgress(prev => prev + 1);
        })();
        promises.push(userPromise);
      }

      await Promise.all(promises);

      const endTime = Date.now();
      const duration = (endTime - startTime) / 1000;

      setResults({
        success: results.success,
        failed: results.failed,
        totalRequests: numUsers,
        duration: duration.toFixed(2),
        requestsPerSecond: ((numUsers / duration).toFixed(2)),
        errors: results.errors.slice(0, 5) // Show first 5 errors
      });
    } catch (err) {
      alert('❌ Load test failed: ' + err.message);
    } finally {
      setIsRunning(false);
    }
  };

  return (
    <div className="load-tester">
      <h2>⚙️ Concurrency Load Tester - Thundering Herd Simulation</h2>
      
      <div className="test-controls">
        <input
          type="text"
          placeholder="Enter Concert ID"
          value={concertId}
          onChange={(e) => setConcertId(e.target.value)}
          disabled={isRunning}
        />
        
        <div className="num-users-input">
          <label>Number of Concurrent Users:</label>
          <input
            type="number"
            min="1"
            max="1000"
            value={numUsers}
            onChange={(e) => setNumUsers(parseInt(e.target.value))}
            disabled={isRunning}
          />
        </div>

        <button
          onClick={handleLoadTest}
          disabled={isRunning}
          className="btn-test"
        >
          {isRunning ? `Testing... ${progress}/${numUsers}` : '🚀 Start Load Test'}
        </button>
      </div>

      {isRunning && (
        <div className="progress-bar-container">
          <div className="progress-bar" style={{ width: `${(progress / numUsers) * 100}%` }}></div>
          <p>{progress} / {numUsers} requests completed</p>
        </div>
      )}

      {results && (
        <div className="test-results">
          <h3>📊 Test Results</h3>
          
          <div className="result-card success">
            <h4>✅ Successful Purchases</h4>
            <p className="large-number">{results.success}</p>
          </div>

          <div className="result-card error">
            <h4>❌ Failed Requests</h4>
            <p className="large-number">{results.failed}</p>
          </div>

          <div className="result-metrics">
            <p><strong>Total Requests:</strong> {results.totalRequests}</p>
            <p><strong>Duration:</strong> {results.duration}s</p>
            <p><strong>Throughput:</strong> {results.requestsPerSecond} requests/sec</p>
            <p><strong>Success Rate:</strong> {(((results.success) / results.totalRequests) * 100).toFixed(2)}%</p>
          </div>

          {results.errors.length > 0 && (
            <div className="errors-list">
              <h4>Common Errors:</h4>
              {results.errors.map((error, idx) => (
                <p key={idx}>• {error}</p>
              ))}
            </div>
          )}

          <div className="explanation">
            <h4>🔍 What This Demonstrates:</h4>
            <ul>
              <li><strong>Thundering Herd Problem:</strong> Multiple concurrent requests for limited resources</li>
              <li><strong>Race Condition Handling:</strong> Redis locks prevent overbooking</li>
              <li><strong>Atomic Operations:</strong> Stock deduction is synchronized</li>
              <li><strong>High Concurrency:</strong> The system handles many simultaneous requests safely</li>
            </ul>
          </div>
        </div>
      )}
    </div>
  );
}

export default LoadTester;
