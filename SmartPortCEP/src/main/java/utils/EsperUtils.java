package utils;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.annotation.Tag;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.common.client.dataflow.core.EPDataFlowInstance;
import com.espertech.esper.common.internal.type.AnnotationTag;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import com.espertech.esperio.amqp.AMQPSource;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author David Corral-Plaza <david.corral@uca.es>
 */

public class EsperUtils {

    private final static Logger logger = LoggerFactory.getLogger(EsperUtils.class);
    private static EPCompiler epCompiler;
    private static EPRuntime epRuntime;
    private static Configuration configuration;

    /**
     * Constructor
     */
    public EsperUtils() {
        synchronized (EsperUtils.class) {
            logger.info("** Starting Esper Engine **");

            if (configuration == null) {
                logger.info("*** Creating configuration ***");
                configuration = new Configuration();
                configuration.getCommon().addImport(AMQPSerializer.class.getPackage().getName() + ".*");
                configuration.getCommon().addImport(AMQPSource.class.getPackage().getName() + ".*");
            }

            if (epCompiler == null) {
                logger.info("*** Starting Esper Compiler ***");
                epCompiler = EPCompilerProvider.getCompiler();
            }

            if (epRuntime == null) {
                logger.info("*** Starting Esper Runtime ***");
                epRuntime = EPRuntimeProvider.getDefaultRuntime(configuration);
            }
        }
    }

    /**
     * Returns the CompilerArguments each time
     * that a EPL code has to be compiled
     *
     * @return arguments with the global configuration
     */
    public static CompilerArguments getCompilerArguments() {
        CompilerArguments arguments = new CompilerArguments(getEpRuntime().getRuntimePath());
        arguments.setConfiguration(configuration);
        return arguments;
    }

    /**
     * Returns the epCompiler that have been set before
     *
     * @return the epCompiler of the engine
     */
    public static EPCompiler getEpCompiler() {
        synchronized (EsperUtils.class) {
            if (epCompiler == null) {
                logger.info("epCompiler is not defined");
                throw new RuntimeException("Unable to continue because epCompiler is not defined!");
            }
        }
        return epCompiler;
    }

    /**
     * Returns the epRuntime that have been set before
     *
     * @return the epRuntime of the engine
     */
    public static EPRuntime getEpRuntime() {
        synchronized (EsperUtils.class) {
            if (epRuntime == null) {
                logger.info("epRuntime is not defined");
                throw new RuntimeException("Unable to continue because epRuntime is not defined!");
            }
        }
        return epRuntime;
    }

    /**
     * Creates a new dataflow
     *
     * @param epl  The dataflow to be deployed
     * @param name The dataflow's name
     * @throws EPCompileException
     * @throws EPDeployException
     */
    public static boolean createDataflow(String epl, String name) throws EPCompileException, EPDeployException {
        logger.info("Deploying dataflow '" + name + "'");
        EPDataFlowInstance instance =
                getEpRuntime().getDataFlowService().instantiate(deployNewEventPattern(epl).getDeploymentId(), name);

        logger.info("Dataflow '" + name + "' instantiated");

        new Thread(() -> {
            instance.run();
            logger.info("Dataflow '" + name + "' finished");
        }).start();

        return true;
    }

    /**
     * Calls to the compiler and then deploy the compiled EPL sentence
     *
     * @param epl The EPL sentence to be compiled
     * @return The EPL sentence deployed
     * @throws EPCompileException
     */
    public static EPDeployment deployNewEventPattern(String epl) throws EPCompileException, EPDeployException {
        logger.info("Adding new EPL: " + epl);
        return getEpRuntime().getDeploymentService().deploy(compileNewEventPattern(epl));
    }

    /**
     * Add a generic listener to each EPL statement
     *
     * @param statement the EPStatement which will have the listener added
     */
    public static void addGenericListener(EPStatement statement) {
        statement.addListener((newComplexEvents, oldComplexEvents, detectedEventPattern, epRuntime) -> {
            if (newComplexEvents != null) {
                HashMap<String, Object> complexEventAndAction = new HashMap<>();

                String eventPatternName = detectedEventPattern.getEventType().getName();
                logger.info("** Complex event '" + eventPatternName + "' detected: " + newComplexEvents[0].getUnderlying());

                complexEventAndAction.put("eventPatternName", eventPatternName);
                complexEventAndAction.put("complexEvent", newComplexEvents[0].getUnderlying());

                HashMap<String, String> actionAttributes = new HashMap<>();
                for (Annotation a : detectedEventPattern.getAnnotations()) {
                    if (a instanceof Tag) {
                        AnnotationTag tag = (AnnotationTag) a;
                        actionAttributes.put(tag.name(), tag.value());
                    }
                }
                complexEventAndAction.put("action", actionAttributes);

                sendComplexEvent( new Gson().toJson(complexEventAndAction));
            }
        });
    }

    private static void sendComplexEvent(String complexEventAndActions) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare("output-alerts", false, false, false, null);
            channel.basicPublish("", "output-alerts", null, complexEventAndActions.toString().getBytes(StandardCharsets.UTF_8));
            logger.info(" [x] Sent '" + complexEventAndActions.toString() + "' to output-alerts queue");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles an EPL sentence using the runtime configuration
     *
     * @param epl The EPL sentence to be compiled
     * @return The EPL sentence compiled
     * @throws EPCompileException
     */
    public static EPCompiled compileNewEventPattern(String epl) throws EPCompileException {
        return getEpCompiler().compile(epl, getCompilerArguments());
    }

    /**
     * Creates a new event type at runtime
     *
     * @param epl The sentence with the new schema to create
     * @throws EPCompileException
     * @throws EPDeployException
     */
    public static String addNewSchema(String epl) throws EPCompileException, EPDeployException {
        logger.info("Adding new schema: " + epl);
        return deployNewEventPattern(epl).getDeploymentId();
    }

}

