package wa.xare.core.builder;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wa.xare.core.annotation.Component;
import wa.xare.core.node.Node;

public class NodeDefinition {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(NodeDefinition.class);

  private Class<?> nodeClass;

  private final Map<String, Field> requiredFields;
  private final Map<String, Field> optionalFields;

  // private Constructor<?> constructor;

  public NodeDefinition(Class<?> c) {
    this.nodeClass = c;

    requiredFields = new HashMap<>();
    optionalFields = new HashMap<>();

    loadFields();
  }

  protected Map<String, Field> getRequiredFields() {
    return requiredFields;
  }

  protected Map<String, Field> getOptionalFields() {
    return optionalFields;
  }

  private void loadFields() {
    // load required and optional fields
    List<Field> fields = getAllAnnotatedFields(nodeClass);
    for (Field f : fields){
      wa.xare.core.annotation.Field annotation = f
          .getAnnotation(wa.xare.core.annotation.Field.class);

      // Check if field is named in annotation
      String name = annotation.value().trim().isEmpty() ? f.getName() : annotation.value().trim();

      if (annotation.required()) {
        requiredFields.put(name, f);
      } else {
        optionalFields.put(name, f);
      }
    }
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

  private List<Field> getAllAnnotatedComponentsFields(Class<?> type) {
    List<Field> fields = new ArrayList<>();

    for (Field f : type.getDeclaredFields()) {
      if (f.getAnnotation(wa.xare.core.annotation.Field.class) != null) {
        fields.add(f);
      }
    }

    for (Class<?> c = type; c != null; c = c.getSuperclass()) {
      for (Field f : c.getDeclaredFields()) {
        if (f.getAnnotation(Component.class) != null) {
          fields.add(f);
        }
      }
    }
    return fields;
  }

  public Class<?> getNodeClass() {
    return nodeClass;
  }

  public JsonObject getTemplate(){
    JsonArray fields = new JsonArray();
    for (Map.Entry<String, Field> entry : requiredFields.entrySet()) {
      JsonObject field = new JsonObject();
      field.put("name", entry.getKey());
      field.put("type", entry.getValue().getType().getSimpleName().toLowerCase());
      if (entry.getValue().getAnnotation(Component.class) != null){
        field.put("category", "component");
      } else {
        field.put("category", "field");
      }
      field.put("required", true);
      fields.add(field);
    }

    for (Map.Entry<String, Field> entry : optionalFields.entrySet()) {
      JsonObject field = new JsonObject();
      field.put("name", entry.getKey());
      field.put("type", entry.getValue().getType().getSimpleName());
      field.put("required", false);
      fields.add(field);
    }

    JsonObject node = new JsonObject();
    node.put("type", nodeClass.getSimpleName());
    node.put("fields", fields);
    return node;
  }

  // public Constructor<?> getConstructor() {
  // return constructor;
  // }

}
