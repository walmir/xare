package wa.xare.core;

import io.vertx.core.*;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.impl.MessageImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rx.java.RxHelper;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import wa.xare.core.configuration.EndpointConfiguration;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.configuration.RouteConfiguration;
import wa.xare.core.node.LoggerNode;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;

import javax.sql.XAConnection;

/**
 * Created by wajdi on 18/10/15.
 */
@RunWith(VertxUnitRunner.class)
public class XareCoreVerticleTest extends TestCase {

  @Mock
  Route route;


  private Vertx vertx;

  @Before
  public void setUp(TestContext ctx) {
    MockitoAnnotations.initMocks(this);
    XareCoreVerticle core = new XareCoreVerticle();
    vertx = Vertx.vertx();
    Async async = ctx.async();
    vertx.deployVerticle(core, ctx.asyncAssertSuccess(event -> async.complete()));
  }


//  @Test
  public void testDeploymendAndUndeployment(TestContext context) {
    TestVerticle testVerticle = new TestVerticle();
//    System.out.println("dep id before: " + testVerticle.deploymentID());

    Async async = context.async();
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(testVerticle, new DeploymentOptions().setWorker(true),
        context.asyncAssertSuccess(event -> {
          String depId = testVerticle.deploymentID();
          System.out.println("dep id after: " + depId);
          vertx.undeploy(depId, context.asyncAssertSuccess());
          async.complete();
        }));
  }

  @Test
  public void testAddAndDeplyRoute(TestContext context) {
    Async async = context.async();

    JsonObject routeConfig = configureRoute();

    DeliveryOptions options = new DeliveryOptions().addHeader("command", "add");
    vertx.eventBus().send(XareCoreVerticle.DEFAULT_ADDRESS, routeConfig, options,
        context.asyncAssertSuccess(msg -> {
          String response = (String) msg.body();
          context.assertNotNull(response)
              .assertTrue(response.equals("route configuration added"));

          async.complete();

        }));

    async.awaitSuccess();
    Async async1 = context.async();
    vertx.eventBus().send(XareCoreVerticle.DEFAULT_ADDRESS, "",
        new DeliveryOptions().addHeader("command", "listRoutes"),
        context.asyncAssertSuccess(listMsg -> {
          context.assertTrue(listMsg.body().equals(routeConfig.getString("name")));
          async1.complete();
        }));


    async1.awaitSuccess();
    Async async2 = context.async();
    vertx.eventBus().send(XareCoreVerticle.DEFAULT_ADDRESS, "the-route",
        new DeliveryOptions().addHeader("command", "deploy"));



//    vertx.eventBus().send(XareCoreVerticle.DEFAULT_ADDRESS, null, new DeliveryOptions().addHeader("command", "listRoutes"));
  }

  @Test
  public void testDeployRoute(TestContext context) {
    Async async = context.async();



  }


  public class TestVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
      System.out.println("starting test verticle");
    }

    @Override
    public void stop() throws Exception {
      System.out.println("stopping test verticle");
    }
  }


  private JsonObject configureRoute() {
    NodeConfiguration nConfig = new NodeConfiguration();
    nConfig.setType("logger");
    nConfig.put(LoggerNode.LOG_LEVEL_FIELD, "info");

    EndpointConfiguration finalNode = new EndpointConfiguration();
    finalNode.setEndpointAddress("output");
    finalNode.setEndpointDirection(EndpointDirection.OUTGOING);
    finalNode.setEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);

    EndpointConfiguration eConfig = new EndpointConfiguration();
    eConfig.setEndpointAddress("address-0");
    eConfig.setEndpointDirection(EndpointDirection.INCOMING);
    eConfig.setEndpointType(EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT);

    RouteConfiguration rConfig = new RouteConfiguration();
    rConfig.setName("the-route");
    rConfig.setIncomingEndpointConfiguration(eConfig);
    rConfig.addNodeConfiguration(nConfig);
    rConfig.addNodeConfiguration(finalNode);

    return rConfig;
  }
}
