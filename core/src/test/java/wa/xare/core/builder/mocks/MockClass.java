package wa.xare.core.builder.mocks;

import wa.xare.core.annotation.Field;

public class MockClass {
  
  public static final String REQUIRED_FIELD_NAME = "requiredField";
  public static final String OPTIONAL_FIELD_NAME = "optionalField";
  public static final String NAMED_FIELD_NAME = "differentName";

  @Field
  private String requiredField;
  
  @Field(required = false)
  private String optionalField;
  
  @Field(value = NAMED_FIELD_NAME, required = false)
  private String namedField;

  public String getRequiredField() {
    return requiredField;
  }

  public void setRequiredField(String requiredField) {
    this.requiredField = requiredField;
  }

  public String getOptionalField() {
    return optionalField;
  }

  public void setOptionalField(String optionalField) {
    this.optionalField = optionalField;
  }

  public String getNamedField() {
    return namedField;
  }

  public void setNamedField(String namedField) {
    this.namedField = namedField;
  }
  
}
