package wa.xare.core.node.subroute;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.json.JsonObject;

import wa.xare.core.node.Node;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.packet.DefaultPacket;
import wa.xare.core.packet.Packet;
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

  // private NodeConfiguration buildChoiceNodeConfig() {
  // NodeConfiguration choiceConfig = new NodeConfiguration()
  // .withType(NodeType.CHOICE);
  // JsonArray cases = new JsonArray();
  //
  // // 1st Selector
  // SelectorConfiguration selector1Config = new SelectorConfiguration()
  // .withExpressionLanguage(
  // SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE)
  // .withExpression("$.[?(@.value==1)]");
  //
  // SelectorConfiguration selector2Config = new SelectorConfiguration()
  // .withExpressionLanguage(
  // SelectorConfiguration.JSON_PATH_EXPRESSION_LANGUAGE)
  // .withExpression("$.[?(@.value==2)]");
  //
  // JsonObject case1 = new JsonObject();
  // case1.putObject("selector", selector1Config);
  //
  // JsonObject case2 = new JsonObject();
  // case2.putObject("selector", selector2Config);
  //
  // JsonArray nodePath1 = new JsonArray();
  // nodePath1.add(end1);
  // JsonArray nodePath2 = new JsonArray();
  // nodePath2.add(end2);
  // JsonArray nodePathOtherwise = new JsonArray();
  // nodePathOtherwise.add(endOtherwise);
  //
  // case1.putArray("nodes", nodePath1);
  // case2.putArray("nodes", nodePath2);
  //
  // cases.add(case1);
  // cases.add(case2);
  //
  // choiceConfig.putArray("cases", cases);
  // choiceConfig.putArray("otherwise", nodePathOtherwise);
  // }

}
