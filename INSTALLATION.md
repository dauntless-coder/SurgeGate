# 📦 Installation Guide

## Prerequisites

- Java 21+ (`java -version`)
- Node.js 18+ (`node --version`)
- Maven 3.8+ (`mvn --version`)
- Docker & Docker Compose (`docker --version`)

## Installation Steps

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/SurgeGate.git
cd SurgeGate
```

### 2. Start Docker Containers (MongoDB, Redis, Kafka, Zookeeper)
```bash
docker-compose up -d
```

Wait 30-60 seconds, then verify all services are running:
```bash
docker-compose ps
```

Expected output:
```
NAME              STATUS
zookeeper         Up (healthy)
kafka             Up (healthy)
redis             Up (healthy)
mongodb           Up (healthy)
```

### 3. Start Backend (Spring Boot)
```bash
cd backend

# Build
mvn clean package -DskipTests

# Run
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

Watch for: `Application 'backend' started on port 8080`

Backend API: **http://localhost:8080**

### 4. Start Frontend (React) - New Terminal
```bash
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm start
```

Frontend: **http://localhost:3000** (opens automatically)

## Verify Services

### Check MongoDB
```bash
mongosh mongodb://localhost:27017/surgegate
```

### Check Redis
```bash
redis-cli ping
# Output: PONG
```

### Check Backend
```bash
curl http://localhost:8080/api/organizer/concerts/test
```

### Check Frontend
Open browser to http://localhost:3000


## Troubleshooting

### Docker services not running
```bash
docker-compose down
docker-compose up -d
docker-compose logs
```

### MongoDB connection error
```bash
docker-compose ps  # Check if running
mongosh --version  # If mongosh not installed, try:
docker exec surgegate-mongodb mongosh
```

### Redis connection error
```bash
redis-cli ping
# If not installed: docker exec surgegate-redis redis-cli ping
```

### Backend port 8080 in use
```bash
# Windows:
netstat -ano | findstr :8080

# macOS/Linux:
lsof -i :8080

# Kill process:
kill -9 <PID>
```

### Frontend port 3000 in use
```bash
# Windows:
netstat -ano | findstr :3000

# macOS/Linux:
lsof -i :3000

kill -9 <PID>
```

### Maven build fails
```bash
mvn clean
mvn clean package -DskipTests
```

### npm install fails
```bash
npm cache clean --force
rm -r node_modules package-lock.json
npm install
```

## Quick Commands

```bash
# Docker
docker-compose up -d      # Start all services
docker-compose down       # Stop all services
docker-compose ps         # Status
docker-compose logs -f    # View logs

# Backend
mvn clean package         # Build
java -jar target/*.jar    # Run

# Frontend
npm start                 # Dev server
npm run build             # Production build

# Database
redis-cli ping            # Test Redis
redis-cli keys "*"        # List Redis keys
mongosh mongodb://localhost:27017  # MongoDB shell
```

Done! Your system is running:
- Backend: http://localhost:8080
- Frontend: http://localhost:3000
- MongoDB: localhost:27017
- Redis: localhost:6379

