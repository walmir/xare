package wa.xare.core.builder;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wa.xare.core.node.Node;

public class NodeConfigurator {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(NodeConfigurator.class);

  private Class<Node> nodeClass;

  private Map<String, Field> constructorFields;
  private Map<String, Field> requiredFields;
  private Map<String, Field> optionalFields;

  private Constructor<?> constructor;

  public NodeConfigurator(Class<Node> c) {
    this.nodeClass = c;

    constructorFields = new HashMap<>();
    requiredFields = new HashMap<>();
    optionalFields = new HashMap<>();

    this.constructor = fetchConstructor();
    loadFields();
  }

  private void loadFields() {
    // load constructor fields
    if (constructor.getParameters().length > 0) {
      
    }
    
    List<Field> fields = getAllAnnotatedFields(nodeClass);
    
    for (Field f : fields){
      wa.xare.core.annotation.Field annotation = f
          .getAnnotation(wa.xare.core.annotation.Field.class);
      if (annotation.required()) {
        requiredFields.put(f.getName(), f);
      } else {
        optionalFields.put(f.getName(), f);
      }
    }
  }

  private Constructor<?> fetchConstructor() {

    Constructor<?> constructor = null;
    Constructor<?>[] constructors = nodeClass.getConstructors();

    if (constructors.length == 1) {
      return constructors[0];
    }

    if (constructors.length == 0) {
      throw new NodeConfigurationException("no constructor found for class "
          + nodeClass.getName());
    }
    // else -> there are more than one constructor
    LOGGER.warn("more than one constructor found for class '"
        + nodeClass.getName()
        + "'. The one with the least parameters will be used. ");

    for (Constructor<?> cons : constructors) {
      int length = cons.getParameters().length;
      if (length == 0) {
        return cons;
      }
      if (constructor == null) {
        constructor = cons;
      } else {
        if (cons.getParameters().length < constructor.getParameters().length) {
          constructor = cons;
        }
      }
    }
    return constructor;
  }

  private List<Field> getAllAnnotatedFields(Class<?> type) {
    List<Field> fields = new ArrayList<>();

    for (Field f : type.getDeclaredFields()) {
      if (f.getAnnotation(wa.xare.core.annotation.Field.class) != null) {
        fields.add(f);
      }
    }

    for (Class<?> c = type; c != null; c = c.getSuperclass()) {
      for (Field f : c.getDeclaredFields()) {
        if (f.getAnnotation(wa.xare.core.annotation.Field.class) != null) {
          fields.add(f);
        }
      }
    }
    return fields;
  }

  public Node instantiateNode(Map<String, String> fieldValues) {

    return null;
  }

  public Node instantiateNode(Map<String, String> fieldValues,
      Map<String, String> selectorFieldValues) {
    return null;
  }

}
