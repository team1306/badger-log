package badgerlog.aspects;

import badgerlog.annotations.Entry;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.conversion.UnitConversions;
import badgerlog.networktables.NTEntry;
import badgerlog.processing.data.Entries;
import badgerlog.transformations.EntryFactory;
import badgerlog.transformations.Mapping;
import badgerlog.utilities.ErrorLogger;
import badgerlog.utilities.KeyParser;
import badgerlog.utilities.Members;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.Measure;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public aspect NewEntryAspect {
    private final Entries entries = new Entries(new HashMap<>());


    @SuppressWarnings("unchecked")
    private void createFieldEntry(Field field, Object instance){
        Class<?> clazz = field.getDeclaringClass();
        String name = field.getName();
        Object fieldValue = Members.getFieldValue(field, instance);

        if (fieldValue == null) {
            ErrorLogger.memberError(field, "is an uninitialized field");
            return;
        }
        
        Configuration config = Configuration.createConfigurationFromAnnotations(field);
        String key = createKeyFromMember(config, field, instance);

        if (Modifier.isStatic(field.getModifiers()) && entries.getInstanceEntries(clazz, null).hasEntry(name)) {
            return;
        }

        Entry annotation = field.getAnnotation(Entry.class);

        if (annotation == null) {
            annotation = clazz.getAnnotation(Entry.class);
        }

        String tableType = getStringFromClass(clazz);

        NTEntry<?> entry;
        
        //todo add annotation processing for structs
        //todo make the default non null, so that there is an opportunity to keep the default struct
        if(config.getStructType() != null){
            entry = EntryFactory.create(key, fieldValue, config.getStructType());
        }
        
        
        //todo split into better logic (check if the class is measure to begin with)
        if (!tableType.isEmpty()){
            entry = EntryFactory.create(key, fieldValue, Mapping.identity(), NetworkTableType.getFromString(tableType));
        }else{
            if(clazz.isAssignableFrom(Measure.class)){
                if(!config.getUnit().isEmpty()){
                    entry = EntryFactory.create(key, fieldValue, (Mapping<Object, ?>) UnitConversions.createMapping(config.getUnit()), NetworkTableType.kDouble);
                }
                else{
                    Measure<?> measure = (Measure<?>) fieldValue;
                    entry = EntryFactory.create(key, measure, (Mapping<? super Measure<?>, ?>) UnitConversions.createMapping(measure.baseUnit()), NetworkTableType.kDouble);
                }
            }
        }
        
    }

    private <T extends Member & AnnotatedElement> String createKeyFromMember(Configuration config, T member, Object instance) {
        Class<?> clazz = member.getDeclaringClass();
        
        String key;
        if (Members.isMemberNonStatic(member)) {
            key = KeyParser.createKeyFromMember(config, member, instance, entries.getClassData(clazz).getInstanceCount());
        } else {
            key = KeyParser.createKeyFromStaticMember(config, member);
        }
        
        return key;
    }

    public static String getStringFromClass(Class<?> clazz) {
        if (clazz == null) {
            return "";
        }

        // Handle primitive arrays
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

        // Handle wrapper arrays
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

        // Handle primitives
        if (clazz == boolean.class) {
            return "boolean";
        } else if (clazz == float.class) {
            return "float";
        } else if (clazz == long.class || clazz == int.class || clazz == short.class || clazz == byte.class) {
            return "int";
        } else if (clazz == double.class) {
            return "double";
        }

        // Handle wrapper classes
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
