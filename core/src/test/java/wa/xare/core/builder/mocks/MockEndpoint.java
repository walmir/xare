package wa.xare.core.builder.mocks;

import wa.xare.core.Route;
import wa.xare.core.annotation.EndpointType;
import wa.xare.core.annotation.Field;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.node.endpoint.Endpoint;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.ProcessingListener;

@EndpointType
public class MockEndpoint implements Endpoint {

  @Field
  private String someString;

  public String getSomeString() {
    return someString;
  }

  public void setSomeString(String someString) {
    this.someString = someString;
  }

  @Override
  public void configure(Route route, NodeConfiguration configuration) {
    // TODO Auto-generated method stub

  }

  @Override
  public void startProcessing(Packet packet) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addProcessingListener(ProcessingListener listener) {
    // TODO Auto-generated method stub

  }

  @Override
  public void deploy() {
    // TODO Auto-generated method stub

  }

  @Override
  public void setHandler(EndpointHandler handler) {
    // TODO Auto-generated method stub

  }

}
