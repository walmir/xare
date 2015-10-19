package wa.xare.core.node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import io.vertx.core.json.JsonArray;

import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;

import wa.xare.core.builder.NodeBuilder;
import wa.xare.core.builder.NodeDefinition;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.ProcessingResult;

public class PipelineNodeTest {

  @Test
  public void testConfigurePipelineNode() {

    PipelineNode node = (PipelineNode) new NodeBuilder(null).getNodeInstance(buildPipelineConfiguration());

    List<Node> nodes = node.getNodes();
    assertThat(nodes).hasSize(2);
    assertThat(nodes.get(0)).isInstanceOf(LoggerNode.class);
    assertThat(nodes.get(1)).isInstanceOf(LoggerNode.class);
    assertThat(((LoggerNode) nodes.get(0)).getLevel()).isEqualTo("info");
    assertThat(((LoggerNode) nodes.get(1)).getLevel()).isEqualTo("debug");

  }

  @Test
  public void testTraverse() throws Exception {
    Packet packet = mock(Packet.class);
    Node node1 = spy(new GoodNode());
    Node node2 = spy(new GoodNode());
    
    InOrder inorder = inOrder(node1, node2);

    PipelineNode node = new PipelineNode();
    node.addNode(node1);
    node.addNode(node2);

    node.startProcessing(packet);

    inorder.verify(node1).startProcessing(packet);
    inorder.verify(node2).startProcessing(packet);

  }

  private NodeConfiguration buildPipelineConfiguration() {

    NodeConfiguration loggerConfig = new NodeConfiguration()
        .withType(LoggerNode.TYPE_NAME);
    loggerConfig.put("level", "info");

    NodeConfiguration loggerConfig2 = new NodeConfiguration()
        .withType(LoggerNode.TYPE_NAME);
    loggerConfig2.put("level", "debug");

    JsonArray nodes = new JsonArray();
    nodes.add(loggerConfig);
    nodes.add(loggerConfig2);

    NodeConfiguration pipelineConfig = new NodeConfiguration();
    pipelineConfig.put("type", "pipeline");
    pipelineConfig.put("nodes", nodes);

    return pipelineConfig;
  }

  public class GoodNode extends AbstractNode {

    @Override
    public void startProcessing(Packet packet) {
      notifyProcessingListeners(ProcessingResult
          .successfulProcessingResult(packet));
    }

  }

}
