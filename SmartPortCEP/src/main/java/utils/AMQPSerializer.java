package utils;

import com.espertech.esperio.amqp.AMQPToObjectCollector;
import com.espertech.esperio.amqp.AMQPToObjectCollectorContext;
import com.google.gson.Gson;

import java.util.Map;

public class AMQPSerializer implements AMQPToObjectCollector {

    @Override
    public void collect(AMQPToObjectCollectorContext context) {
        //System.out.println("Processing Message: " + new String(context.getBytes()));
        Map event = new Gson().fromJson(new String(context.getBytes()), Map.class);

        context.getEmitter().submit(event);
    }
}