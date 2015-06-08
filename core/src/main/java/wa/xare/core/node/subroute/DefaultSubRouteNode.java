package wa.xare.core.node.subroute;

import java.util.Collections;
import java.util.List;

import wa.xare.core.ProcessingChain;
import wa.xare.core.node.DefaultNodeProcessingChain;
import wa.xare.core.node.DefaultRouteNode;
import wa.xare.core.node.Node;
import wa.xare.core.node.ProcessingResult;
import wa.xare.core.packet.Packet;

/**
 * Represents a node that starts a subroute that is traversed independently from
 * the main route.
 * 
 * @author Wajdi
 */
public abstract class DefaultSubRouteNode extends DefaultRouteNode implements
    ProcessingChain {

  private ProcessingChain nodeChain;

  @Override
  public void addNode(Node node) {
    if (nodeChain == null) {
      nodeChain = new DefaultNodeProcessingChain();
      nodeChain.addProcessingListener(this::notifyProcessingListeners);
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

  @Override
  public final void startProcessing(Packet packet) {
    doProcess(packet);
    notifyProcessingListeners(ProcessingResult
        .successfulProcessingResult(packet));
  }

  abstract void doProcess(Packet packet);

}
