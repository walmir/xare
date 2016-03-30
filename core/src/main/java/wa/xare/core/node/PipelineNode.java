package wa.xare.core.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wa.xare.core.annotation.Field;
import wa.xare.core.annotation.NodeType;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.ProcessingListener;
import wa.xare.core.packet.ProcessingResult;

@NodeType
public class PipelineNode extends AbstractNode {

  @Field
  private List<Node> nodes;

  List<ProcessingListener> processingListeners;

  public void addNode(Node node) {

    if (nodes == null) {
      nodes = new ArrayList<Node>();
    }

    if (nodes.isEmpty()) {
      nodes.add(node);
      node.addProcessingListener(this::notifyProcessingListeners);
    } else {
      Node last = nodes.get(nodes.size() - 1);
      last.addProcessingListener(res -> {
        if (res.isSuccessful()) {
          node.startProcessing(res.getResultingPacket());
        } else {
          notifyProcessingListeners(res);
        }
      });
      nodes.add(node);
    }

  }

  @Override
  public void initialize() {

    nodes.stream().reduce(this, (n1, n2) -> {
      n1.addProcessingListener(res -> {
        if (res.isSuccessful()) {
          n2.startProcessing(res.getResultingPacket());
        } else {
          notifyProcessingListeners(res);
        }
      });
      n2.initialize();
      return n2;
    });

  }

  public void setNodes(List<Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public void startProcessing(Packet packet) {
    if (nodes != null && !nodes.isEmpty()) {
      nodes.get(0).startProcessing(packet);
    } else {
      ProcessingResult result = ProcessingResult
          .successfulProcessingResult(packet);
      notifyProcessingListeners(result);
    }
  }

  @Override
  public void addProcessingListener(ProcessingListener listener) {
    if (processingListeners == null) {
      processingListeners = new ArrayList<>();
    }
    processingListeners.add(listener);

  }

  public List<Node> getNodes() {
    if (nodes == null) {
      return Collections.emptyList();
    }
    return nodes;
  }

}
