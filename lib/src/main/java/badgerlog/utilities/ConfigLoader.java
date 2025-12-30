package badgerlog.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {
    private static final String DEFAULT_CONFIG_PATH = "default.json";
    private static final String USER_CONFIG_NAME = "badgerlog.json";
    private final Gson gson = new Gson();
    
    public <T> T loadConfig(Class<T> configClass) throws IOException {
        // Load default config from library
        JsonObject defaultConfig = loadDefaultConfig();

        // Try to load user's default.json
        JsonObject userConfig = loadUserConfig();

        // Merge: user config overrides defaults
        JsonObject merged = mergeConfigs(defaultConfig, userConfig);

        return gson.fromJson(merged, configClass);
    }

    private JsonObject loadDefaultConfig() throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(DEFAULT_CONFIG_PATH);

        if (inputStream == null) {
            throw new FileNotFoundException(
                    "Default config not found in library: " + DEFAULT_CONFIG_PATH);
        }

        return parseJson(inputStream);
    }

    private JsonObject loadUserConfig() {
        try {
            ClassLoader classLoader = Thread.currentThread()
                    .getContextClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(USER_CONFIG_NAME);

            if (inputStream == null) {
                // No user config found, return empty - will use all defaults
                return new JsonObject();
            }

            return parseJson(inputStream);
        } catch (IOException e) {
            // If there's an error reading user config, log and return empty
            System.err.println("Warning: Could not read " + USER_CONFIG_NAME +
                    ", using defaults: " + e.getMessage());
            return new JsonObject();
        }
    }

    private JsonObject parseJson(InputStream inputStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                throw new IOException("Config file must be a JSON object");
            }
            return element.getAsJsonObject();
        }
    }

    private JsonObject mergeConfigs(JsonObject defaultConfig, JsonObject userConfig) {
        if (userConfig == null || userConfig.isEmpty()) {
            return defaultConfig;
        }

        return deepMerge(defaultConfig.deepCopy(), userConfig);
    }

    private JsonObject deepMerge(JsonObject target, JsonObject source) {
        for (String key : source.keySet()) {
            JsonElement sourceValue = source.get(key);
            JsonElement targetValue = target.get(key);

            if (targetValue != null && targetValue.isJsonObject() &&
                    sourceValue.isJsonObject()) {
                // Recursively merge nested objects
                deepMerge(targetValue.getAsJsonObject(), sourceValue.getAsJsonObject());
            } else {
                // Replace with new value
                target.add(key, sourceValue);
            }
        }

        return target;
    }
}
