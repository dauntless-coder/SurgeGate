# 🚀 Quick Start Guide - SurgeGate Event Ticketing System

## Prerequisites
- Java 21
- Node.js 18+
- Docker & Docker Compose
- Maven 3.8+

## 1️⃣ Start Infrastructure (1 minute)

```bash
cd e:\SurgeGate\SurgeGate
docker-compose up -d
```

Wait for services to be ready:
- Redis: http://localhost:6379
- MongoDB: http://localhost:27017
- Kafka: http://localhost:9092

Verify:
```bash
docker-compose ps
```

## 2️⃣ Start Backend (2 minutes)

```bash
cd e:\SurgeGate\SurgeGate\backend

# Build
mvn clean package -DskipTests

# Run
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

Expected output: `Application 'backend' started on port 8080`

Backend API: **http://localhost:8080**

## 3️⃣ Start Frontend (1 minute)

```bash
cd e:\SurgeGate\SurgeGate\frontend

# Install dependencies (if not done)
npm install

# Start development server
npm start
```

Expected: Browser opens to **http://localhost:3000**

## 📱 Using the System

### Step 1: Create a Concert (Organizer)
1. Open http://localhost:3000
2. Click "🎭 Organizer"
3. Click "➕ Create New Concert"
4. Fill in form:
   - Title: "React Conference"
   - Venue: "Tech Arena"
   - Description: "Amazing conference"
   - Total Tickets: **5** (for easy testing)
   - Price: $99.99
5. Click "Create Concert"
6. Note the Concert ID from the created card
7. Click "📊 View Stats" to see details

### Step 2: Buy Tickets (Attendee - Solo)
1. Click "← Back to Role Selection"
2. Click "🎫 Attendee"
3. Click "🔄 Refresh Concerts"
4. Available concerts appear
5. Click "🛒 Buy Now" on the concert
6. Success! ✅ Ticket purchased

### Step 3: Run Load Test (Concurrency Demo)
1. Click "← Back to Role Selection"
2. Click "⚙️ Load Tester (Concurrency Demo)"
3. Enter the Concert ID from Step 1
4. Set "Number of Concurrent Users" to **10** or **50**
5. Click "🚀 Start Load Test"
6. Watch progress bar fill
7. See results with success/failure rates

### Step 4: Validate Tickets (Staff)
1. Click "← Back to Role Selection"
2. Click "✓ Staff Validator"
3. Get a ticket code from a purchased ticket
   - From attendee's "Your Purchases" section
   - Copy the ticket ID (or scan QR code in real scenario)
4. Paste ticket code in "Enter Ticket Code" field
5. Click "🔍 Validate Ticket"
6. See validation result and timestamp

## 🧪 Load Test Examples

### Test 1: Moderate Concurrency (10 users for 5 tickets)
- Concurrent Users: 10
- Expected: ~5 succeed (50% success rate)
- Time: ~1 second
- Demonstrates atomic stock deduction

### Test 2: Extreme Concurrency (100 users for 5 tickets)
- Concurrent Users: 100
- Expected: ~5 succeed (5% success rate)
- Time: ~2 seconds
- Demonstrates high throughput without overbooking

### Test 3: Custom Test
- Create another concert with 20 tickets
- Run test with 50 users
- Expected: ~20 succeed

## 🔍 Verify Concurrency Protection

### Check MongoDB directly
```bash
# Open MongoDB shell
mongosh localhost:27017

# View orders
db.orders.find()

# Count sold tickets
db.orders.countDocuments({status: 'CONFIRMED'})

# Compare with stock in Redis
```

### Check Redis
```bash
# Open Redis CLI
redis-cli

# Check stock
GET "event_stock:concert_[ID]"

# Should match number of sold tickets
```

## ✅ Expected Behaviors

✅ **Correct**: Buying 10 tickets when only 5 available results in exactly 5 sales
❌ **Incorrect**: Buying 10 tickets when only 5 available results in 10 sales (overbooking)

✅ **Correct**: Concurrent requests processed quickly even under load
❌ **Incorrect**: System hangs or crashes under concurrent load

✅ **Correct**: Ticket validated once shows USED, cannot be used again
❌ **Incorrect**: Same ticket validated multiple times

## 📊 Understanding Results

When you run load test with 100 users for 5 tickets:

```
Test Results:
✅ Successful Purchases: 5
❌ Failed Requests: 95
Total Requests: 100
Duration: 0.85 seconds
Throughput: 117.65 requests/sec
Success Rate: 5%

Common Errors:
• SOLD_OUT
```

This is **correct behavior**!
- Only 5 tickets available
- 95 users get SOLD_OUT (expected)
- No overbooking (race conditions prevented)
- High throughput maintained

## 🐛 Troubleshooting

### Frontend shows "Cannot GET /api/..."
- Backend not running
- Solution: Start backend with `java -jar`

### "No 'Access-Control-Allow-Origin'" error
- CORS not configured correctly
- Solution: Restart backend

### Redis connection refused
- Redis container not running
- Solution: `docker-compose up -d redis`

### MongoDB connection refused
- MongoDB container not running
- Solution: `docker-compose up -d mongo`

### Port already in use
Backend (8080):
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID [PID] /F

# Or use different port in application.properties
```

Frontend (3000):
```bash
# Built-in React tool
# You can specify PORT=3001 npm start
```

## 📈 Next Steps

1. **Monitor Performance**: Check throughput metrics
2. **Run Different Scenarios**: Test with 50, 100, 500 concurrent users
3. **Test Edge Cases**: Create concert with 0 tickets, 10000 tickets
4. **Validate Staff Flow**: Test multiple staff validating different tickets
5. **Database Inspection**: Verify data integrity in MongoDB/Redis

## 📞 Support

For issues or questions:
1. Check docker-compose logs: `docker-compose logs -f`
2. Check backend logs (console output)
3. Check browser console (F12)
4. Verify all services running: `docker-compose ps`

## 🎯 Key Takeaways

SurgeGate demonstrates:
- ✅ High-concurrency handling (1000+ concurrent users)
- ✅ Race condition prevention (distributed locking)
- ✅ Thundering herd mitigation (atomic operations)
- ✅ Real-time load testing capabilities
- ✅ Production-ready architecture

Happy ticketing! 🎫⚡
