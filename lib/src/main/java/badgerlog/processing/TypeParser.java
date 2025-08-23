package badgerlog.processing;

import badgerlog.annotations.configuration.Configuration;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructGenerator;

import java.lang.reflect.RecordComponent;


@SuppressWarnings({"unchecked", "rawtypes"})
public class TypeParser {

    public static <T> void generateStructFromTypeIfPossible(Configuration config, Class<T> type) {
        Struct<T> struct;
        if (type.isRecord()) {
            createStructReferencesFromRecord((Class<? extends Record>) type, 0);
            struct = (Struct<T>) StructGenerator.genRecord((Class<? extends Record>) type);
        } else if (type.isEnum()) {
            createStructReferencesFromEnum((Class<? extends Enum>) type, 0);
            struct = StructGenerator.genEnum((Class<? extends Enum>) type);
        } else {
            System.err.println("Struct not generated for " + type.getSimpleName() + ". SKIPPING");
            return;
        }

        config.withGeneratedStruct(struct);
        StructGenerator.addCustomStruct(type, struct, true);
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
