package wa.xare.core.integration.java;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import wa.xare.core.DefaultRoute;
import wa.xare.core.configuration.EndpointConfiguration;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.configuration.RouteConfiguration;
import wa.xare.core.configuration.SelectorConfiguration;
import wa.xare.core.node.LoggerNode;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;
import wa.xare.core.node.subroute.FilterNode;
import wa.xare.core.packet.DefaultPacket;
import wa.xare.core.packet.Packet;

@RunWith(VertxUnitRunner.class)
public class FilterNodeIntegrationTest {
  
  private static final Logger LOGGER = LoggerFactory
      .getLogger(FilterNodeIntegrationTest.class);

  private static final String OUT_ADDRESS = "out-address";
  private static final String IN_ADDRESS = "in-address";
  private static final String AUTHOR = "author";
  private static final String PRICE = "price";
  private static final String RIGHT_AUTHOR = "Joseph Tribiani";
  private static final String WRONG_AUTHOR = "Sam Wan";

  Vertx vertx;
  
  private JsonObject wrongBook;
  private JsonObject rightBook;

  private Packet firstPacket;
  private Packet secondPacket;
  private String expression = "$.[?(@.author=='" + RIGHT_AUTHOR + "')]";

  @Before
  public void before(TestContext context){
    vertx = Vertx.vertx();

    prepareNode();
    JsonObject routeConfig = configureRoute();
    vertx.deployVerticle(DefaultRoute.class.getName(), new DeploymentOptions()
        .setConfig(routeConfig).setWorker(true), context.asyncAssertSuccess());
  }
  
  @After
  public void after(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  public void prepareNode() {
    wrongBook = new JsonObject();
    wrongBook.put(AUTHOR, WRONG_AUTHOR);
    wrongBook.put(PRICE, 20);
    firstPacket = new DefaultPacket();
    firstPacket.setBody(wrongBook);

    rightBook = new JsonObject();
    rightBook.put(AUTHOR, RIGHT_AUTHOR);
    rightBook.put(PRICE, 10);
    secondPacket = new DefaultPacket();
    secondPacket.setBody(rightBook);
  }

  @Test
  public void testFilterNode(TestContext context) throws Exception {
    prepareNode();

    Async async = context.async();

    vertx.eventBus().consumer(OUT_ADDRESS, message -> {
      LOGGER.info("recieved output message: " + message.body());
      context.assertEquals(message.body(), rightBook);
      async.complete();
    });

    vertx.eventBus().send(IN_ADDRESS, wrongBook);
    vertx.eventBus().send(IN_ADDRESS, rightBook);
  }

  private JsonObject configureRoute() {

    RouteConfiguration rConfig = new RouteConfiguration();
    rConfig.setIncomingEndpointConfiguration(new EndpointConfiguration()
        .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT)
        .withEndpointDirection(EndpointDirection.INCOMING)
        .withEndpointAddress(IN_ADDRESS));

    NodeConfiguration filterConfig = new NodeConfiguration()
      .withType(FilterNode.TYPE_NAME)
      .withSelector(
        new SelectorConfiguration().withExpression(expression)
            .withExpressionLanguage(
                SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE));

    NodeConfiguration subLoggerNode = new NodeConfiguration()
        .withType(LoggerNode.TYPE_NAME);
    subLoggerNode.put(LoggerNode.LOG_LEVEL_FIELD, "info");

    EndpointConfiguration subEndpointConfig = new EndpointConfiguration()
        .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT)
        .withEndpointDirection(EndpointDirection.OUTGOING)
        .withEndpointAddress(OUT_ADDRESS);

    JsonArray nodeArray = new JsonArray();
    nodeArray.add(subLoggerNode);
    nodeArray.add(subEndpointConfig);

    filterConfig.put("nodes", nodeArray);
    rConfig.addNodeConfiguration(filterConfig);
    return rConfig;
  }
}
