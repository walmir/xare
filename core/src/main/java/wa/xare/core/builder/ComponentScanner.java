package wa.xare.core.builder;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

import java.beans.Introspector;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wa.xare.core.annotation.Component;
import wa.xare.core.annotation.EndpointType;
import wa.xare.core.annotation.NodeType;
import wa.xare.core.node.Node;
import wa.xare.core.node.endpoint.Endpoint;

/**
 * Functions as a wrapper for {@link FastClasspathScanner} and exposes
 * functionality to scan for Nodes, Endpoints and other Node-Components.
 *
 * @author Wajdi
 *
 */
public class ComponentScanner {

  private static final String NODE_NAME_SUFFIX = "Node";
  private static final String ENDPOINT_NAME_SUFFIX = "Endpoint";

  private final HashMap<String, Class<?>> nodeClassMap;
  private final HashMap<String, Class<?>> endpointClassMap;
  private final HashMap<String, Map<String, Class<?>>> componentClassMap;

  private static volatile ComponentScanner INSTANCE;

  private ComponentScanner() {
    nodeClassMap = new HashMap<>();
    endpointClassMap = new HashMap<>();
    componentClassMap = new HashMap<>();
  }

  public void initialScan() {
    scanForNodes();
    scanForEndpoints();
    scanForComponents();
  }

  public void cleanComponents() {
    nodeClassMap.clear();
    endpointClassMap.clear();
    componentClassMap.clear();
  }

  public synchronized static ComponentScanner getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ComponentScanner();
    }
    return INSTANCE;
  }

  public HashMap<String, Class<?>> scanForNodes(String... packages) {

    final HashMap<String, Class<?>> nodeMap = new HashMap<>();
    new FastClasspathScanner(packages)
        .matchClassesWithAnnotation(
            NodeType.class,
            c -> {
              if (c.getClass().isInstance(Node.class)) {
                nodeMap.put(getNodeName(c, NODE_NAME_SUFFIX), c);
              } else {
                throw new NodeConfigurationException(
                    "Class '"
                        + c.getName()
                        + "' is not of type Node, and cannot be annotated with @NodeType");
              }
            }).scan();

    nodeClassMap.putAll(nodeMap);
    return nodeMap;
  }

  public HashMap<String, Class<?>> scanForEndpoints(String... packages) {
    final HashMap<String, Class<?>> endpointMap = new HashMap<>();

    new FastClasspathScanner(packages)
        .matchClassesWithAnnotation(
            EndpointType.class,
            c -> {
              if (c.getClass().isInstance(Endpoint.class)) {
                endpointMap.put(getEndpointName(c), c);
              } else {
                throw new NodeConfigurationException(
                    "Class '"
                        + c.getName()
                        + "' is not of type Endpoint, and cannot be annotated with @EndpointType");
              }
            }).scan();

    endpointClassMap.putAll(endpointMap);
    return endpointMap;
  }

  public HashMap<String, Map<String, Class<?>>> scanForComponents(
      String... packages) {
    final List<Class<?>> componentList = new ArrayList<>();

    new FastClasspathScanner(packages).matchClassesWithAnnotation(
        Component.class, c -> {
          componentList.add(c);
        }).scan();

    HashMap<String, Map<String, Class<?>>> components = scanComponents(componentList);
    componentClassMap.putAll(components);
    return components;
  }

  public HashMap<String, Class<?>> getNodeClassMap() {
    return nodeClassMap;
  }

  public HashMap<String, Class<?>> getEndpointClassMap() {
    return endpointClassMap;
  }

  public HashMap<String, Map<String, Class<?>>> getComponentClassMap() {
    return componentClassMap;
  }

  private HashMap<String, Map<String, Class<?>>> scanComponents(
      List<Class<?>> componentList) {
    final HashMap<String, Map<String, Class<?>>> map = new HashMap<>();

    FastClasspathScanner scanner = new FastClasspathScanner();
    for (Class<?> componentClass : componentList) {
      final String groupName = getComponentName(componentClass, "");
      if (!map.containsKey(componentClass)) {
        map.put(groupName, new HashMap<>());
      }
      if (componentClass.isInterface()) {
        // Scan
        scanner.matchClassesImplementing(componentClass, c -> {
          if (!Modifier.isAbstract(c.getModifiers())) {
            map.get(groupName).put(getComponentName(c, groupName), c);
          }
        });
      } else if (Modifier.isAbstract(componentClass.getModifiers())) {
        // Scan
        scanner.matchSubclassesOf(componentClass, c -> {
          if (!Modifier.isAbstract(c.getModifiers())) {
            map.get(groupName).put(getComponentName(c, groupName), c);
          }
        });
      }
    }
    scanner.scan();

    return map;
  }

  private String getEndpointName(Class<?> c) {
    EndpointType annotation = c.getAnnotation(EndpointType.class);
    if (annotation.value() != null && !annotation.value().isEmpty()) {
      return annotation.value();
    }
    return getNodeName(c, ENDPOINT_NAME_SUFFIX);
  }

  private String getComponentName(Class<?> c, String suffix) {
    Component annotation = c.getAnnotation(Component.class);
    if (annotation != null && annotation.value() != null
        && !annotation.value().isEmpty()) {
      return annotation.value();
    }
    return getNodeName(c, suffix);
  }

  private String getNodeName(Class<?> c, String suffix) {
    NodeType annotation = c.getAnnotation(NodeType.class);
    if (annotation != null && annotation.value() != null
        && !annotation.value().isEmpty()) {
      return annotation.value();
    }
    String simpleName = BuilderUtils.decapitalize(c.getSimpleName());
    if (simpleName.toLowerCase().endsWith(suffix.toLowerCase())) {
      return simpleName.substring(0, simpleName.length() - suffix.length());
    }
    return simpleName;
  }

}
