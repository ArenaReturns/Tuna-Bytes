package io.tunabytes.bytecode;

import io.tunabytes.bytecode.introspect.MixinInfo;
import io.tunabytes.bytecode.introspect.MixinMethod;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for narrowing down the target method of a mixin.
 */
public final class MethodNarrower {

    /**
     * Set this to false to log the narrowing process for all mixins, not just the ones that fail.
     */
    public static boolean LOG_WORK_ONLY_ON_ERROR = true;
    
    public static MethodNode tryNarrow(ClassNode clazzToMixin, MixinInfo fullInfo, MixinMethod method) {
        String targetMethodName = method.getTargetMethodName();
        List<MethodNode> collected = clazzToMixin.methods.stream()
                                         .filter(c -> c.name.equals(targetMethodName))
                                         .collect(Collectors.toList());
        StringBuilder workTrace = new StringBuilder();
        String error = " target method(s) found for mixin:\n\t" + fullInfo.getMixinName() + "#" + targetMethodName + "(...)\n\t\tpatching =>\n\t" +
                           clazzToMixin.name.replace("/", ".") + "#";
        String targetMethodDescriptor = "..." + targetMethodName + "(...)";
        String tip = "";
        String argumentsStr = "";
        if (collected.size() > 1) {
            Type[] arguments = Type.getArgumentTypes(method.getRealDescriptor());
            Type[] substringArgument = Arrays.copyOf(arguments, Math.min(method.getLastParameterArgIndex(), arguments.length));
            argumentsStr = Arrays.stream(substringArgument).map(Type::toString).collect(Collectors.joining());
            targetMethodDescriptor = "..." + targetMethodName + "(" + argumentsStr + ")";
            workTrace.append("[info] " + collected.size() + error + targetMethodDescriptor +"\n\tTrying to narrowing it down with args: "
                                   + targetMethodDescriptor);

            collected = collected.stream()
                            .filter(m -> {
                                String desc = m.desc;
                                return Arrays.equals(Type.getArgumentTypes(desc), substringArgument);
                            }).collect(Collectors.toList());
            if (method.getLastParameterArgIndex() == Integer.MAX_VALUE) {
                tip = "\n\t Did you forget a @StackSeparator ? \n";
            }
        }

        if (collected.size() > 1) {
            String mixinDescriptorWithoutStackSeparator = "(" + argumentsStr + ")" + method.getDescriptor().getReturnType().toString();
            workTrace.append("[info] " + collected.size() + error + targetMethodDescriptor +"\n\tTrying to narrowing it down with return type: "
                                   + targetMethodDescriptor);
            collected = collected.stream().filter(m -> m.desc.equals(mixinDescriptorWithoutStackSeparator)).collect(Collectors.toList());
        }

        if (collected.size() > 1) {
            System.out.println(workTrace);
            throw new InvalidMixinException("Too much matches !\n" + collected.size() + error + targetMethodDescriptor
                                                + "\n" + collected.stream().map(methodNode -> "- " + methodNode.desc)
                                                             .collect(Collectors.joining("\n")) + tip);
        }

        if (collected.isEmpty()) {
            System.out.println(workTrace);
            throw new InvalidMixinException("No" + error + targetMethodDescriptor + tip);
        }
        
        if (! LOG_WORK_ONLY_ON_ERROR) {
            System.out.println(workTrace);
        }

        return collected.get(0);
    }
}
