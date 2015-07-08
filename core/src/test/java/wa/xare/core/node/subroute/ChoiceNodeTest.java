package wa.xare.core.node.subroute;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import io.vertx.core.json.JsonObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import wa.xare.core.api.Node;
import wa.xare.core.api.Packet;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.packet.DefaultPacket;
import wa.xare.core.selector.JsonPathSelector;
import wa.xare.core.selector.Selector;


@RunWith(MockitoJUnitRunner.class)
public class ChoiceNodeTest {

  private ChoiceNode node;

  JsonObject obj1 = new JsonObject("{\"value\":1}");
  JsonObject obj2 = new JsonObject("{\"value\":2}");
  JsonObject obj3 = new JsonObject("{\"value\":3}");

  @Mock
  Node afterFilterOne;

  @Mock
  Node afterFilterTwo;

  @Mock
  Node afterOtherwise;

  @Before
  public void prepare() {
    node = new ChoiceNode();
    PipelineNode otherwisePipeline = new PipelineNode();
    otherwisePipeline.addNode(afterOtherwise);

    node.setOtherwise(otherwisePipeline);

    Selector s1 = new JsonPathSelector("$.[?(@.value==1)]");
    Selector s2 = new JsonPathSelector("$.[?(@.value==2)]");
    FilterNode filter1 = new FilterNode();
    FilterNode filter2 = new FilterNode();

    filter1.setSelector(s1);
    filter2.setSelector(s2);

    filter1.addNode(afterFilterOne);
    filter2.addNode(afterFilterTwo);

    node.addNode(filter1);
    node.addNode(filter2);
  }

  @Test
  public void testChoice() throws Exception {
    Packet packet = new DefaultPacket();
    packet.setBody(obj1);

    node.doProcess(packet);
    verify(afterFilterOne).startProcessing(packet);
    verify(afterFilterTwo, never()).startProcessing(packet);
    verify(afterOtherwise, never()).startProcessing(packet);

    reset(afterFilterOne, afterFilterTwo, afterOtherwise);

    packet.setBody(obj2);

    node.doProcess(packet);
    verify(afterFilterOne, never()).startProcessing(packet);
    verify(afterFilterTwo).startProcessing(packet);
    verify(afterOtherwise, never()).startProcessing(packet);

    reset(afterFilterOne, afterFilterTwo, afterOtherwise);

    packet.setBody(obj3);

    node.doProcess(packet);
    verify(afterFilterOne, never()).startProcessing(packet);
    verify(afterFilterTwo, never()).startProcessing(packet);
    verify(afterOtherwise).startProcessing(packet);

  }


}
