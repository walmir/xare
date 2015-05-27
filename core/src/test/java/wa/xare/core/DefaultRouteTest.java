package wa.xare.core;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;

import wa.xare.core.DefaultRoute;
import wa.xare.core.RouteConfiguration;
import wa.xare.core.node.DefaultRouteNode;
import wa.xare.core.node.ProcessingResult;
import wa.xare.core.node.endpoint.EndpointConfiguration;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.node.endpoint.EndpointTypeNames;
import wa.xare.core.packet.Packet;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRouteTest {

	@Spy
	DefaultRoute defaultRoute;

	@Mock
	Container container;

	@Mock
	Logger logger;

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

		defaultRoute.setContainer(container);
		// when(route.getContainer()).thenReturn(container);
		when(container.logger()).thenReturn(logger);
		when(container.config()).thenReturn(config);

		when(defaultRoute.getVertx()).thenReturn(vertx);
		when(vertx.eventBus()).thenReturn(eventBus);

		defaultRoute.addNode(node1);
		defaultRoute.addNode(node2);

	}

	// @Test
	// public void testTraverse() throws Exception {
	// Packet packet = mock(Packet.class);
	// InOrder inorder = inOrder(node1, node2);
	//
	// defaultRoute.start();
	// defaultRoute.traverse(packet);
	//
	// inorder.verify(node1).startProcessing(packet);
	// inorder.verify(node2).startProcessing(packet);
	//
	// }

	@Test
	public void testStart() throws Exception {
		// throw new RuntimeException("not yet implemented");
	}

	public class GoodNode extends DefaultRouteNode {

		@Override
		public void startProcessing(Packet packet) {
			notifyProcessingListeners(ProcessingResult
			    .successfulProcessingResult(packet));
		}

	}

	public class BadNode extends DefaultRouteNode {

		@Override
		public void startProcessing(Packet packet) {
			notifyProcessingListeners(ProcessingResult.failedProcessingResult(packet,
			    new Throwable("bad node does bad things")));
		}

	}

}
