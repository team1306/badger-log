package badgerlog.utilities;

import badgerlog.annotations.configuration.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal class used by BadgerLog to create NetworkTables keys.
 */
public final class KeyParser {
    private static final Pattern FIELD_PATTERN = Pattern.compile("\\{([^}]+)\\}");

    private KeyParser() {
    }
    
    public static boolean hasFieldKey(String key){
        return !extractFieldNames(key).isEmpty();
    }
    
    public static void createKeyFromMember(Configuration config, Member member, Object instance) {
        String unparsedKey;
        if (config.getKey() == null || config.getKey().isBlank()) {
            unparsedKey = member.getDeclaringClass().getSimpleName() + "/" + member.getName();
        } else {
            unparsedKey = config.getKey();
        }

        config.withKey(unparsedKey);

        List<String> fieldNames = extractFieldNames(unparsedKey);
        if (fieldNames.isEmpty()) {
            return;
        }

        Map<String, String> fieldValues = new HashMap<>();
        for (String fieldName : fieldNames) {
            try {
                Field valueField = instance.getClass().getDeclaredField(fieldName);
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
