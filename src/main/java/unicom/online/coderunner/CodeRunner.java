package unicom.online.coderunner;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.openhft.compiler.CompilerUtils;

public class CodeRunner {

    public static void main(String[] args) {

        // declaring variables.
        final String className = System.getenv("CLASS_NAME");
        final String javaCode = System.getenv("JAVA_CODE");

        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("Java class name required");
        }
        if (javaCode == null || javaCode.isEmpty()) {
            throw new IllegalArgumentException("Java source code required");
        }

        // compile and run code.
        StringWriter writer = new StringWriter();
        ClassLoader anonymousClassLoader = new ClassLoader() {
        };
        try {
            Class<?> clazz = CompilerUtils.CACHED_COMPILER
                    .loadFromJava(anonymousClassLoader, className, javaCode, new PrintWriter(writer));
            Method mainMethod = clazz.getMethod("main", String[].class);
            mainMethod.invoke(clazz.newInstance(), new Object[]{null});
        } catch (InvocationTargetException e) {
            System.err.println(e.getTargetException());
        } catch (ClassNotFoundException | NoSuchMethodException  | SecurityException | InstantiationException | IllegalAccessException  e) {
            System.err.println(e.getMessage());
        }
        if (!writer.toString().isEmpty()) {
            System.err.println(writer.toString());
        }
    }
}
