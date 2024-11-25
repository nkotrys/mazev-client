package example;

import java.io.*;
import java.net.*;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.domain.Request;
import example.domain.Response;
import example.domain.game.Cave;
import example.domain.game.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final ObjectMapper objectMapper = new ObjectMapper();
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

            {
                final var json = objectMapper.writeValueAsString(new Request.Authorize("1234"));
                writer.write(json);
                writer.newLine();
                writer.flush();
                logger.info("Sent command: {}", json);

                final var line = reader.readLine();
                if (line == null) {
                    return;
                }

                final var response = objectMapper.readValue(line, Response.class);
                switch (response) {
                    case Response.Authorized authorized -> {
                        logger.info("authorized: {}", authorized);
                    }
                    case Response.Unauthorized unauthorized -> {
                        logger.error("unauthorized: {}", unauthorized);
                        return;
                    }
                    case Response.StateCave stateCave -> {
                        logger.error("unexpected response: {}", stateCave);
                        return;
                    }
                    case Response.StateLocations stateLocations -> {
                        logger.error("unexpected response: {}", stateLocations);
                        return;
                    }
                }
            }

            Cave cave;

            {
                final var line = reader.readLine();
                if (line == null) {
                    return;
                }

                final var response = objectMapper.readValue(line, Response.class);
                if (Objects.requireNonNull(response) instanceof Response.StateCave stateCave) {
                    cave = stateCave.cave();
                } else {
                    logger.error("unexpected response: {}", response);
                    return;
                }
            }

            while (!Thread.currentThread().isInterrupted()) {
                final var line = reader.readLine();
                if (line == null) {
                    break;
                }

                final var response = objectMapper.readValue(line, Response.class);
                logger.info("Response: {}", response);
                if (Objects.requireNonNull(response) instanceof Response.StateLocations) {
                    final var cmd = new Request.Command(Entity.Player.Direction.Up);
                    final var cmdJson = objectMapper.writeValueAsString(cmd);
                    writer.write(cmdJson);
                    writer.newLine();
                    writer.flush();
                    logger.info("Sent command: {}", cmd);
                } else {
                    logger.error("Unexpected response: {}", response);
                }
            }
        } catch (IOException e) {
            logger.error("Error in client operation", e);
        } finally {
            logger.info("Client exiting");
        }
    }
}