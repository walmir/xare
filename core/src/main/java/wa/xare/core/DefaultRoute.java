package wa.xare.core;

import java.util.List;

import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import wa.xare.core.node.Node;
import wa.xare.core.node.NodeBuilder;
import wa.xare.core.node.NodeConfiguration;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.node.endpoint.Endpoint;
import wa.xare.core.node.endpoint.EndpointBuilder;
import wa.xare.core.node.endpoint.EndpointConfiguration;
import wa.xare.core.packet.Packet;

public class DefaultRoute extends Verticle implements Route {

  private Logger logger;

  private EndpointBuilder endpointBuilder;

  private String name;

  private Endpoint incomingEndpoint;

  private PipelineNode pipeline;

  public Endpoint getIncomingEndpoint() {
    return incomingEndpoint;
  }

  public void setIncomingEndpoint(Endpoint incomingEndpoint) {
    this.incomingEndpoint = incomingEndpoint;
  }

  public List<Node> getPipeline() {
    return pipeline.getNodes();
  }

  public void setNodes(List<Node> nodes) {
    for (Node n : nodes) {
      this.addNode(n);
    }
  }

  public void addNode(Node node) {
    if (node == null) {
      throw new IllegalArgumentException("node cannot be null");
    }
    if (pipeline == null) {
      pipeline = new PipelineNode();
    }
    node.setRoute(this);
    pipeline.addNode(node);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public void start() {
    logger = container.logger();
    endpointBuilder = new EndpointBuilder(this);

    RouteConfiguration config = new RouteConfiguration(container.config());
    configureRoute(config);

    logger.info("starting route " + name);
  }

  private void endRoute(boolean success, Packet packet) {
    if (success) {
      logger.debug("route finished successfully");
    } else {
      logger.error("route finished with errors");
    }
  }

  private void configureRoute(RouteConfiguration routeConfig) {
    name = routeConfig.getName();

    List<NodeConfiguration> nodeConfigs = routeConfig.getNodeConfigurations();
    configureNodes(nodeConfigs);

    pipeline.addProcessingListener(result -> {
      endRoute(result.isSuccessful(), result.getResultingPacket());
    });

    EndpointConfiguration inpointConfig = routeConfig
        .getIncomingEndpointConfiguration();

    if (inpointConfig != null) {
      configureIncomingEndpoint(inpointConfig);
    } // TODO: else consider throwing exception
  }

  private void configureNodes(List<NodeConfiguration> nodeConfigs) {
    NodeBuilder builder = new NodeBuilder(this);
    for (NodeConfiguration nc : nodeConfigs) {
      logger.info("Node " + nc);
      this.addNode(builder.buildNode(nc));
    }
  }

  private void configureIncomingEndpoint(EndpointConfiguration inpointConfig) {
    this.incomingEndpoint = endpointBuilder.buildEndpoint(inpointConfig);
    if (incomingEndpoint != null) {
      if (pipeline == null) {
        throw new IllegalStateException("processing chain cannot be null");
      }
      incomingEndpoint.setHandler(pipeline::startProcessing);
      incomingEndpoint.deploy();
    }
  }

  public void setPipline(PipelineNode pipeline) {
    this.pipeline = pipeline;
  }

}
