package wa.xare.core;

import java.util.List;

import wa.xare.core.node.Node;
import wa.xare.core.node.ProcessingListener;
import wa.xare.core.packet.Packet;

public interface ProcessingChain {

	void addNode(Node node);

	void traverse(Packet packet);

	void addProcessingListener(ProcessingListener listener);

	List<Node> getNodes();

}
