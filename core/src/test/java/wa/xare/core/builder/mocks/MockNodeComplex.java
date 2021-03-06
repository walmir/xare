package wa.xare.core.builder.mocks;

import java.util.List;

import wa.xare.core.Route;
import wa.xare.core.annotation.Field;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.node.Node;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.ProcessingListener;

public class MockNodeComplex implements Node {

  @Field(required = false)
  public String accessible;

  @Field(required = false)
  public String remainsEmpty;

  @Field
  private String stringField;

  @Field
  private int intField;

  @Field(value = "bool", required = false)
  private boolean booleanField;

  @Field(required = false)
  private String[] strings;

  @Field(value = "theList", required = true)
  private List<Integer> intList;

  public String getStringField() {
    return stringField;
  }

  public void setStringField(String stringField) {
    this.stringField = stringField;
  }

  public int getIntField() {
    return intField;
  }

  public void setIntField(int intField) {
    this.intField = intField;
  }

  public boolean isBooleanField() {
    return booleanField;
  }

  public void setBooleanField(boolean booleanField) {
    this.booleanField = booleanField;
  }

  public String[] getStrings() {
    return strings;
  }

  public void setStrings(String[] strings) {
    this.strings = strings;
  }

  public List<Integer> getIntList() {
    return intList;
  }

  public void setIntList(List<Integer> theList) {
    this.intList = theList;
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
