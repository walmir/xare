package wa.xare.core.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import wa.xare.core.builder.mocks.MockClass;

@RunWith(MockitoJUnitRunner.class)
public class NodeDefinitionTest {

  // @Mock Class<?> nodeMockClass;
  
//  @Mock Field mockRequiredField;
//  @Mock Field mockOptionalField;
//  @Mock Field mockUnannotatedField;

  @Mock wa.xare.core.annotation.Field mockRequiredFieldAnnotation;
  @Mock wa.xare.core.annotation.Field mockOptionalFieldAnnotation;

  @Before
  public void setUp() {
    // when(nodeMockClass.getDeclaredFields()).thenReturn(
    // new Field[] { mockRequiredField, mockOptionalField,
    // mockUnannotatedField });
    //
    // when(mockRequiredField.getAnnotation(wa.xare.core.annotation.Field.class))
    // .thenReturn(mockRequiredFieldAnnotation);
    // when(mockOptionalField.getAnnotation(wa.xare.core.annotation.Field.class))
    // .thenReturn(mockOptionalFieldAnnotation);
    // when(mockUnannotatedField.getAnnotation(wa.xare.core.annotation.Field.class))
    // .thenReturn(null);
    //
    // when(mockRequiredFieldAnnotation.required()).thenReturn(true);
    // when(mockOptionalFieldAnnotation.required()).thenReturn(false);

  }

  @Test
  public void testNodeDefinitionInstantiation() throws Exception {

    NodeDefinition definition = new NodeDefinition(MockClass.class);
    Map<String, Field> requiredFields = definition.getRequiredFields();
    Map<String, Field> optionalFields = definition.getOptionalFields();

    assertThat(definition.getNodeClass()).isEqualTo(MockClass.class);

    assertThat(requiredFields).hasSize(1);
    assertThat(optionalFields).hasSize(2);

    assertThat(requiredFields).containsOnlyKeys(MockClass.REQUIRED_FIELD_NAME);
    assertThat(optionalFields).containsOnlyKeys(MockClass.OPTIONAL_FIELD_NAME,
        MockClass.NAMED_FIELD_NAME);

    // assertThat(requiredFields.get(REQUIRED_FIELD_NAME)).isEqualTo(
    // mockRequiredField);
    // assertThat(optionalFields.get(OPTIONAL_FIELD_NAME)).isEqualTo(
    // mockOptionalField);
  }

  // @Test
  // public void testInstantiationWithConstructors() {
  //
  // NodeDefinition definition = new NodeDefinition(
  // MockClassWithConstructors.class);
  // Map<String, Field> requiredFields = definition.getRequiredFields();
  // Map<String, Field> optionalFields = definition.getOptionalFields();
  //
  // assertThat(requiredFields).isEmpty();
  // assertThat(optionalFields).hasSize(1);
  // assertThat(optionalFields).containsOnlyKeys(
  // MockClassWithConstructors.SOME_FIELD_NAME);
  //
  // assertThat(constructor.getParameterCount()).isEqualTo(2);
  // assertThat(Arrays.stream(constructor.getParameters()).map(p -> p.getName())
  // .collect(Collectors.toList())).containsOnly(
  // MockClassWithConstructors.CONSTRUCTOR_FIELD_NAME,
  // MockClassWithConstructors.ANNOTATED_CONSTRUCTOR_FIELD_NAME);
  // }
  
}
