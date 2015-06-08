package wa.xare.core.node.subroute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import wa.xare.core.ProcessingChain;
import wa.xare.core.node.DefaultNodeProcessingChain;
import wa.xare.core.node.Node;
import wa.xare.core.packet.Packet;

public class ChoiceNode extends DefaultSubRouteNode {

  private List<FilterNode> whereNodes;

  private ProcessingChain otherwise;

  public ChoiceNode() {
    whereNodes = new ArrayList<>();
    otherwise = new DefaultNodeProcessingChain();
  }

  public ChoiceNode(ProcessingChain otherwiseChain) {
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
      otherwise.traverse(packet);
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

  public void setOtherwise(ProcessingChain chain) {
    otherwise = chain;
  }


}
