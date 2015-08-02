package wa.xare.core;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import wa.xare.core.configuration.EndpointConfiguration;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.configuration.RouteConfiguration;
import wa.xare.core.node.AbstractNode;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.ProcessingResult;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRouteTest {

  // private static final Logger LOGGER = LoggerFactory
  // .getLogger(DefaultRouteTest.class);

  @Spy
  DefaultRoute defaultRoute;

  // @Mock
  // Logger logger;

  @Mock
  Vertx vertx;

  @Mock
  EventBus eventBus;

  @Spy
  GoodNode node1;

  @Spy
  GoodNode node2;

  @Spy
  BadNode badNode;

  @Before
  public void prepare() {
    RouteConfiguration config = new RouteConfiguration();
    config.setIncomingEndpointConfiguration(new EndpointConfiguration()
        .withEndpointDirection(EndpointDirection.INCOMING).withEndpointType(
            EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT));

    // defaultRoute.setContainer(container);
    // when(route.getContainer()).thenReturn(container);
    // when(container.logger()).thenReturn(logger);
    // when(container.config()).thenReturn(config);

    when(defaultRoute.getVertx()).thenReturn(vertx);
    when(vertx.eventBus()).thenReturn(eventBus);

    defaultRoute.addNode(node1);
    defaultRoute.addNode(node2);

  }

  @Test
  public void testTraverse() throws Exception {
    Packet packet = mock(Packet.class);
    InOrder inorder = inOrder(node1, node2);

    // defaultRoute.start();
    defaultRoute.getPipeline().startProcessing(packet);

    inorder.verify(node1).startProcessing(packet);
    inorder.verify(node2).startProcessing(packet);

  }

  @Test
  public void testStart() throws Exception {
    // throw new RuntimeException("not yet implemented");
  }

  public class GoodNode extends AbstractNode {

    @Override
    public void startProcessing(Packet packet) {
      notifyProcessingListeners(ProcessingResult
          .successfulProcessingResult(packet));
    }

    @Override
    protected void doConfigure(NodeConfiguration configuration) {
      // TODO Auto-generated method stub
    }

  }

  public class BadNode extends AbstractNode {

    @Override
    public void startProcessing(Packet packet) {
      notifyProcessingListeners(ProcessingResult.failedProcessingResult(packet,
          new Throwable("bad node does bad things")));
    }

    @Override
    protected void doConfigure(NodeConfiguration configuration) {
      // TODO Auto-generated method stub
    }

  }

}
