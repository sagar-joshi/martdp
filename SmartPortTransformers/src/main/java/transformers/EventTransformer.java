package transformers;

import com.github.underscore.lodash.U;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.Map;
import java.util.concurrent.TimeoutException;

public class EventTransformer {

    private static boolean JSON = false;
    private static boolean XML = false;

    public static void main(String[] args) throws IOException, TimeoutException {
        if (args.length < 2) {
            System.out.println("At least the input type and input queue are required!");
            System.exit(1);
        }

        String inputType = args[0];
        String inputQueue = args[1];
        String outputQueue = args.length > 2 ? args[2] : "input-map";
        String inputHost = args.length > 3 ? args[3] : "localhost";
        String outputHost = args.length > 4 ? args[4] : "localhost";

        if (inputType.equalsIgnoreCase("json"))
            JSON = true;
        else if (inputType.equalsIgnoreCase("xml"))
            XML = true;
        else {
            System.out.println("The specified input type is not valid!");
            System.exit(1);
        }

        ConnectionFactory outputFactory = new ConnectionFactory();
        outputFactory.setHost(outputHost);
        Connection outputConnection = outputFactory.newConnection();
        Channel outputChannel = outputConnection.createChannel();
        outputChannel.queueDeclare(outputQueue, true, false, false, null);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(inputHost);
        Connection connection;

        try {
            connection = factory.newConnection();
            Channel channel = null;
            channel = connection.createChannel();
            channel.queueDeclare(inputQueue, true, false, false, null);
            System.out.println(" [*] Waiting for messages to transform. To exit press CTRL+C");
            final Connection finalConnection = connection;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                long receivedTime = System.currentTimeMillis();
                //System.out.println(" [x] Received '" + message + "'");
                Map eventMap = transformMessage(message, JSON, XML);
                eventMap.put("receivedTime", receivedTime);
                eventMap.put("transformedTime", System.currentTimeMillis());
                sendEventMap(outputChannel, outputQueue, eventMap, message);
            };
            channel.basicConsume(inputQueue, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transforms an input message into the desired output format
     *
     * @param event the message to be transformed
     * @param JSON  indicates if the message comes as JSON
     * @param XML   indicates if the message comes as XML
     * @return returns the message as Java Map
     */
    public static Map transformMessage(String event, boolean JSON, boolean XML) {
        //System.out.println(" [x] Transforming message...");
        if (JSON)
            return new Gson().fromJson(event, Map.class);
        else if (XML)
            return U.fromXml(event);
        else
            throw new RuntimeException("Input type is not valid. Please restart.");
    }

    /**
     * Send the transformed message to the desired output topic
     *
     * @param outputChannel the output channel and host where the Message Broker is running
     * @param outputQueue   the output topic where the message will be sent
     * @param eventMap      the message, as Java Map, to be sent
     */
    public static void sendEventMap(Channel outputChannel, String outputQueue, Map eventMap, String message) throws IOException {
        //System.out.println(" [x] Sending transformed message...");
        outputChannel.basicPublish("", outputQueue, null, eventMap.toString().getBytes(StandardCharsets.UTF_8));
    }

}


