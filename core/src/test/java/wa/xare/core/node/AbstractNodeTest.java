package wa.xare.core.node;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import wa.xare.core.packet.ProcessingListener;
import wa.xare.core.packet.ProcessingResult;

public class AbstractNodeTest {

	AbstractNode node;

	@Before
	public void prepare() {
		node = spy(AbstractNode.class);
	}

	@Test
	public void testNotifyProcessingListeners() throws Exception {
		ProcessingListener l1 = mock(ProcessingListener.class);
		ProcessingListener l2 = mock(ProcessingListener.class);
		node.addProcessingListener(l1);
		node.addProcessingListener(l2);

		node.notifyProcessingListeners(mock(ProcessingResult.class));
		verify(l1).done(any());
		verify(l2).done(any());
	}

}
