package wa.xare.core.integration.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.vertx.testtools.VertxAssert.fail;
import static org.vertx.testtools.VertxAssert.testComplete;

import java.util.ArrayList;
import java.util.List;

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
import wa.xare.core.packet.PacketSegment;
import wa.xare.core.selector.SelectorConfiguration;

public class SplitterNodeIntegrationTest extends TestVerticle {

  private JsonObject messageBody;
  private JsonArray booksArray;
  private JsonObject bookOne;
  private JsonObject bookTwo;

  @Test
  public void testRoute() {

    final List<Object> list = new ArrayList<>();
    // final List<Object> booksList = booksArray.toList();
    
    Object msgBody = prepareMessageBody();
    vertx.eventBus().registerHandler("sub-route-output", message -> {
      container.logger().info("recieved output message: " + message.body());
      list.add(message.body());
      if (list.size() == 2){
        assertThat(list).contains(bookOne, bookTwo);
        testComplete();
      }
    });

    vertx.eventBus().sendWithTimeout("address-0", msgBody, 10_000, r -> {
      if (r.failed()) {
        fail(r.cause().getMessage());
      }
      container.logger().info("result: " + r.result());
    });
  }

  @Override
  public void start() {
    initialize();
    configureAndDeployRoute();
  }

  private void configureAndDeployRoute() {
    String bookSelector = "$.books";
    String titleSelector = "$..title";

    NodeConfiguration logNodeConfig = new NodeConfiguration();
    logNodeConfig.setType(NodeType.LOGGER);
    logNodeConfig.putString(LoggerNode.LOG_LEVEL_FIELD, "info");

    NodeConfiguration splitNodeConfig = new NodeConfiguration();
    splitNodeConfig.setType(NodeType.SPLITTER);
    splitNodeConfig.setSelector(new SelectorConfiguration()
        .withExpressionLanguage(
            SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE)
        .withSegment(PacketSegment.BODY)
        .withExpression(bookSelector));

    NodeConfiguration secondLogNode = new NodeConfiguration();
    secondLogNode.setType(NodeType.LOGGER);
    secondLogNode.putString(LoggerNode.LOG_LEVEL_FIELD, "info");
    secondLogNode
        .setSelector(new SelectorConfiguration()
        .withExpressionLanguage(
            SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE)
        .withExpression(titleSelector));
    
    EndpointConfiguration subFinalNode = new EndpointConfiguration();
    subFinalNode.setEndpointAddress("sub-route-output");
    subFinalNode.setEndpointDirection(EndpointDirection.OUTGOING);
    subFinalNode.setEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);
    
    JsonArray nodes = new JsonArray();
    nodes.add(secondLogNode);
    nodes.add(subFinalNode);
    splitNodeConfig.putArray("nodes", nodes);
    
    EndpointConfiguration finalNode = new EndpointConfiguration();
    finalNode.setEndpointAddress("route-output");
    finalNode.setEndpointDirection(EndpointDirection.OUTGOING);
    finalNode.setEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);

    EndpointConfiguration eConfig = new EndpointConfiguration();
    eConfig.setEndpointAddress("address-0");
    eConfig.setEndpointDirection(EndpointDirection.INCOMING);
    eConfig.setEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);

    RouteConfiguration rConfig = new RouteConfiguration();
    rConfig.setName("the-route");
    rConfig.setIncomingEndpointConfiguration(eConfig);
    rConfig.addNodeConfiguration(logNodeConfig);
    rConfig.addNodeConfiguration(splitNodeConfig);
    rConfig.addNodeConfiguration(finalNode);

    System.out.println(rConfig);
    container.deployWorkerVerticle(DefaultRoute.class.getName(), rConfig, 1,
        false, r -> {
          if (r.succeeded()) {
            startTests();
          } else {
            fail(r.cause().getMessage());
          }
        });
  }

  private Object prepareMessageBody() {
    
    messageBody = new JsonObject();
    messageBody.putString("someField", "someValue");
    booksArray = new JsonArray();
    
    bookOne = new JsonObject();
    bookOne.putString("title", "The Jungle Book");
    bookOne.putString("author", "Rudyard Kipling");
    
    bookTwo = new JsonObject();
    bookTwo.putString("title", "Demian");
    bookTwo.putString("author", "Hermann Hesse");
    
    booksArray.add(bookOne);
    booksArray.add(bookTwo);

    messageBody.putArray("books", booksArray);
    return messageBody;

  }

}
