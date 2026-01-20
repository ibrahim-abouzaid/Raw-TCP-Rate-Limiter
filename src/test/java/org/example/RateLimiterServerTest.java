package org.example;

import org.junit.jupiter.api.*;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterServerTest {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    @BeforeAll
    static void setup() {
        System.out.println("⚠️ Make sure Redis and TCP server are running before tests.");
    }

    @Test
    @DisplayName("First 10 requests should be ALLOW")
    void testAllowLimit() throws Exception {
        for (int i = 1; i <= 10; i++) {
            String response = sendRequest();
            assertEquals("ALLOW", response, "Request " + i + " should be ALLOW");
        }
    }

    @Test
    @DisplayName("Requests exceeding limit should be DENY")
    void testDenyAfterLimit() throws Exception {
        // Send 12 requests to exceed limit
        for (int i = 1; i <= 12; i++) {
            sendRequest();
        }

        String response = sendRequest();
        assertEquals("DENY", response, "Requests after limit should be DENY");
    }

    private String sendRequest() throws Exception {
        try (Socket socket = new Socket(HOST, PORT)) {
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            out.write("REQUEST\n");
            out.flush();

            return in.readLine();
        }
    }
}