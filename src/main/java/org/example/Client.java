package org.example;

import java.io.*;
import java.net.*;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private final String clientId = UUID.randomUUID().toString();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        new Client().startClient();
    }

    public void startClient() {
        try (final var socket = new Socket(HOST, PORT);
             final var is = socket.getInputStream();
             final var isr = new InputStreamReader(is);
             final var reader = new BufferedReader(isr);
             final var os = socket.getOutputStream();
             final var osr = new OutputStreamWriter(os);
             final var writer = new BufferedWriter(osr)) {
            logger.info("Connected to server at {}:{}", HOST, PORT);

            while (!Thread.currentThread().isInterrupted()) {
                final var line = reader.readLine();
                if (line == null) {
                    break;
                }

                final var state = objectMapper.readValue(line, State.class);
                logger.info("Received state: {}", state.value());

                final var cmd = new Command.Increment(clientId);
                final var cmdJson = objectMapper.writeValueAsString(cmd);
                writer.write(cmdJson);
                writer.newLine();
                writer.flush();
                logger.info("Sent command: {}", cmd);
            }
        } catch (IOException e) {
            logger.error("Error in client operation", e);
        } finally {
            logger.info("Client exiting");
        }
    }
}