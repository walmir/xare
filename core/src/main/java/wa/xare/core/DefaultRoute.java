package wa.xare.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;

import wa.xare.core.annotation.Field;
import wa.xare.core.builder.NodeBuilder;
import wa.xare.core.configuration.EndpointConfiguration;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.configuration.RouteConfiguration;
import wa.xare.core.node.Node;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.node.endpoint.Endpoint;
import wa.xare.core.node.endpoint.EndpointDirection;
import wa.xare.core.packet.Packet;

public class DefaultRoute extends AbstractVerticle implements Route {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DefaultRoute.class);

  private String name;

  @Field("from")
  private Endpoint incomingEndpoint;

  @Field("nodes")
  private PipelineNode pipeline;

  public Endpoint getIncomingEndpoint() {
    return incomingEndpoint;
  }

  public void setIncomingEndpoint(Endpoint incomingEndpoint) {
    this.incomingEndpoint = incomingEndpoint;
  }

  public PipelineNode getPipeline() {
    return pipeline;
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
    RouteConfiguration config = new RouteConfiguration(config());
    configureRoute(config);

    LOGGER.info("starting route " + name);
  }

  private void endRoute(boolean success, Packet packet) {
    if (success) {
      LOGGER.debug("route finished successfully");
    } else {
      LOGGER.error("route finished with errors");
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
    // NodeBuilder builder = new NodeBuilder(this);
    // for (NodeConfiguration nc : nodeConfigs) {
    // logger.info("Node " + nc);
    // this.addNode(builder.buildNode(nc));
    // }
    NodeBuilder builder = NodeBuilder.getInstance();
    for (NodeConfiguration nc : nodeConfigs) {
      Node n = builder.getNodeInstance(this, nc);
      this.addNode(n);
    }
  }

  private void configureIncomingEndpoint(EndpointConfiguration inpointConfig) {
    inpointConfig.setEndpointDirection(EndpointDirection.INCOMING);
    inpointConfig.setType("endpoint");

    incomingEndpoint = NodeBuilder.getInstance()
        .getEndpointInstance(this, inpointConfig);

    // this.incomingEndpoint = endpointBuilder.buildEndpoint(inpointConfig);
    if (incomingEndpoint != null) {
      if (pipeline == null) {
        throw new IllegalStateException("processing chain cannot be null");
      }
      incomingEndpoint.setRoute(this);
      incomingEndpoint.setHandler(pipeline::startProcessing);
      incomingEndpoint.deploy();
    }
  }

  public void setPipeline(PipelineNode pipeline) {
    this.pipeline = pipeline;
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }

  // @Override
  // public HttpServer getServer() {
  // if (server == null) {
  // server = vertx.createHttpServer();
  // }
  // return server;
  // }
  
  

}
