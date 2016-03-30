package wa.xare.core.integration.java;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.impl.launcher.commands.ListCommand;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import wa.xare.core.Route;
import wa.xare.core.XareCoreVerticle;
import wa.xare.core.builder.RouteBuilder;
import wa.xare.core.builder.RouteConfigurationException;
import wa.xare.core.cli.ListNodesCommand;

@RunWith(VertxUnitRunner.class)
public class ListNodesIntegrationTest {

  Vertx vertx;

  @Before
  public void before(TestContext context) throws RouteConfigurationException {
    VertxOptions vertxOptions = new VertxOptions();
    vertxOptions.setMaxEventLoopExecuteTime(Long.MAX_VALUE);

    Async async = context.async();

    Vertx.clusteredVertx(vertxOptions, result -> {
          if (result.succeeded()) {
            vertx = result.result();
            async.complete();
          } else {
            context.fail("could not initialize clustered VertX");
          }
        }
      );

    async.awaitSuccess();

    XareCoreVerticle coreVerticle = new XareCoreVerticle();
    vertx.deployVerticle(coreVerticle);

  }

  @After
  public void after(TestContext context) {
    vertx.close(context.asyncAssertSuccess());

  }

  @Test
  public void testRoute(TestContext context) {
    ListNodesCommand command = new ListNodesCommand();
    command.setAddress(XareCoreVerticle.DEFAULT_ADDRESS);
    command.run();
    context.asyncAssertSuccess();
  }

}
