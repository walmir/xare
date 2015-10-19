package wa.xare.core.node.subroute;

import java.util.Collections;
import java.util.List;

import wa.xare.core.ProcessingChain;
import wa.xare.core.Route;
import wa.xare.core.annotation.Field;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.node.AbstractNode;
import wa.xare.core.node.Node;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.ProcessingResult;

/**
 * Represents a node that starts a subroute that is traversed independently from
 * the main route.
 * 
 * @author Wajdi
 */
public abstract class DefaultSubRouteNode extends AbstractNode implements
    ProcessingChain {

  public static final String NODES_FIELD = "nodes";

  @Field(NODES_FIELD)
  private PipelineNode pipeline;

  @Override
  public void addNode(Node node) {
    if (pipeline == null) {
      pipeline = new PipelineNode();
      pipeline.addProcessingListener(this::notifyProcessingListeners);
    }
    pipeline.addNode(node);
  }

  @Override
  public void traverse(Packet packet) {
    pipeline.startProcessing(packet);
  }

  @Override
  public List<Node> getNodes() {
    return pipeline == null ? Collections.emptyList() : pipeline.getNodes();
  }

  public PipelineNode getPipeline() {
    return pipeline;
  }

  public void setPipeline(PipelineNode pipeline) {
    this.pipeline = pipeline;
  }

  @Override
  public final void startProcessing(Packet packet) {
    doProcess(packet);
    notifyProcessingListeners(ProcessingResult
        .successfulProcessingResult(packet));
  }

  abstract void doProcess(Packet packet);

}
