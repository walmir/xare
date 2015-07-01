package wa.xare.core.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import wa.xare.core.node.builder.NodeType;
import wa.xare.core.node.builder.ScanningNodeBuilder;
import wa.xare.core.packet.Packet;

@NodeType
public class PipelineNode extends AbstractNode {

  public static final String TYPE = "pipeline";

  private List<Node> nodes;

  List<ProcessingListener> processingListeners;

  // @Override
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

  // @Override
  // protected void notifyProcessingListeners(ProcessingResult result) {
  // for (ProcessingListener pl : processingListeners) {
  // pl.done(result);
  // }
  // }

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

  @Override
  protected void doConfigure(NodeConfiguration configuration) {
    JsonArray array = configuration.getArray("nodes");
    for (Object obj : array) {
      if (obj instanceof JsonObject) {
        NodeConfiguration config = new NodeConfiguration((JsonObject) obj);
        Node node = ScanningNodeBuilder.getInstance().getNodeInstance(route,
            config);
        addNode(node);
      }
    }
  }

}
