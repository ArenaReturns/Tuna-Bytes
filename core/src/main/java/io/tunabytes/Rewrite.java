package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.objectweb.asm.tree.MethodNode;

/**
 * An annotation to edit the asm method directly.
 * The method on which this method is applied must be abstract.
 * <p>
 * The mixins method name and signature must exactly match the targeted method.
 * That is:
 * <ul>
 *     <li>The method's name. This can also be specifind in {@link Rewrite#value()}</li>
 *     <li>The method's return type</li>
 *     <li>The method's parameter types in order</li>
 * </ul>
 * <p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Rewrite {

    /**
     * The name of the method being rewritten.
     *
     * @return The method name
     */
    String value() default "";
    
    Class<? extends Rewritter> runtimeRewriter();

    interface Rewritter {
      public void rewrite(MethodNode list);
    }
}
