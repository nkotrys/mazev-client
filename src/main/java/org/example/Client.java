package org.example;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.concurrent.Executors;

public class Client {

    private static String brokerURL = "tcp://34.171.135.110:8080";
    private final double v;

    public Client(double v) {
        this.v = v;
    }

    public static void main(String[] args) throws Exception {
        try (final var executor = Executors.newFixedThreadPool(2)) {
            executor.submit(() -> {
                Client client = new Client(0.1);
                try {
                    client.run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            executor.submit(() -> {
                Client client = new Client(0.2);
                try {
                    client.run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Thread.sleep(Long.MAX_VALUE);
        }
    }

    public void run() throws Exception {
        // Create a connection factory
        final var connectionFactory = new ActiveMQConnectionFactory(brokerURL);

        // Create a connection
        final var connection = connectionFactory.createConnection();
        connection.start();

        // Create a session (non-transacted, auto acknowledge)
        final var session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Subscribe to the stateTopic
        final var stateTopic = session.createTopic("stateTopic");
        final var stateConsumer = session.createConsumer(stateTopic);

        // Create a producer to send commands
        final var commandQueue = session.createQueue("commandQueue");
        final var commandProducer = session.createProducer(commandQueue);

        // Set a listener to process state updates
        stateConsumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage stateText) {
                    processState(session, commandProducer, stateText.getText());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void processState(Session session, MessageProducer commandProducer, String stateText) throws JMSException {
        // Process the state and send a command
        System.out.println("Client received state: " + stateText);
        try {
            final var stateValue = Double.parseDouble(stateText);
            // Compute a new command based on the state
            final var commandValue = -stateValue * v; // For example, try to reduce the state towards zero
            final var commandText = Double.toString(commandValue);

            // Send the command back to the server
            final var commandMessage = session.createTextMessage(commandText);
            commandProducer.send(commandMessage);
            System.out.println("Client sent command: " + commandText);
        } catch (NumberFormatException e) {
            System.err.println("Invalid state received: " + stateText);
        }
    }
}
