package actions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class ActionBroker {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("The input queue is required!");
            System.exit(1);
        }

        String inputQueue = args[0];
        String host = args.length > 1 ? args[1] : "localhost";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        Connection connection = null;

        try {
            connection = factory.newConnection();
            Channel channel = null;
            channel = connection.createChannel();
            channel.queueDeclare(inputQueue, false, false, false, null);
            System.out.println(" [*] Waiting for complex events to act. To exit press CTRL+C");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                JsonObject jsonMessage = new Gson().fromJson(new String(delivery.getBody(), StandardCharsets.UTF_8), JsonObject.class);
                System.out.println(" [x] Received '" + jsonMessage + "'");

                String action = jsonMessage.getAsJsonObject("action").get("action").getAsString();
                String eventPatterName = jsonMessage.get("eventPatternName").getAsString();
                String alert = jsonMessage.getAsJsonObject("complexEvent").toString();

                if (action.equalsIgnoreCase("file")) {
                    String fileName = jsonMessage.getAsJsonObject("action").get("name").getAsString();
                    FileAction fileAction = new FileAction(fileName, eventPatterName, alert);
                    fileAction.saveAlert();
                } else if (action.equalsIgnoreCase("database")) {
                    String mongoURI = jsonMessage.getAsJsonObject("action").get("mongoURI").getAsString();
                    String databaseName = jsonMessage.getAsJsonObject("action").get("databaseName").getAsString();
                    NoSQLAction noSQLAction = new NoSQLAction(mongoURI, databaseName, eventPatterName, alert);
                    noSQLAction.saveAlert();
                } else {
                    System.out.println("Action not supported yet!");
                }
            };
            channel.basicConsume(inputQueue, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
