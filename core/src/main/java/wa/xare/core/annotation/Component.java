package wa.xare.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * Defines a component of a route (i.e. a Node) or a component of node, for example selectors.
 * @author Wajdi
 *
 */
public @interface Component {

  /** Defines the name of the component */
  String value() default "";
  
  /**
   * Defines the json field that identifies the concrete type or class of this
   * component
   */
  String discriminator() default "type";
}
