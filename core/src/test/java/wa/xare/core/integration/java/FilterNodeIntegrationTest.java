package wa.xare.core.integration.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.vertx.testtools.VertxAssert.fail;
import static org.vertx.testtools.VertxAssert.testComplete;

import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import wa.xare.core.DefaultRoute;
import wa.xare.core.RouteConfiguration;
import wa.xare.core.node.LoggerNode;
import wa.xare.core.node.NodeConfiguration;
import wa.xare.core.node.NodeType;
import wa.xare.core.node.endpoint.EndpointConfiguration;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;
import wa.xare.core.packet.DefaultPacket;
import wa.xare.core.packet.Packet;
import wa.xare.core.selector.SelectorConfiguration;

public class FilterNodeIntegrationTest extends TestVerticle {

  private static final String OUT_ADDRESS = "out-address";
  private static final String IN_ADDRESS = "in-address";
  private static final String AUTHOR = "author";
  private static final String PRICE = "price";
  private static final String RIGHT_AUTHOR = "Joseph Tribiani";
  private static final String WRONG_AUTHOR = "Sam Wan";

  private JsonObject wrongBook;
  private JsonObject rightBook;

  private Packet firstPacket;
  private Packet secondPacket;
  private String expression = "$.[?(@.author=='" + RIGHT_AUTHOR + "')]";

  @Before
  public void prepareNode() {
    wrongBook = new JsonObject();
    wrongBook.putString(AUTHOR, WRONG_AUTHOR);
    wrongBook.putNumber(PRICE, 20);
    firstPacket = new DefaultPacket();
    firstPacket.setBody(wrongBook);

    rightBook = new JsonObject();
    rightBook.putString(AUTHOR, RIGHT_AUTHOR);
    rightBook.putNumber(PRICE, 10);
    secondPacket = new DefaultPacket();
    secondPacket.setBody(rightBook);
  }

  @Test
  public void testSomething() throws Exception {
    prepareNode();

    vertx.eventBus().registerHandler(OUT_ADDRESS, message -> {
      container.logger().info("recieved output message: " + message.body());
      assertThat(message.body()).isEqualTo(rightBook);
      testComplete();
    });

    vertx.eventBus().send(IN_ADDRESS, wrongBook);
    vertx.eventBus().send(IN_ADDRESS, rightBook);
  }

  @Override
  public void start() {
    initialize();
    configureAndDeployRoute();
  }

  private void configureAndDeployRoute() {
    
    RouteConfiguration rConfig = new RouteConfiguration();
    rConfig.setIncomingEndpointConfiguration(new EndpointConfiguration()
        .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT)
        .withEndpointDirection(EndpointDirection.INCOMING)
        .withEndpointAddress(IN_ADDRESS));
    
    NodeConfiguration filterConfig = new NodeConfiguration().withType(
        NodeType.FILTER)
        .withSelector(
            new SelectorConfiguration()
                .withExpression(expression)
                .withExpressionLanguage(
                    SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE));
    
    NodeConfiguration subLoggerNode = new NodeConfiguration()
        .withType(NodeType.LOGGER);
    subLoggerNode.putString(LoggerNode.LOG_LEVEL_FIELD, "info");
    
    EndpointConfiguration subEndpointConfig = new EndpointConfiguration()
      .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT)
      .withEndpointDirection(EndpointDirection.OUTGOING)
      .withEndpointAddress(OUT_ADDRESS);
    
    JsonArray nodeArray = new JsonArray();
    nodeArray.add(subLoggerNode);
    nodeArray.add(subEndpointConfig);
    
    filterConfig.putArray("nodes", nodeArray);
    rConfig.addNodeConfiguration(filterConfig);

    container.deployWorkerVerticle(DefaultRoute.class.getName(), rConfig, 1,
        false, r -> {
          if (r.succeeded()) {
            startTests();
          } else {
            fail(r.cause().getMessage());
          }
        });
  }
}
