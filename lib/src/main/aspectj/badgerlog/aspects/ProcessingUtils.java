package badgerlog.aspects;

import badgerlog.annotations.Entry;
import badgerlog.annotations.NoEntry;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.utilities.KeyParser;
import badgerlog.utilities.Members;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public class ProcessingUtils {

    public static <T extends Member & AnnotatedElement> String createKeyFromMember(Configuration config, T member, Object instance, int instanceCount) {
        String key;
        if (Members.isMemberNonStatic(member)) {
            key = KeyParser.createKeyFromMember(config, member, instance, instanceCount);
        } else {
            key = KeyParser.createKeyFromStaticMember(config, member);
        }

        return key;
    }

    public static boolean isValidForClassGeneration(Field field) {
        return Members.isMemberNonStatic(field) && !Modifier.isFinal(field.getModifiers()) && !field
                .isAnnotationPresent(NoEntry.class) && !field.isAnnotationPresent(Entry.class);
    }

    public static String getStringFromClass(Class<?> clazz) {
        if (clazz == null) {
            return "";
        }

        if (clazz == boolean[].class) {
            return "boolean[]";
        } else if (clazz == float[].class) {
            return "float[]";
        } else if (clazz == long[].class) {
            return "int[]";
        } else if (clazz == double[].class) {
            return "double[]";
        } else if (clazz == byte[].class) {
            return "raw";
        } else if (clazz == int[].class) {
            return "int[]";
        } else if (clazz == short[].class) {
            return "int[]";
        }

        if (clazz == Boolean[].class) {
            return "boolean[]";
        } else if (clazz == Float[].class) {
            return "float[]";
        } else if (clazz == Long[].class) {
            return "int[]";
        } else if (clazz == Integer[].class) {
            return "int[]";
        } else if (clazz == Short[].class) {
            return "int[]";
        } else if (clazz == Byte[].class) {
            return "raw";
        } else if (clazz == String[].class) {
            return "string[]";
        } else if (clazz == Double[].class || Number[].class.isAssignableFrom(clazz)) {
            return "double[]";
        }

        if (clazz == boolean.class) {
            return "boolean";
        } else if (clazz == float.class) {
            return "float";
        } else if (clazz == long.class || clazz == int.class || clazz == short.class || clazz == byte.class) {
            return "int";
        } else if (clazz == double.class) {
            return "double";
        }

        if (clazz == Boolean.class) {
            return "boolean";
        } else if (clazz == Float.class) {
            return "float";
        } else if (clazz == Long.class || clazz == Integer.class || clazz == Short.class || clazz == Byte.class) {
            return "int";
        } else if (clazz == Double.class || Number.class.isAssignableFrom(clazz)) {
            return "double";
        } else if (clazz == String.class) {
            return "string";
        }

        return "";
    }
}
