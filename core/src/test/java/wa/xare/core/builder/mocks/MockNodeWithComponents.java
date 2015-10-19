package wa.xare.core.builder.mocks;

import wa.xare.core.Route;
import wa.xare.core.annotation.Field;
import wa.xare.core.annotation.NodeType;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.node.Node;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.ProcessingListener;

@NodeType
public class MockNodeWithComponents implements Node {

  @Field(required = true)
  private IMockComponent component;

  @Field
  private MockNodeComplex simpleNode;

  public IMockComponent getComponent() {
    return component;
  }

  public void setComponent(IMockComponent component) {
    this.component = component;
  }

  public MockNodeComplex getSimpleNode() {
    return simpleNode;
  }

  public void setSimpleNode(MockNodeComplex simpleNode) {
    this.simpleNode = simpleNode;
  }

  @Override
  public void startProcessing(Packet packet) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addProcessingListener(ProcessingListener listener) {
    // TODO Auto-generated method stub

  }

}
