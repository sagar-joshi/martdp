package utils;

import com.espertech.esper.common.client.EPException;
import com.espertech.esper.common.client.EventType;
import com.espertech.esper.common.internal.event.json.core.JsonEventType;
import com.espertech.esperio.amqp.AMQPToObjectCollector;
import com.espertech.esperio.amqp.AMQPToObjectCollectorContext;

public class AMQPSerializerJSON implements AMQPToObjectCollector {
    private JsonEventType jsonEventType;

    public AMQPSerializerJSON() {
        System.out.println("Starting Serializer");
    }

    public void collect(AMQPToObjectCollectorContext context) {
        System.out.println("PROCESSING MESSAGE: " + new String(context.getBytes()));
        if (this.jsonEventType == null) {
            this.init(context.getOutputEventType());
        }

        Object underlying = this.jsonEventType.parse(new String(context.getBytes()));
        context.getEmitter().submit(underlying);
    }

    private void init(EventType outputEventType) {
        if (!(outputEventType instanceof JsonEventType)) {
            throw new EPException("Expected a JSON event type but received " + (outputEventType == null ? "undefined type" : outputEventType.getName()));
        } else {
            this.jsonEventType = (JsonEventType) outputEventType;
        }
    }
}