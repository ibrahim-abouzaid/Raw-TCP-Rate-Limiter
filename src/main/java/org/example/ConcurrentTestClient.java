package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentTestClient {

    private static final int CLIENTS = 10;
    private static final int REQUESTS_PER_CLIENT = 15;

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(CLIENTS);

        for (int i = 1; i <= CLIENTS; i++) {
            int clientId = i;
            executor.submit(() -> runClient(clientId));
        }

        executor.shutdown();
    }

    private static void runClient(int clientId) {
        for (int i = 1; i <= REQUESTS_PER_CLIENT; i++) {
            try (Socket socket = new Socket("localhost", 8080)) {

                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                out.write("REQUEST Client"+clientId+"\n");
                out.flush();

                String response = in.readLine();
                System.out.println("Client-" + clientId +
                        " Request-" + i + " -> " + response);

                Thread.sleep(100); // simulate traffic

            } catch (Exception e) {
                System.out.println("Client-" + clientId + " error: " + e.getMessage());
            }
        }
    }
}
