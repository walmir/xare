package wa.xare.core;

import java.util.List;

import wa.xare.core.api.Node;
import wa.xare.core.api.Packet;
import wa.xare.core.api.processing.ProcessingListener;

public interface ProcessingChain {

	void addNode(Node node);

	void traverse(Packet packet);

	void addProcessingListener(ProcessingListener listener);

	List<Node> getNodes();

}
