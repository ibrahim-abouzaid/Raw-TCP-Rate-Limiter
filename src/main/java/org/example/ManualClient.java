package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ManualClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Manual Rate Limit Client ---");
        System.out.println("Enter REQUEST to send request.");
        System.out.println("Type 'exit' to quit.");

        while (true) {
            // 1. Wait for you to type
            System.out.print("> ");
            String userInput = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userInput)) break;

            // 2. Send the request
            sendRequest(userInput);
        }
    }

    private static void sendRequest(String userInput) {
        // The server expects a new connection for every request
        try (Socket socket = new Socket("localhost", 8080))
            {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
            // Protocol: REQUEST

                out.write(userInput + "\n");
                out.flush();

            // Read Response
            String response = in.readLine();
            System.out.println("Server: " + response);

        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
        }
    }
}
