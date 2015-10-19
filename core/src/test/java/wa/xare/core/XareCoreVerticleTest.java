package wa.xare.core;

import io.vertx.core.*;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by wajdi on 18/10/15.
 */
@RunWith(VertxUnitRunner.class)
public class XareCoreVerticleTest extends TestCase {


  @Test
  public void testDeploymendAndUndeployment(TestContext context) {
    TestVerticle testVerticle = new TestVerticle();
//    System.out.println("dep id before: " + testVerticle.deploymentID());

    Vertx.vertx().deployVerticle(testVerticle, new DeploymentOptions().setWorker(true),
        context.asyncAssertSuccess(event -> {
          String depId = testVerticle.deploymentID();
          System.out.println("dep id after: " + depId);
          Vertx.vertx().undeploy(depId, context.asyncAssertSuccess());
    }));

  }

  private void handleResult() {

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


}
