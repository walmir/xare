package wa.xare.core.node.subroute;

import java.util.Collections;
import java.util.List;

import wa.xare.core.ProcessingChain;
import wa.xare.core.node.DefaultNodeProcessingChain;
import wa.xare.core.node.DefaultRouteNode;
import wa.xare.core.node.Node;
import wa.xare.core.packet.Packet;
import wa.xare.core.selector.Selector;

/**
 * Represents a node that starts a subroute that is traversed independently from
 * the main route.
 * 
 * @author Wajdi
 */
public abstract class DefaultSubRouteNode extends DefaultRouteNode implements
    ProcessingChain {

  private Selector selector;

  private ProcessingChain nodeChain;

  @Override
  public void addNode(Node node) {
    if (nodeChain == null) {
      nodeChain = new DefaultNodeProcessingChain();
    }
    nodeChain.addNode(node);
  }

  @Override
  public void traverse(Packet packet) {
    nodeChain.traverse(packet);
  }

  @Override
  public List<Node> getNodes() {
    return nodeChain == null ? Collections.emptyList() : nodeChain.getNodes();
  }

  public ProcessingChain getNodeChain() {
    return nodeChain;
  }

  public void setNodeChain(ProcessingChain nodeChain) {
    this.nodeChain = nodeChain;
  }

}
