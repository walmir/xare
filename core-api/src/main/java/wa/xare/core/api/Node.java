package wa.xare.core.api;

import wa.xare.core.api.configuration.NodeConfiguration;
import wa.xare.core.api.processing.ProcessingListener;


public interface Node {

  void configure(Route route, NodeConfiguration configuration);

  void startProcessing(Packet packet);

	void addProcessingListener(ProcessingListener listener);

	void setRoute(Route route);

}
