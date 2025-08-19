package badgerlog.processing;

import badgerlog.annotations.configuration.Configuration;
import edu.wpi.first.util.struct.StructGenerator;


public class TypeParser {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void generateStructFromTypeIfPossible(Configuration config, Class<?> type) {
        if (type.isRecord()) {
            config.withGeneratedStruct(StructGenerator.genRecord((Class<? extends Record>) type));
        }
        if (type.isEnum()) {
            config.withGeneratedStruct(StructGenerator.genEnum((Class<? extends Enum>) type));
        }
    }
}
