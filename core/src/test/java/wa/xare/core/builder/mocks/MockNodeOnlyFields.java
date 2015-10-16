package wa.xare.core.builder.mocks;

import wa.xare.core.annotation.Field;
import wa.xare.core.annotation.NodeType;

@NodeType
public class MockNodeOnlyFields {

  public static final String NAMED_FIELD_NAME = "otherName";

  @Field(required = true)
  private String stringField;

  @Field(required = true)
  private int intField;

  @Field
  private boolean booleanField;

  @Field
  private int[] intArray;

  @Field(NAMED_FIELD_NAME)
  private String namedField;

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

  public int[] getIntArray() {
    return intArray;
  }

  public void setIntArray(int[] intArray) {
    this.intArray = intArray;
  }

  public String getNamedField() {
    return namedField;
  }

  public void setNamedField(String namedField) {
    this.namedField = namedField;
  }
  
  

}
