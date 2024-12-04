package io.github.mcengine.api;

public class MCEngineCurrencyApiUtil {
    public static Object initializeDatabase(String className, Object... constructorArgs) throws Exception {
        Class<?> clazz = Class.forName(className);
        Class<?>[] parameterTypes = new Class[constructorArgs.length];
        for (int i = 0; i < constructorArgs.length; i++) {
            parameterTypes[i] = mapWrapperToPrimitive(constructorArgs[i].getClass());
        }
        return clazz.getConstructor(parameterTypes).newInstance(constructorArgs);
    }

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
