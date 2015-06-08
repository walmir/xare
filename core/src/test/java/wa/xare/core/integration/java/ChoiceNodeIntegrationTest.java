package wa.xare.core.integration.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.vertx.testtools.VertxAssert.fail;
import static org.vertx.testtools.VertxAssert.testComplete;

import org.junit.Test;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import wa.xare.core.DefaultRoute;
import wa.xare.core.RouteConfiguration;
import wa.xare.core.node.NodeConfiguration;
import wa.xare.core.node.NodeType;
import wa.xare.core.node.endpoint.EndpointConfiguration;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;
import wa.xare.core.selector.SelectorConfiguration;

public class ChoiceNodeIntegrationTest extends TestVerticle {

  private static final String IN_ADDRESS = "in-address";
  private static final String OUT1_ADDRESS = "out1-address";
  private static final String OUT2_ADDRESS = "out2-address";
  private static final String OUT_OTHERWISE_ADDRESS = "out-otherwise-address";

  JsonObject obj1 = new JsonObject("{\"value\":1}");
  JsonObject obj2 = new JsonObject("{\"value\":2}");
  JsonObject obj3 = new JsonObject("{\"value\":3}");

  public void configureAndDeployRoute() {
    RouteConfiguration rConfig = new RouteConfiguration();
    rConfig.setName("choice-route");
    rConfig.setIncomingEndpointConfiguration(new EndpointConfiguration()
        .withEndpointAddress(IN_ADDRESS)
        .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT)
        .withEndpointDirection(EndpointDirection.INCOMING));

    NodeConfiguration choiceConfig = new NodeConfiguration()
        .withType(NodeType.CHOICE);
    JsonArray cases = new JsonArray();

    // 1st Selector
    SelectorConfiguration selector1Config = new SelectorConfiguration()
        .withExpressionLanguage(
            SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE)
        .withExpression("$.[?(@.value==1)]");

    SelectorConfiguration selector2Config = new SelectorConfiguration()
        .withExpressionLanguage(
            SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE)
        .withExpression("$.[?(@.value==2)]");

    JsonObject case1 = new JsonObject();
    case1.putObject("selector", selector1Config);

    JsonObject case2 = new JsonObject();
    case2.putObject("selector", selector2Config);

    EndpointConfiguration end1 = new EndpointConfiguration()
        .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT)
        .withEndpointDirection(EndpointDirection.OUTGOING)
        .withEndpointAddress(OUT1_ADDRESS);

    EndpointConfiguration end2 = new EndpointConfiguration()
        .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT)
        .withEndpointDirection(EndpointDirection.OUTGOING)
        .withEndpointAddress(OUT2_ADDRESS);

    EndpointConfiguration endOtherwise = new EndpointConfiguration()
        .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT)
        .withEndpointDirection(EndpointDirection.OUTGOING)
        .withEndpointAddress(OUT_OTHERWISE_ADDRESS);

    JsonArray nodePath1 = new JsonArray();
    nodePath1.add(end1);
    JsonArray nodePath2 = new JsonArray();
    nodePath2.add(end2);
    JsonArray nodePathOtherwise = new JsonArray();
    nodePathOtherwise.add(endOtherwise);

    case1.putArray("nodes", nodePath1);
    case2.putArray("nodes", nodePath2);

    cases.add(case1);
    cases.add(case2);
    
    choiceConfig.putArray("cases", cases);
    choiceConfig.putArray("otherwise", nodePathOtherwise);

    rConfig.addNodeConfiguration(choiceConfig);

    container.deployWorkerVerticle(DefaultRoute.class.getName(), rConfig, 1,
        false, r -> {
          if (r.succeeded()) {
            startTests();
          } else {
            fail(r.cause().getMessage());
          }
        });

  }

  @Override
  public void start() {
    initialize();
    configureAndDeployRoute();
  }

  @Test
  public void testFirstPath() {
    vertx.eventBus().registerHandler(OUT2_ADDRESS, message -> {
      fail("recieved on wrong address: " + OUT2_ADDRESS);
    });

    vertx.eventBus().registerHandler(OUT_OTHERWISE_ADDRESS, message -> {
      fail("recieved on wrong address: " + OUT_OTHERWISE_ADDRESS);
    });

    vertx.eventBus().registerHandler(OUT1_ADDRESS, message -> {
      assertThat(message.body()).isEqualTo(obj1);
      testComplete();
    });

    vertx.eventBus().send(IN_ADDRESS, obj1);
  }

  @Test
  public void testSecondPath() {
    vertx.eventBus().registerHandler(OUT2_ADDRESS, message -> {
      assertThat(message.body()).isEqualTo(obj2);
      testComplete();
    });

    vertx.eventBus().registerHandler(OUT_OTHERWISE_ADDRESS, message -> {
      fail("recieved on wrong address: " + OUT_OTHERWISE_ADDRESS);
    });

    vertx.eventBus().registerHandler(OUT1_ADDRESS, message -> {
      fail("recieved on wrong address: " + OUT1_ADDRESS);
    });

    vertx.eventBus().send(IN_ADDRESS, obj2);
  }

  @Test
  public void testOtherwisePath() {
    vertx.eventBus().registerHandler(OUT2_ADDRESS, message -> {
      fail("recieved on wrong address: " + OUT2_ADDRESS);
    });

    vertx.eventBus().registerHandler(OUT_OTHERWISE_ADDRESS, message -> {
      assertThat(message.body()).isEqualTo(obj3);
      testComplete();
    });

    vertx.eventBus().registerHandler(OUT1_ADDRESS, message -> {
      fail("recieved on wrong address: " + OUT1_ADDRESS);
    });

    vertx.eventBus().send(IN_ADDRESS, obj3);
  }

}
