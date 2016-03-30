package wa.xare.core.builder;

import wa.xare.core.annotation.Component;
import wa.xare.core.annotation.EndpointType;
import wa.xare.core.annotation.NodeType;

import java.beans.Introspector;

/**
 * Created by wajdi on 28/10/15.
 */
public final class BuilderUtils {

  public static final String ENDPOINT_NAME_SUFFIX = "Endpoint";
  public static final String NODE_NAME_SUFFIX = "Node";

  public static String getNodeName(Class<?> c) {
    NodeType annotation = c.getAnnotation(NodeType.class);
    if (annotation.value() != null && !annotation.value().isEmpty()) {
      return annotation.value();
    }
    return getNodeName(c, NODE_NAME_SUFFIX);
  }

  public static String getEndpointName(Class<?> c) {
    EndpointType annotation = c.getAnnotation(EndpointType.class);
    if (annotation.value() != null && !annotation.value().isEmpty()) {
      return annotation.value();
    }
    return getNodeName(c, ENDPOINT_NAME_SUFFIX);
  }

  public static String getComponentName(Class<?> c, String suffix) {
    Component annotation = c.getAnnotation(Component.class);
    if (annotation.value() != null && !annotation.value().isEmpty()) {
      return annotation.value();
    }
    return getNodeName(c, suffix);
  }

  public static String getNodeName(Class<?> c, String suffix) {
    String simpleName = decapitalize(c.getSimpleName());
    if (simpleName.endsWith(suffix)) {
      return simpleName.substring(0, simpleName.length() - suffix.length());
    }
    return simpleName;
  }

  public static String decapitalize(String string) {
    if (string == null || string.isEmpty()) {
      return string;
    }
    String dec = string.substring(0, 1).toLowerCase();
    if (string.length() > 1) {
      dec += string.substring(1);
    }
   return dec;
  }
}
