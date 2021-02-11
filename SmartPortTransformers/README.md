# SmartPort Transformers
This is a Java project with a generic transformer which consumes messages from an specific AMQP queue and transforms them to Java Map.

## Steps

1. Clone the repo
2. Open it with a IDE (such as IntelliJ)
3. Install the maven dependencies manually or automatically (it depends on your IDE)
4. Run `EventTransformer.java` to start the application

## How to use it

The transformer receives up to 4 arguments:

- `inputType` specifies the input type to consume. Supported types: `json`, `xml`.
- `inputQueue` specifies the input queue to consume the messages from.
- `outputQueue` specifies the output queue where the transformed messages are sent.
- `host` specifies the host where the AMQP 0.9.1 broker is running.

The first two arguments are mandatory, `inputType` and `inputQueue`.

In order to execute the transformer, you can run it in the IDE and set the arguments (it depends on your IDE).

Another alternative might be compile the project as a single JAR file:
- Run `mvn package` to compile the project
- The resulted JAR file could be executed as:
     - `java -jar SmartPortTransformer.jar json json-events map-events localhost` 
