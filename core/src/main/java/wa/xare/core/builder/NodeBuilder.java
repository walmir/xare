package wa.xare.core.builder;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

import wa.xare.core.Route;
import wa.xare.core.annotation.Component;
import wa.xare.core.node.Node;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.node.endpoint.Endpoint;

public class NodeBuilder {

  private Route route;
  private NodeDefinitionBuilder nodeDefinitionBuilder;

  public NodeBuilder(Route route) {
    this.route = route;
    this.nodeDefinitionBuilder = NodeDefinitionBuilder.getInstance();
  }

  public Node getNodeInstance(JsonObject configuration) {
    return getNodeInstance(configuration, null);
  }

  private Node getNodeInstance(JsonObject configuration, Class<?> componentType) {
    String type = configuration.getString(Node.NODE_TYPE_FIELD);
    if ("endpoint".equals(type)) {
      return getEndpointInstance(route, configuration);
    }

    if (type == null && componentType != null) {
      type = BuilderUtils.getNodeName(componentType);
    }

    NodeDefinition nodeDef = nodeDefinitionBuilder.getNodeDefinition(type);
    if (nodeDef == null) {
      throw new NodeConfigurationException("unkown class type: " + type);
    }
    return (Node) createNodeInstance(nodeDef, configuration);
  }

  public Endpoint getEndpointInstance(Route route, JsonObject configuration) {

    String endpointType = configuration.getString(Endpoint.ENDPOINT_TYPE_FIELD);

    if (nodeDefinitionBuilder.getEndpointDefinition(endpointType) == null) {
      throw new NodeConfigurationException("unkown endpoint class type: " + endpointType);
    }

    NodeDefinition endpointDefinition = nodeDefinitionBuilder.getEndpointDefinition(endpointType);

    Endpoint endpoint = (Endpoint) createNodeInstance(endpointDefinition, configuration);
    endpoint.setRoute(route);

    return endpoint;
  }

  private Object createNodeInstance(NodeDefinition nodeDefinition, JsonObject configuration) {
    try {
      // Instantiate
      Object node = nodeDefinition.getNodeClass().newInstance();

      Map<String, PropertyDescriptor> propertyDescriptorMap =
          Arrays.stream(Introspector.getBeanInfo(nodeDefinition.getNodeClass())
              .getPropertyDescriptors()).collect(Collectors.toMap(PropertyDescriptor::getName, pd -> pd));

      // insert field vaues
      insertFieldValues(node, nodeDefinition.getRequiredFields(), configuration, propertyDescriptorMap, true);
      insertFieldValues(node, nodeDefinition.getOptionalFields(), configuration, propertyDescriptorMap, false);

      return node;

    } catch (InstantiationException | IllegalAccessException e) {
      throw new NodeConfigurationException(String.format("The node '%s' could not be instantiated",
          nodeDefinition.getNodeClass().getName()), e);
    } catch (IntrospectionException e1) {
      throw new NodeConfigurationException("unexpeted introsepction error", e1);
    }
  }

  /**
   * Builds an instance for the given field.
   * TODO: Simplify method. Too complex code.
   *
   * @param fieldName
   * @param field
   * @param configuration
   * @return
   */
  private Object getComponentInstance(String fieldName, Field field, JsonObject configuration) {

    String discriminator = "type";
    String componentName = "";
    Component componentAnnotation = field.getType().getAnnotation(Component.class);
    if (componentAnnotation != null) {
      discriminator = componentAnnotation.discriminator();
      componentName = componentAnnotation.value();
    }

    if (componentName.trim().isEmpty()) {
      String simpleName = field.getType().getSimpleName();
      componentName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    NodeDefinition componentDef;
    JsonObject componentConfig = null;
    String discriminatorValue = null;

    Object componentConfigValue = configuration.getValue(fieldName);
    if (componentConfigValue instanceof JsonObject) {
      componentConfig = ((JsonObject) componentConfigValue);
      discriminatorValue = componentConfig.getString(discriminator);
    }

    if (Endpoint.class.isAssignableFrom(field.getType())) {
      componentDef = nodeDefinitionBuilder.getEndpointDefinition(discriminatorValue);
    } else if (PipelineNode.class.isAssignableFrom(field.getType())) {
      // Pipeline is a special case, since the configuration could be just an array of nodes
      Object pipelineConfig = configuration.getValue(fieldName);
      if (pipelineConfig instanceof JsonArray) {
        componentConfig = new JsonObject();
        componentConfig.put(discriminator, componentName); // type is pipeline
        componentConfig.put("nodes", pipelineConfig);
      }
      discriminatorValue = "pipeline";

      componentDef = nodeDefinitionBuilder.getNodeDefinition(discriminatorValue);
    } else {
      if ((discriminatorValue == null || discriminatorValue.isEmpty())
          && (!field.getType().isInterface() && !Modifier.isAbstract(field.getType().getModifiers()))) {
        // If field type is a concrete node, no need for type value in config -> insert implicit value
        discriminatorValue = componentName;
        assert componentConfig != null;
        componentConfig.put(discriminator, discriminatorValue);
      }

      if (discriminatorValue == null) {
        throw new NodeConfigurationException(
            String.format("discriminator value for discriminator '%s', for component '%s', is not defined",
                discriminator, componentName));
      }

      if (Node.class.isAssignableFrom(field.getType())) {
        componentDef = nodeDefinitionBuilder.getNodeDefinition(discriminatorValue);
      } else {
        componentDef = nodeDefinitionBuilder.getComponentContainer(componentName)
            .getComponentDefinition(discriminatorValue);
      }
    }

    return createNodeInstance(componentDef, componentConfig);
  }

  @SuppressWarnings("unchecked")
  private void insertFieldValues(Object node,
                                 Map<String, Field> fields,
                                 JsonObject configuration,
                                 Map<String, PropertyDescriptor> propertyDescriptorMap,
                                 boolean required) {

    for (String fieldName : fields.keySet()) {
      Field field = fields.get(fieldName);
      Object fieldValue = getFieldValue(fieldName, field, configuration);

      if (fieldValue == null) {
        if (required) {
          throw new NodeConfigurationException(String.format(
              "value for required field '%s' is not defined", fieldName));
        }
        continue;
      }

      try {

        // Check if enum
        if (field.getType().isEnum()) {
          String enumString = ((String) fieldValue).toUpperCase();
          fieldValue = Enum.valueOf((Class<Enum>) field.getType(), enumString);
        }

        if (Modifier.isPublic(field.getModifiers())) {
          field.set(node, configuration.getString(fieldName));
        } else {
          // If field is not accessible, try to use the setter method.
          // Setter method is not based on the definition field name (which
          // can be set in the annotation), but the actual field name
          String actualFieldName = fields.get(fieldName).getName();
          if (propertyDescriptorMap.containsKey(actualFieldName)
              && propertyDescriptorMap.get(actualFieldName).getWriteMethod() != null) {
            propertyDescriptorMap.get(actualFieldName).getWriteMethod()
                .invoke(node, fieldValue);
          } else {
            // Field value cannot be set.
            throw new NodeConfigurationException(String.format(
                "the field '%s' is neither public nor has a setter method",
                fieldName));
          }
        }
      } catch (IllegalArgumentException | IllegalAccessException
          | InvocationTargetException e) {
        throw new NodeConfigurationException(String.format(
            "the value of the field '%s' could not be set for node class '%s'",
            fieldName, node.getClass()), e);
      }
    }

  }

  private Object getFieldValue(String fieldName, Field field, JsonObject configuration) {

    Class<?> fieldType = field.getType();
    Object fieldValue = null;

    if (!configuration.containsKey(fieldName)) {
      return null;
    }

    if (Node.class.isAssignableFrom(field.getType())
        || isNodeComponent(fieldType)) {
      fieldValue = getComponentInstance(fieldName, field, configuration);
    } else if (fieldType.isArray()) {
      // copy array values to a new array
      fieldValue = copyJsonArrayToArray(fieldType.getComponentType(),
          configuration.getJsonArray(fieldName));
    } else if (Collection.class.isAssignableFrom(fieldType)) {
      Class<?> componentType = getParameterizedType(field);
      fieldValue = Arrays.asList(copyJsonArrayToArray(componentType,
          configuration.getJsonArray(fieldName)));
    } else {
      fieldValue = configuration.getValue(fieldName);
    }
    return fieldValue;
  }

  private Class<?> getParameterizedType(Field field) {
    return (Class<?>) ((ParameterizedType) field.getGenericType())
        .getActualTypeArguments()[0];
  }

  private Object[] copyJsonArrayToArray(Class<?> componentType,
      JsonArray jsonArray) {

    // get component type (if list get parameterized type)
    Object[] dst = (Object[]) Array.newInstance(componentType, jsonArray.size());
    for (int i = 0; i < jsonArray.size(); i++) {
      if (componentType.isArray()) {
        Array.set(dst, i,
            copyJsonArrayToArray(componentType.getComponentType(),
                jsonArray.getJsonArray(i)));
      } else if (Collections.class.isAssignableFrom(componentType)) {
        throw new NodeConfigurationException(
            "Collection nesting is currently not supported. Try array nesting, or a collection of arrays.");
      } else if (Node.class.isAssignableFrom(componentType)) {
        Node nodeInstance = getNodeInstance(jsonArray.getJsonObject(i), componentType);
        Array.set(dst, i, nodeInstance);
      } else {
        Array.set(dst, i, jsonArray.getValue(i));
      }
    }
    return dst;
  }


  private boolean isNodeComponent(Class<?> type) {
    if (type == null) {
      return false;
    }

    Annotation[] annotations = type.getAnnotations();
    for (Annotation a : annotations) {
      if (a instanceof Component) {
        return true;
      }
    }
    // // not component, maybe superClass
    // if (isNodeComponent(type.getSuperclass())){
    // return true;
    // }
    //
    // // not component, try interfaces
    // for (Class<?> inter : type.getInterfaces()) {
    // if (isNodeComponent(inter)) {
    // return true;
    // }
    // }
    return false;
  }

  }
