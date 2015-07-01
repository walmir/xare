package wa.xare.core.node.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NodeType {

  /**
   * <p>
   * Defines the type of the node. If not set the name of the class is used
   * instead, removing the suffix {@code Node} if it existed.
   * </p>
   * 
   */
  String value() default "";

}
