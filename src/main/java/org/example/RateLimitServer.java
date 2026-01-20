package org.example;

import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RateLimitServer {
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 50; // Handle 50 concurrent connections

    // 1. Setup Redis Connection Pool (Thread safe)
    // Ensure Redis is running on localhost:6379
    private static final JedisPool jedisPool = new JedisPool("localhost", 6379);

    // 2. Setup Thread Pool for handling clients
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    public static void main(String[] args) {

        RedisRateLimiter rateLimiter = new RedisRateLimiter(jedisPool);

       // try-with-resources
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                // Blocks until a client connects
                Socket clientSocket = serverSocket.accept();

                // Submit the handling task to the thread pool
                executor.submit(new ClientHandler(clientSocket, rateLimiter));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            jedisPool.close();
            executor.shutdown();
        }
    }
}
