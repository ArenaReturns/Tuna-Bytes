package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SwitchMerger {
    /**
     * The method name that is being targeted, Default to the method's name
     *
     * @return The target method name to inject into.
     */
    String method() default "";
    
    int[] removeBranches() default {};
    
    int switchOrdinal() default 0;
}
