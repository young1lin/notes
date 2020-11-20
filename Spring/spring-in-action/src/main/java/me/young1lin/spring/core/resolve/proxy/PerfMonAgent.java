package me.young1lin.spring.core.resolve.proxy;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/11/20 8:46 上午
 */
public class PerfMonAgent {
    private static Instrumentation inst = null;

    public static void premain(String agentArgs, Instrumentation _inst) {
        System.out.println("PerfMonAgent#premain was called.");
        // Initialize the static variables we use to track information
        inst = _inst;
        // Set up the class-file transformer.
        ClassFileTransformer trans = new PerfMonTransformer();
        System.out.println("Adding a PerfMonTransformer instance to the JVM");
        inst.addTransformer(trans);
    }
}
