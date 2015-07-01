package wa.xare.core.node;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Container;

import wa.xare.core.DefaultRoute;

@RunWith(MockitoJUnitRunner.class)
public class PipelineNodeTest {

  @Mock
  private DefaultRoute route;

  @Before
  public void prepare() {
    Container container = mock(Container.class);
    when(route.getContainer()).thenReturn(container);
    when(container.logger()).thenReturn(mock(Logger.class));
  }

  private NodeConfiguration buildPipelineConfiguration() {

    NodeConfiguration loggerConfig = new NodeConfiguration()
        .withType(NodeType.LOGGER);
    loggerConfig.putString("level", "info");
    
    NodeConfiguration loggerConfig2 = new NodeConfiguration()
    .withType(NodeType.LOGGER);
    loggerConfig2.putString("level", "info");

    JsonArray nodes = new JsonArray();
    nodes.add(loggerConfig);
    nodes.add(loggerConfig2);
    
    
    NodeConfiguration pipelineConfig = new NodeConfiguration();
    pipelineConfig.putString("type", "pipeline");
    pipelineConfig.putArray("nodes", nodes);

    return pipelineConfig;
  }

  @Test
  public void testConfigurePipelineNode() {
    PipelineNode node = new PipelineNode();
    node.configure(route, buildPipelineConfiguration());

    System.out.println(node.getNodes().get(0));
  }

}
