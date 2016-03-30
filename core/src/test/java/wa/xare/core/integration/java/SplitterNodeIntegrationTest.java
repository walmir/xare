package wa.xare.core.integration.java;

import static org.assertj.core.api.Assertions.assertThat;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import wa.xare.core.DefaultRoute;
import wa.xare.core.Route;
import wa.xare.core.builder.RouteBuilder;
import wa.xare.core.builder.RouteConfigurationException;
import wa.xare.core.configuration.EndpointConfiguration;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.configuration.RouteConfiguration;
import wa.xare.core.configuration.SelectorConfiguration;
import wa.xare.core.node.LoggerNode;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;
import wa.xare.core.packet.PacketSegment;

@RunWith(VertxUnitRunner.class)
public class SplitterNodeIntegrationTest {
  
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SplitterNodeIntegrationTest.class);

  private JsonObject messageBody;
  private JsonArray booksArray;
  private JsonObject bookOne;
  private JsonObject bookTwo;

  Vertx vertx;

  @Before
  public void before(TestContext context) throws RouteConfigurationException {
    vertx = Vertx.vertx();
    JsonObject rConfig = configureRoute();
    RouteBuilder builder = new RouteBuilder();
    Route route = builder.buildRoute(rConfig);
    vertx.deployVerticle(route, new DeploymentOptions().setWorker(true),
        context.asyncAssertSuccess());
  }

  @After
  public void after(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testRoute(TestContext context) {
    
    final List<Object> list = new ArrayList<>();
    // final List<Object> booksList = booksArray.toList();

    Object msgBody = prepareMessageBody();
    Async async = context.async();

    vertx.eventBus().consumer("sub-route-output", message -> {
      LOGGER.info("recieved output message: " + message.body());
      list.add(message.body());
      if (list.size() == 2) {
        assertThat(list).contains(bookOne, bookTwo);
        async.complete();
      }
    });


    vertx.eventBus()
      .send("address-0", msgBody, new DeliveryOptions().setSendTimeout(10_000), r -> {
        if (r.failed()) {
          context.fail(r.cause().getMessage());
        }
        LOGGER.info("result: " + r.result());
      });
  }

  // @Override
  // public void start() {
  // initialize();
  // configureAndDeployRoute();
  // }

  private JsonObject configureRoute() {
    String bookSelector = "$.books";
    String titleSelector = "$.title";

    NodeConfiguration logNodeConfig = new NodeConfiguration();
    logNodeConfig.setType("logger");
    logNodeConfig.put(LoggerNode.LOG_LEVEL_FIELD, "info");

    NodeConfiguration splitNodeConfig = new NodeConfiguration()
        .withType("splitter")
        .withSelector(new SelectorConfiguration()
          .withExpressionLanguage(
              SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE)
              .withSegment(PacketSegment.BODY).withExpression(bookSelector));

    NodeConfiguration secondLogNode = new NodeConfiguration()
      .withType("logger")
      .withSelector(new SelectorConfiguration()
        .withExpressionLanguage(
            SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE)
        .withExpression(titleSelector));
    secondLogNode.put(LoggerNode.LOG_LEVEL_FIELD, "info");

    EndpointConfiguration subFinalNode = new EndpointConfiguration();
    subFinalNode.setEndpointAddress("sub-route-output");
    subFinalNode.setEndpointDirection(EndpointDirection.OUTGOING);
    subFinalNode.setEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);

    JsonArray nodes = new JsonArray();
    nodes.add(secondLogNode);
    nodes.add(subFinalNode);
    splitNodeConfig.put("nodes", nodes);

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

    return rConfig;
  }

  private Object prepareMessageBody() {

    messageBody = new JsonObject();
    messageBody.put("someField", "someValue");
    booksArray = new JsonArray();

    bookOne = new JsonObject();
    bookOne.put("title", "The Jungle Book");
    bookOne.put("author", "Rudyard Kipling");

    bookTwo = new JsonObject();
    bookTwo.put("title", "Demian");
    bookTwo.put("author", "Hermann Hesse");

    booksArray.add(bookOne);
    booksArray.add(bookTwo);

    messageBody.put("books", booksArray);
    return messageBody;

  }

}
