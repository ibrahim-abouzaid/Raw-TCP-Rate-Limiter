package org.example;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private final RedisRateLimiter rateLimiter;

    public ClientHandler(Socket socket, RedisRateLimiter rateLimiter) {
        this.clientSocket = socket;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void run() {
        String clientId = clientSocket.getInetAddress().getHostAddress();
        System.out.println(clientId);

        try (
                // Try-with-resources ensures streams close automatically
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        ) {

            String inputLine = in.readLine();

            // Basic Protocol Parsing: "REQUEST"
            if (inputLine != null && inputLine.contains("REQUEST")) {
                    //for multiClient test
                    String[] parts = inputLine.split(" ", 2);
                 clientId = parts.length > 1 ? clientId+parts[1] : clientId;
                    if (rateLimiter.isAllowed(clientId)) {
                        out.write("ALLOW\n");
                    } else {
                        out.write("DENY\n");
                    }
                 out.flush();
            } else {
                out.write("ERROR:Null input Or Invalid Format. Use: REQUEST\n");
            }
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
}
