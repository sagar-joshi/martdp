# SmartPort CEP
This is a REST API with an embedded Esper CEP engine. AMQP adapter is enabled so we can define AMQP dataflows and run them properly.

## Steps

1. Clone the repo
2. Open it with a IDE (such as IntelliJ)
3. Install the maven dependencies manually or automatically (it depends on your IDE)
4. Run `SmartPortCepApplication.java` to start the application

## How to use it
There are 3 endpoints in the API:

### POST /schema
It receives an schema in JSON payload and deploys it in the CEP engine:
~~~~
{
	"schema" : "@public @buseventtype create schema Dummy (p1 String, p2 Double)"
}
~~~~

### POST /pattern
It receives an EPL pattern in JSON payload and deploys it in the CEP engine. Besides, the actions parameters are specified using `@Tag`:
~~~~
{
    "pattern": "@Tag(name=\"action\", value=\"file\") @Tag(name=\"name\", value=\"alert.txt\") @public @buseventtype select * from Dummy"
}
~~~~

The pattern above specifies that it has to be saved into a file named `alert.txt`. The required `tag` are:
- `action` which specifies the action, in this case `file`
- `name` which specifies the file name, in this case `alert.txt`

~~~~
{
    "pattern": "@Tag(name=\"action\", value=\"database\") @Tag(name=\"mongoURI\", value=\"mongodb+srv://<user>:<password>@<host>/test?retryWrites=true&w=majority\") @Tag(name=\"databaseName\", value=\"alerts\") @public @buseventtype select * from Dummy"
}
~~~~

The pattern above specifies that it has to be saved into a NoSQL database.  The required `tag` are:
- `action` which specifies the action, in this case `database`
- `mongoURI` which specifies the mongo URI to connect (credentials included), in this case `mongodb+srv://<user>:<password>@<host>/test?retryWrites=true&w=majority`
- `databaseName` which specifies the database where the alerts will be saved, in this case `alerts`

Using this endopoint, `/pattern`, you can send more than one pattern to be deployed using semicolons (`;`) as separator:
~~~~
{
    "pattern": "@public @buseventtype @name("pattern1") select * from Dummy;@public @buseventtype @name("pattern2") select current_timestamp from Dummy;"
}
~~~~

Finally, in order to send `context interval`, as it is a EPL code of Esper, you can send them using this same endpoint:
~~~~
{
    "pattern": "@public create context IntervalSpanningSeconds start @now end after 300 sec;"
}
~~~~

### POST /dataflow
It receives an EPL dataflow code in JSON payload and deploys it in the CEP engine:
~~~~
{
    "dataflow" : "create dataflow AMQPIncomingDataFlow AMQPSource -> outstream<Dummy> {host: 'localhost', queueName: 'input-spring', collector: {class: 'AMQPSerializer'}, logMessages: true, declareAutoDelete: false} EventBusSink(outstream){}",
    "name": "AMQPIncomingDataFlow"
}
~~~~

## How to consume events
Currently, the only way to consume events is using Esper Dataflows. For example, using the dataflow previously specified, the CEP engine will consume events from an AMQP 0.9.1 queue.

The serialization that has been coded allows you to publish events into the queue as JSON messages:
~~~~
{
    "p1": "Hello World",
    "p2": 158.1
}
~~~~
Such a message will be transformed into a Java Map and sent to the Esper CEP engine.
