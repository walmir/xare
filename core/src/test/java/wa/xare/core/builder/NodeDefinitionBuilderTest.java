package wa.xare.core.builder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class NodeDefinitionBuilderTest {

  @Mock
  NodeDefinitionBuilder builder;

  @Before
  public void setUp() {
    // builder.
    // NodeDefinitionBuilder.setInstance(builder);
    // when(builder.getNodeDefinition(type)))
  }

  @Test
  public void test() {
    NodeDefinitionBuilder builder = NodeDefinitionBuilder.getInstance();
    System.out.println(builder);
  }

}
