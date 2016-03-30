package wa.xare.core.integration.java;

import static org.assertj.core.api.Assertions.assertThat;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

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
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;

@RunWith(VertxUnitRunner.class)
public class ChoiceNodeIntegrationTest {

  private static final String IN_ADDRESS = "in-address";
  private static final String OUT1_ADDRESS = "out1-address";
  private static final String OUT2_ADDRESS = "out2-address";
  private static final String OUT_OTHERWISE_ADDRESS = "out-otherwise-address";

  Vertx vertx;

  JsonObject obj1 = new JsonObject("{\"value\":1}");
  JsonObject obj2 = new JsonObject("{\"value\":2}");
  JsonObject obj3 = new JsonObject("{\"value\":3}");

  @Before
  public void before(TestContext context) throws RouteConfigurationException {
    vertx = Vertx.vertx();

    JsonObject routeConfig = configureRoute();

    RouteBuilder routeBuilder = new RouteBuilder();
    Route route = routeBuilder.buildRoute(routeConfig);

    vertx.deployVerticle(route, new DeploymentOptions()
        .setConfig(routeConfig).setWorker(true), context.asyncAssertSuccess());
  }

  @After
  public void after(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  public JsonObject configureRoute() {
    RouteConfiguration rConfig = new RouteConfiguration();
    rConfig.setName("choice-route");
    rConfig.setIncomingEndpointConfiguration(new EndpointConfiguration()
        .withEndpointAddress(IN_ADDRESS)
        .withEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT)
        .withEndpointDirection(EndpointDirection.INCOMING));

    NodeConfiguration choiceConfig = new NodeConfiguration()
      .withType("choice");
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
    case1.put("selector", selector1Config);

    JsonObject case2 = new JsonObject();
    case2.put("selector", selector2Config);

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

    case1.put("nodes", nodePath1);
    case2.put("nodes", nodePath2);

    cases.add(case1);
    cases.add(case2);

    choiceConfig.put("cases", cases);
    choiceConfig.put("otherwise", nodePathOtherwise);

    rConfig.addNodeConfiguration(choiceConfig);
    System.out.printf(rConfig.toString());
    return rConfig;
  }

  @Test
  public void testFirstPath(TestContext context) {
    vertx.eventBus().consumer(OUT2_ADDRESS, message -> {
      context.fail("recieved on wrong address: " + OUT2_ADDRESS);
    });

    vertx.eventBus().consumer(OUT_OTHERWISE_ADDRESS, message -> {
      context.fail("recieved on wrong address: " + OUT_OTHERWISE_ADDRESS);
    });

    vertx.eventBus().consumer(OUT1_ADDRESS, message -> {
      assertThat(message.body()).isEqualTo(obj1);
    });

    vertx.eventBus().send(IN_ADDRESS, obj1);
  }

  @Test
  public void testSecondPath(TestContext context) {
    vertx.eventBus().consumer(OUT2_ADDRESS, message -> {
      assertThat(message.body()).isEqualTo(obj2);
    });

    vertx.eventBus().consumer(OUT_OTHERWISE_ADDRESS, message -> {
      context.fail("recieved on wrong address: " + OUT_OTHERWISE_ADDRESS);
    });

    vertx.eventBus().consumer(OUT1_ADDRESS, message -> {
      context.fail("recieved on wrong address: " + OUT1_ADDRESS);
    });

    vertx.eventBus().send(IN_ADDRESS, obj2);
  }

  @Test
  public void testOtherwisePath(TestContext context) {
    vertx.eventBus().consumer(OUT2_ADDRESS, message -> {
      context.fail("recieved on wrong address: " + OUT2_ADDRESS);
    });

    vertx.eventBus().consumer(OUT_OTHERWISE_ADDRESS, message -> {
      assertThat(message.body()).isEqualTo(obj3);
    });

    vertx.eventBus().consumer(OUT1_ADDRESS, message -> {
      context.fail("recieved on wrong address: " + OUT1_ADDRESS);
    });

    vertx.eventBus().send(IN_ADDRESS, obj3);
  }

}
