package io.github.mcengine.api;

public class MCEngineCurrencyApiUtil {
    /**
     * Initializes a database by dynamically loading the specified class and invoking its constructor
     * with the provided arguments.
     *
     * @param className         the fully qualified name of the class to load
     * @param constructorArgs   the arguments to pass to the constructor of the class
     * @return an instance of the initialized database object
     * @throws Exception if the class cannot be loaded, the constructor cannot be found, or instantiation fails
     */
    public static Object initializeDatabase(String className, Object... constructorArgs) throws Exception {
        Class<?> clazz = Class.forName(className);
        Class<?>[] parameterTypes = new Class[constructorArgs.length];
        for (int i = 0; i < constructorArgs.length; i++) {
            parameterTypes[i] = mapWrapperToPrimitive(constructorArgs[i].getClass());
        }
        return clazz.getConstructor(parameterTypes).newInstance(constructorArgs);
    }

    /**
     * Invokes a specified method on a given object with the provided arguments.
     *
     * @param databaseInstance  the instance of the object on which to invoke the method
     * @param methodName        the name of the method to invoke
     * @param args              the arguments to pass to the method
     * @throws RuntimeException if the method invocation fails
     */
    public static void invokeMethod(Object databaseInstance, String methodName, Object... args) {
        try {
            Class<?>[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = mapWrapperToPrimitive(args[i].getClass());
            }
            databaseInstance.getClass().getMethod(methodName, argTypes).invoke(databaseInstance, args);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking method '" + methodName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Maps wrapper classes to their corresponding primitive types. If the class is not a wrapper
     * for a primitive type, it returns the input class unchanged.
     *
     * @param clazz the class to map
     * @return the primitive type corresponding to the wrapper class, or the input class if no mapping exists
     */
    public static Class<?> mapWrapperToPrimitive(Class<?> clazz) {
        if (clazz == Double.class) return double.class;
        if (clazz == Integer.class) return int.class;
        if (clazz == Long.class) return long.class;
        if (clazz == Boolean.class) return boolean.class;
        if (clazz == Float.class) return float.class;
        if (clazz == Character.class) return char.class;
        if (clazz == Byte.class) return byte.class;
        if (clazz == Short.class) return short.class;
        return clazz;
    }
}
