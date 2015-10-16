package wa.xare.core.node.subroute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import wa.xare.core.annotation.Field;
import wa.xare.core.annotation.NodeType;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.node.Node;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.packet.Packet;

@NodeType(ChoiceNode.TYPE_NAME)
public class ChoiceNode extends DefaultSubRouteNode {

  public static final String TYPE_NAME = "choice";

  public static final String CASES_FIELD = "cases";
  public static final String OTHERWISE_FIELD = "otherwise";

  @Field(CASES_FIELD)
  private List<FilterNode> whereNodes;

  @Field(OTHERWISE_FIELD)
  private PipelineNode otherwise;

  public ChoiceNode() {
    whereNodes = new ArrayList<>();
    otherwise = new PipelineNode();
  }

  public ChoiceNode(PipelineNode otherwiseChain) {
    if (otherwise == null) {
      throw new IllegalArgumentException(
          "'otherwise' node chain cannot be null");
    }
    otherwise = otherwiseChain;
  }

  @Override
  public void doProcess(Packet packet) {
    Optional<FilterNode> nodeOption = whereNodes.stream()
        .filter(n -> n.passesFilter(packet)).findFirst();

    if (nodeOption.isPresent()) {
      nodeOption.get().startProcessing(packet);
    } else {
      otherwise.startProcessing(packet);
    }
  }

  @Override
  public void addNode(Node node) {
    if (node instanceof FilterNode){
      whereNodes.add((FilterNode) node);
    } else {
      throw new IllegalArgumentException(
          "only filter nodes can be added to a choice node");
    }
  }

  public void setOtherwise(PipelineNode chain) {
    otherwise = chain;
  }

  @Override
  protected void doConfigure(NodeConfiguration configuration) {
    // if (!configuration.containsKey(CASES_FIELD)) {
    // throw new NodeConfigurationException("choice", CASES_FIELD);
    // }
    //
    // Object whereElement = configuration.getValue(CASES_FIELD);
    // if (whereElement instanceof JsonArray) {
    // ((JsonArray) whereElement).iterator().forEachRemaining(
    // caseNodeConfig -> {
    // NodeConfiguration filterConfig = new NodeConfiguration(
    // (JsonObject) caseNodeConfig);
    // filterConfig.setType("filter");
    // Node filter = NodeDefinitionBuilder.getInstance().getNodeInstance(
    // route, filterConfig);
    // this.addNode(filter);
    // });
    // } else { // JsonObject
    // Node filter = NodeDefinitionBuilder.getInstance().getNodeInstance(route,
    // new NodeConfiguration((JsonObject) whereElement));
    // this.addNode(filter);
    // }
    // if (configuration.containsKey(OTHERWISE_FIELD)) {
    // JsonArray otherwisePathConfig = configuration
    // .getJsonArray(OTHERWISE_FIELD);
    // if (otherwisePathConfig != null) {
    //
    // // Build pipeline configuration
    // NodeConfiguration pipelineConfig = new NodeConfiguration();
    // pipelineConfig.setType(PipelineNode.TYPE);
    // pipelineConfig.put(NODES_FIELD, otherwisePathConfig);
    // PipelineNode pipeline = (PipelineNode) NodeDefinitionBuilder
    // .getInstance().getNodeInstance(route, pipelineConfig);
    // this.setOtherwise(pipeline);
    // }
    // }

  }

}
