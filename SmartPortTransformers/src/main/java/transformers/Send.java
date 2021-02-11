package transformers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class Send {

    /**
     * Java Class for testing purposes
     **/

    private final static String QUEUE_NAME = "input-json";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            String message = "{" +
                    "\"eventType\": \"AirQualityMeasurement\"," +
                    "\"stationId\":1," +
                    "\"pm10\": 124.5," +
                    "\"pm25\": 6.0," +
                    "\"o3\": 0.052," +
                    "\"co\": 2.1," +
                    "\"so2\": 24.2," +
                    "\"no2\": 55.6" +
                    "}";
            long start = System.currentTimeMillis();
            long cont = 0;
            for (; ; ) {
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                /*cont++;
                System.out.println();
                System.out.println("Messages Sent = " + (cont));
                double end = (System.currentTimeMillis() - start) / 1000;
                System.out.println(end);
                System.out.println("Send rate (m/s) = " + (cont) / end);
                System.out.println();*/
            }
        }
    }
}