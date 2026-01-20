# TCP Rate Limiter using Java and Redis

## Overview
This project implements a TCP rate-limiting server in Java using **raw TCP sockets** and **Redis**.  
Clients send requests in the format `REQUEST`, and the server responds with `ALLOW` or `DENY`.

- Each client is limited to 10 requests per 60 seconds.
- Multiple clients are supported concurrently.
- Redis is used to store per-client counters and TTL for the rate-limiting window.

---

## Setup & Installation

### Prerequisites
- Java 17+
- Maven
- Docker
- Redis server (localhost:6379) - **Very important step**

### Steps
1. Start Redis:		
-1.2 Open Docker Desktop		
-1.2 run in terminal 
```bash
docker run -p 6379:6379 redis
```
this will download redis image and run it on container
2. Clone or extract the project folder.

---

## Build & Run

### 1. Clean and compile
```bash
mvn clean compile
```

### 2. Run the TCP server
```bash
mvn exec:java -Dexec.mainClass="org.example.RateLimitServer"
```

### 3. Run JUnit tests
Open a new terminal and execute:
```bash
mvn test
```

### 4. Run manual client (interactive)
```bash
mvn exec:java -Dexec.mainClass="org.example.ManualClient"
```
### 5. Run concurrent client (simulate multiple clients)
```bash
mvn exec:java -Dexec.mainClass="org.example.ConcurrentTestClient"
```

- Type `REQUEST` to send a request.
- Example:
```
> REQUEST
Server: ALLOW
```

- OR Type `REQUEST <clientId>` to send a request with Unique ID **for mutiClient testing**.
- Example:
```
> REQUEST client1
Server: ALLOW
```

---

## Rate-limiting Algorithm

- Each client is identified by a **unique client IP** (note:it work on differet IP address).  
- Redis key format: `rate_limit:<clientIP>`  
- When a request is received:
  1. Increment the Redis key atomically (`INCR`).
  2. If key is new (`count == 1`), set TTL = 60 seconds.
  3. If `count <= MAX_REQUESTS (10)`, return `ALLOW`.
  4. If `count > MAX_REQUESTS`, return `DENY`.

**Atomicity and concurrency:**  
- Lua script ensures `INCR` + `EXPIRE` is atomic.  
- Thread pool handles multiple clients concurrently.

---

## Dependencies

- [JUnit 5](https://junit.org/junit5/) - unit testing  
- [Jedis](https://github.com/redis/jedis) - Redis client for Java  
- Maven for build automation


## GitHub repo
### what next?:
- initiate pipeline using git Actions	
- create an image on docker Hub	
- follow this Repo:	
	-https://github.com/ibrahim-abouzaid/Raw-TCP-Rate-Limiter.git
