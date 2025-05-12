package io.tunabytes;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LdcSwap.LdcSwaps.class)
public @interface LdcSwap {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface LdcSwaps {
        LdcSwap[] value();
    }

    /**
     * The method name that is being targeted, Default to the method's name
     *
     * @return The target method name to inject into.
     */
    String method() default "";

    String targetLdc();

    String newLdc();

    int ldcSkipped() default 0;

    int applyCount() default 1;
}
