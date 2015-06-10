package wa.xare.core.node.subroute;

import java.util.Collections;
import java.util.List;

import wa.xare.core.ProcessingChain;
import wa.xare.core.node.AbstractNode;
import wa.xare.core.node.Node;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.node.ProcessingResult;
import wa.xare.core.packet.Packet;

/**
 * Represents a node that starts a subroute that is traversed independently from
 * the main route.
 * 
 * @author Wajdi
 */
public abstract class DefaultSubRouteNode extends AbstractNode implements
    ProcessingChain {

  private PipelineNode pipline;

  @Override
  public void addNode(Node node) {
    if (pipline == null) {
      pipline = new PipelineNode();
      pipline.addProcessingListener(this::notifyProcessingListeners);
    }
    pipline.addNode(node);
  }

  @Override
  public void traverse(Packet packet) {
    pipline.startProcessing(packet);
  }

  @Override
  public List<Node> getNodes() {
    return pipline == null ? Collections.emptyList() : pipline.getNodes();
  }

  public PipelineNode getPipline() {
    return pipline;
  }

  public void setPipline(PipelineNode pipeline) {
    this.pipline = pipeline;
  }

  @Override
  public final void startProcessing(Packet packet) {
    doProcess(packet);
    notifyProcessingListeners(ProcessingResult
        .successfulProcessingResult(packet));
  }

  abstract void doProcess(Packet packet);

}
