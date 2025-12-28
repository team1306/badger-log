package badgerlog.utilities;

import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructGenerator;

import java.lang.reflect.RecordComponent;
import java.util.Optional;

/**
 * Generates structs from predefined types.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class TypeParser {

    private TypeParser() {
    }

    /**
     * Tries to create a struct from a class and apply it to a configuration.
     *
     * <p>The class should either be a record or an Enum.</p>
     *
     * @param type the type class
     * @param <T> the type of the class
     */
    public static <T> Optional<Struct<T>> generateStructFromTypeIfPossible(Class<T> type) {
        Struct<T> struct;
        if (type.isRecord()) {
            createStructReferencesFromRecord((Class<? extends Record>) type, 0);
            struct = (Struct<T>) StructGenerator.genRecord((Class<? extends Record>) type);
        } else if (type.isEnum()) {
            createStructReferencesFromEnum((Class<? extends Enum>) type, 0);
            struct = StructGenerator.genEnum((Class<? extends Enum>) type);
        } else {
            return Optional.empty();
        }

        StructGenerator.addCustomStruct(type, struct, true);
        return Optional.of(struct);
    }

    private static <T extends Record> void createStructReferencesFromRecord(Class<T> type, int depth) {
        if (depth > 100) {
            throw new IllegalArgumentException("Infinite recursive loop for record class: " + type.getSimpleName());
        }

        for (RecordComponent component : type.getRecordComponents()) {
            if (component.getType().isRecord()) {
                createStructReferencesFromRecord((Class<? extends Record>) component.getType(), depth + 1);
            }
            if (component.getClass().isEnum()) {
                createStructReferencesFromEnum((Class<? extends Enum>) component.getType(), depth + 1);
            }
        }
        Struct<T> struct = StructGenerator.genRecord(type);
        StructGenerator.addCustomStruct(type, struct, true);
    }

    private static <T extends Enum<T>> void createStructReferencesFromEnum(Class<T> type, int depth) {
        if (depth > 100) {
            throw new IllegalArgumentException("Infinite recursive loop for enum class: " + type.getSimpleName());
        }

        Struct<T> struct = StructGenerator.genEnum(type);
        StructGenerator.addCustomStruct(type, struct, true);
    }
}
