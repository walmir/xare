package wa.xare.core.node;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;

import wa.xare.core.Route;
import wa.xare.core.node.LoggerNode;
import wa.xare.core.packet.Packet;

@RunWith(MockitoJUnitRunner.class)
public class LoggerNodeTest {

	@Spy
	LoggerNode node;

	@Mock
	Route Route;

	@Mock
	Logger logger;

	@Before
	public void prepare() {
		Container container = mock(Container.class);

		when(Route.getContainer()).thenReturn(container);
		when(container.logger()).thenReturn(logger);

		node.setRoute(Route);
	}

	@Test
	public void testStartProcessing() throws Exception {
		node.setLevel(LoggerNode.INFO);
		node.startProcessing(mock(Packet.class));

		verify(logger).info(any());
		verifyNoMoreInteractions(logger);

		reset(logger);

		node.setLevel(LoggerNode.DEBUG);
		node.startProcessing(mock(Packet.class));

		verify(logger).debug(any());
		verifyNoMoreInteractions(logger);

		reset(logger);

		node.setLevel(LoggerNode.ERROR);
		node.startProcessing(mock(Packet.class));

		verify(logger).error(any());
		verifyNoMoreInteractions(logger);

		reset(logger);

		node.setLevel(LoggerNode.TRACE);
		node.startProcessing(mock(Packet.class));

		verify(logger).trace(any());
		verifyNoMoreInteractions(logger);

		reset(logger);

		node.setLevel(LoggerNode.WARN);
		node.startProcessing(mock(Packet.class));

		verify(logger).warn(any());
		verifyNoMoreInteractions(logger);
	}

}
