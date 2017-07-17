package wa.xare.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import wa.xare.core.annotation.Field;
import wa.xare.core.node.Node;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.node.endpoint.Endpoint;
import wa.xare.core.packet.Packet;

import java.util.List;

public class DefaultRoute extends AbstractVerticle implements Route {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DefaultRoute.class);

  @Field("name")
  private String name;

  @Field(value = "version", required = false)
  private String version;

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

  public void setPipeline(PipelineNode pipeline) {
    this.pipeline = pipeline;
  }

  @Override
  public void start() {
    JsonObject config = getVertx().getOrCreateContext().config();
    LOGGER.info("starting route " + name);
    LOGGER.debug(config);
    initRoute();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }

  @Override
  public String getDeploymentId() {
    return deploymentID();
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  private void endRoute(boolean success, Packet packet) {
    if (success) {
      LOGGER.debug("route finished successfully");
    } else {
      LOGGER.error("route finished with errors");
    }
  }

  private void initRoute() {
    incomingEndpoint.deploy();
    incomingEndpoint.setHandler(pipeline::startProcessing);
    pipeline.addProcessingListener(result -> endRoute(result.isSuccessful(), result.getResultingPacket()));
    pipeline.initialize();
  }





}
