package wa.xare.core.node;

import wa.xare.core.Route;
import wa.xare.core.packet.Packet;

public interface Node {

	void startProcessing(Packet packet);

	void addProcessingListener(ProcessingListener listener);

	void setRoute(Route route);

}
