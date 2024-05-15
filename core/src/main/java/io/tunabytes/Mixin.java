package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The main entrypoint for a tuna mixin class. This annotation marks that
 * the annotated member will be manipulating the specified class.
 * <p>
 * This annotation will be scanned by {@link io.tunabytes.ap.MixinsProcessor the annotation processor},
 * which is required for tuna mixins to work.
 * <p>
 * This annotation can be added to <em>classes</em> and <em>interfaces</em>:
 * <ul>
 *     <li>Classes will get necessary bytecode cloned into the class being
 *     manipulated. This includes fields and methods.</li>
 *     <li>Interfaces will be implemented by the class being manipulated, in
 *     which abstract methods must <strong>only</strong> be {@link Accessor accessor} methods.
 *     Interfaces can define {@link Inject} and {@link Overwrite} methods as well, as long
 *     as methods are default and not abstract.</li>
 * </ul>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Mixin {

    /**
     * The class we're targeting.
     *
     * @return The class we will be mixing into
     */
    Class<?> value() default Object.class;

    /**
     * The name of the class, an alternative way in case of inaccessible classes
     *
     * @return The name of the class
     */
    String name() default "";

    boolean enumTarget() default false;

  /**
   * Tell if the parent of the mixin is just a placeholder used for compiling the mixin.
   * It is generally a bad idea to make a mixin extend a class
   *  if you want this class to have some mixins as well, the
   *  hierarchical linking will mess with the classes load order.
   * If you just want to use the super keyword in your mixin code, set this to true.
   *  The calls to the parent class will be remapped to the parent of the target class of the mixin.
   * 
   * You can alternatively use a mixin like this if your field/method from the parent class is public :
   * <pre>
   *    @Mixin(Child.class)
   *    public abstract class OverridenChildMixin {
   *
   *    @Overwrite("foo")
   *    public void foo() {
   *      Parent parent = (Parent) (Object) this;
   *      parent.foo()
   *    }
   * </pre>
   * 
   * @return Whether the parent of the mixin is a placeholder for mixin compilation.
   */
  boolean withFakeParentAccessor() default false;
}
