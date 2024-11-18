package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;

public class GameClient extends AbstractVerticle {
    @Override
    public void start() {
        vertx.createHttpClient().webSocket(8080, "localhost", "/", ws -> {
            if (ws.succeeded()) {
                WebSocket webSocket = ws.result();
                System.out.println("Connected to the server");

                // Listen for messages from the server (game state updates)
                webSocket.textMessageHandler(message -> {
                    System.out.println("Received game state: " + message);
                    String command = "ala ma kota";
                    webSocket.writeTextMessage(command);
                });
            } else {
                System.out.println("Failed to connect to server: " + ws.cause());
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new GameClient());
    }
}
