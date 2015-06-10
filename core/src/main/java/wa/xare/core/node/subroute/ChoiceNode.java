package wa.xare.core.node.subroute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import wa.xare.core.node.Node;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.packet.Packet;

public class ChoiceNode extends DefaultSubRouteNode {

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


}
