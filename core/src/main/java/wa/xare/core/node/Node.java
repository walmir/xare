package wa.xare.core.node;

import wa.xare.core.Route;
import wa.xare.core.annotation.Component;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.ProcessingListener;

@Component
public interface Node {

  String NODE_TYPE_FIELD = "type";

//  void configure(Route route, NodeConfiguration configuration);

  void startProcessing(Packet packet);

	void addProcessingListener(ProcessingListener listener);

  default void setRoute(Route route) {
  }

  default void initialize() {
  }

}
