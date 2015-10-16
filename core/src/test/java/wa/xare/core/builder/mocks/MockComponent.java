package wa.xare.core.builder.mocks;

import wa.xare.core.annotation.Field;

public class MockComponent implements IMockComponent {

  @Field
  private String componentField;

  public String getComponentField() {
    return componentField;
  }

  public void setComponentField(String componentField) {
    this.componentField = componentField;
  }

}
