package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark a parameter of the mixin as the end of the target method's parameter stack and
 * the start of the mixin's local variables.
 * 
 * <p> Since a mixin must have the same signature as the target method, this annotation is used to declare
 * a mixin method with more parameters than the target method. The parameters after this annotation will
 * be considered as the mixin's local variables.
 * <p> The JVM automatically pushes the parameters of a method to the stack so we use this quirk to compile mixins
 * that must be injected anywhere in the target method without corrupting the original method's stack.
 * 
 * <p> Example:
 * Target method:
 * <pre>
 *     void method(int a, int b) {
 *         int c = a + b;
 *         int d = a * b;
 *         int e = a - b;
 *         System.out.println(c + d + e);
 *     }
 *         
 * </pre>
 * We want to inject a mixin that adds a new variable f = c * d * e and prints it.
 * <pre>
 *     @Mixin(method = "method", at = Inject.At.END)
 *     void mixin(int a, int b, @StackSeparator int c, int d, int e) {
 *          int f = c * d * e;
 *          System.out.println(f);
 *     }
 * </pre>
 * See the <b>Angler</b> module for more examples.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface StackSeparator {
}
