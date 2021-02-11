# SmartPort Actions
This a Java project with a series of actions to be performed when an alert is detected by the CEP engine.

## Steps

1. Clone the repo
2. Open it with a IDE (such as IntelliJ)
3. Install the maven dependencies manually or automatically (it depends on your IDE)
4. Run `ActionBroker.java` to start the application (you might want to compile the project as single `JAR` file using `mvn package`)

## How to use it

The action broker receives up to 2 arguments:

- `inputQueue` specifies the input queue to consume the messages from.
- `host` specifies the host where the AMQP 0.9.1 broker is running.

The first argument is mandatory, `inputQueue`. The host as default will be `localhost`.

In order to execute the transformer, you can run it in the IDE and set the arguments (it depends on your IDE).

Another alternative might be compile the project as a single jar file:
- Run `mvn package` to compile the project
- The resulted jar file could be executed as:
     - `java -jar SmartPortActions.jar output-alerts localhost` 

## Actions

When an alert is consumed, it will check which action has to be performed. Right now, the implemented actions are:

- File: Insert the detected alert into a text file.
- NoSQL Database: Insert the detected alert into a Mongo database. 

These actions are specified when you send the pattern to the `SmartPort CEP`. Further details are available at [SmartPortCEP](https://gitlab.com/ucase/interno/smartportcep).