package wa.xare.core.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wa.xare.core.ProcessingChain;
import wa.xare.core.packet.Packet;

public class DefaultNodeProcessingChain implements ProcessingChain {

  private List<Node> nodes;

  List<ProcessingListener> processingListeners;

  @Override
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

  private void notifyProcessingListeners(ProcessingResult result) {
    for (ProcessingListener pl : processingListeners) {
      pl.done(result);
    }
  }

  @Override
  public void traverse(Packet packet) {
    if (nodes != null && !nodes.isEmpty()) {
      nodes.get(0).startProcessing(packet);
    }
  }

  @Override
  public void addProcessingListener(ProcessingListener listener) {
    if (processingListeners == null) {
      processingListeners = new ArrayList<>();
    }
    processingListeners.add(listener);

  }

  @Override
  public List<Node> getNodes() {
    if (nodes == null) {
      return Collections.emptyList();
    }
    return nodes;
  }

}
