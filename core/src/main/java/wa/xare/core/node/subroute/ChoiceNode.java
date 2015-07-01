package wa.xare.core.node.subroute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;

import wa.xare.core.node.Node;
import wa.xare.core.node.NodeConfiguration;
import wa.xare.core.node.NodeConfigurationException;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.node.builder.NodeType;
import wa.xare.core.node.builder.ScanningNodeBuilder;
import wa.xare.core.packet.Packet;

@NodeType
public class ChoiceNode extends DefaultSubRouteNode {

  public static final String CASES_FIELD = "cases";
  public static final String OTHERWISE_FIELD = "otherwise";

  private List<FilterNode> whereNodes;

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
    if (!configuration.containsField(CASES_FIELD)) {
      throw new NodeConfigurationException("choice", CASES_FIELD);
    }
    JsonElement whereElement = configuration.getElement(CASES_FIELD);
    if (whereElement instanceof JsonArray) {
      ((JsonArray) whereElement).iterator().forEachRemaining(
          caseNodeConfig -> {
            NodeConfiguration filterConfig = new NodeConfiguration(
                (JsonObject) caseNodeConfig);
            filterConfig.setType("filter");
            Node filter = ScanningNodeBuilder.getInstance().getNodeInstance(
                route, filterConfig);
            this.addNode(filter);
          });
    } else { // JsonObject
      Node filter = ScanningNodeBuilder.getInstance().getNodeInstance(route,
          new NodeConfiguration((JsonObject) whereElement));
      this.addNode(filter);
    }
    if (configuration.containsField(OTHERWISE_FIELD)) {
      JsonArray otherwisePathConfig = configuration.getArray(OTHERWISE_FIELD);
      if (otherwisePathConfig != null) {

        // Build pipeline configuration
        NodeConfiguration pipelineConfig = new NodeConfiguration();
        pipelineConfig.setType(PipelineNode.TYPE);
        pipelineConfig.putArray(NODES_FIELD, otherwisePathConfig);
        PipelineNode pipeline = (PipelineNode) ScanningNodeBuilder
            .getInstance().getNodeInstance(route, pipelineConfig);
        this.setOtherwise(pipeline);
      }
    }

  }

}
