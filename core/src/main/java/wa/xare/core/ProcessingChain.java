package wa.xare.core;

import java.util.List;

import wa.xare.core.annotation.Component;
import wa.xare.core.node.Node;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.ProcessingListener;

@Component
public interface ProcessingChain {

	void addNode(Node node);

	void traverse(Packet packet);

	void addProcessingListener(ProcessingListener listener);

	List<Node> getNodes();

}
