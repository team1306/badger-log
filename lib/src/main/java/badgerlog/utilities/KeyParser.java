package badgerlog.utilities;

import badgerlog.annotations.configuration.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates NetworkTables keys from existing values and names.
 */
public final class KeyParser {
    private static final Pattern FIELD_PATTERN = Pattern.compile("\\{([^}]+)\\}");

    private KeyParser() {
    }

    /**
     * Checks whether a key doesn't contain a potential field specific key.
     * @param key the key to check
     * @return if the key is missing a potential field specific key
     */
    public static boolean missingFieldKey(String key){
        return extractFieldNames(key).isEmpty();
    }

    /**
     * Creates a key and assigns it in the {@code config} using the provided config, member, and instance. 
     * 
     * <p>Basic procedure is defined as such: Use the predefined key or table when assigned, 
     * otherwise create a table with the class name and a key with the member's name. 
     * Then, assign any field specific parts of the key to the value on the field specified.</p>
     * 
     * @param config the configuration to both get data from and apply the final key to 
     * @param member the member to generate the key for
     * @param instance the instance for the instance specific keys
     * @param instanceCount the number of instances that have already been created
     */
    public static void createKeyFromMember(Configuration config, Member member, Object instance, int instanceCount) {
        StringBuilder keyBuilder = new StringBuilder(createUnparsedKey(config, member));
        
        if(instanceCount > 1 && missingFieldKey(keyBuilder.toString())){
            int beforeKey = keyBuilder.indexOf("/");
            if(beforeKey == -1){
                keyBuilder.append(instanceCount);
            }
            else{
                keyBuilder.insert(beforeKey, instanceCount);
            }
        }
        String unparsedKey = keyBuilder.toString();

        config.withKey(unparsedKey);

        List<String> fieldNames = extractFieldNames(unparsedKey);
        if (fieldNames.isEmpty()) {
            return;
        }

        Map<String, String> fieldValues = new HashMap<>();
        for (String fieldName : fieldNames) {
            try {
                Field valueField = member.getDeclaringClass().getDeclaredField(fieldName);
                fieldValues.put(fieldName, Fields.getFieldValue(valueField, instance).toString());
            } catch (NoSuchFieldException | NullPointerException e) {
                config.makeInvalid();
                ErrorLogger.customError(member.getDeclaringClass().getSimpleName() + "." + member
                        .getName() + " was invalidated after key contained invalid field.");
                return;
            }
        }

        String parsedKey = replaceFields(unparsedKey, fieldValues);
        config.withKey(parsedKey);
    }

    /**
     * Creates a key for a static member and assigns it in the {@code config} using the provided config and member.
     * 
     * <p>This drops any instance specific key values, but otherwise follows the {@link #createKeyFromMember(Configuration, Member, Object, int)} procedure.</p>
     * @param config the configuration to both get data from and apply the final key to 
     * @param member the static member to generate the key for
     */
    public static void createKeyFromStaticMember(Configuration config, Member member){
        String unparsedKey = createUnparsedKey(config, member);
        
        List<String> fieldNames = extractFieldNames(unparsedKey);
        Map<String, String> emptyReplaceMap = fieldNames.stream().collect(HashMap::new, (map, value) -> map.put(value, ""), HashMap::putAll);
    
        unparsedKey = replaceFields(unparsedKey, emptyReplaceMap);

        config.withKey(unparsedKey);
    }

    private static String createUnparsedKey(Configuration config, Member member) {
        String existingTableName = config.getTable();
        String existingKeyName = config.getKey();

        StringBuilder keyBuilder = new StringBuilder();

        String newTable = Objects.requireNonNullElseGet(existingTableName, () -> member.getDeclaringClass().getSimpleName());
        keyBuilder.append(newTable);
        keyBuilder.append("/");

        if(existingKeyName == null || existingKeyName.isBlank()){
            keyBuilder.append(member.getName());
        }
        else{
            keyBuilder.append(existingKeyName);
        }
        
        return keyBuilder.toString();
    }

    private static List<String> extractFieldNames(String template) {
        List<String> fieldNames = new ArrayList<>();
        Matcher matcher = FIELD_PATTERN.matcher(template);

        while (matcher.find()) {
            fieldNames.add(matcher.group(1)); // group(1) gets the content inside {}
        }

        return fieldNames;
    }

    private static String replaceFields(String template, Map<String, String> fieldValues) {
        Matcher matcher = FIELD_PATTERN.matcher(template);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String replacement = fieldValues.getOrDefault(fieldName, matcher.group(0));
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
