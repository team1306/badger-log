package frc.robot.util.dashboardv3.networktables.mappings;

import edu.wpi.first.networktables.NetworkTableType;
import frc.robot.util.dashboardv3.Dashboard;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Class containing all mappings for each type. On startup, searches for any fields annotated with {@link MappingType} and adds them to the list of current mappings.
 */
public class Mappings {
    public static final List<Mapping<?, ?>> mappings = new ArrayList<>();

    /**
     * Find a mapping based off the field type. It is assumed that only one mapping per field type is created, and will throw an error if there are more than one present
     * @param type the type of mapping to search for
     * @return the mapping with the specified input, and a valid NetworkTableOutput
     * @param <FieldType> the type specified by the field
     * @param <NTType> the NetworkTableType
     */
    @SuppressWarnings("unchecked")
    public static <FieldType, NTType> Mapping<FieldType, NTType> findMapping(Class<FieldType> type) {
        var filteredMappings = mappings.stream().filter(mapping -> mapping.matches(type)).toList();
        if (filteredMappings.isEmpty()) throw new IllegalArgumentException("No mapping found for " + type);
        if (filteredMappings.size() > 1) throw new IllegalArgumentException("Multiple mapping found for " + type);

        return (Mapping<FieldType, NTType>) filteredMappings.get(0);
    }

    /**
     * Finds the NetworkTable type given a field type class
     * @param type the field type
     * @return the NetworkTableType
     */
    public static NetworkTableType findMappingType(Class<?> type) {
        return findMapping(type).getNetworkTableType();
    }

    /**
     * Init method to search for all instances of the {@link MappingType} class and add them to the current list of mappings
     */
    @SneakyThrows({IllegalArgumentException.class, IllegalAccessException.class, ExecutionException.class, InterruptedException.class})
    public static void initialize() {
        var classGraph = new ClassGraph()
                .enableFieldInfo()
                .enableAnnotationInfo()
                .ignoreFieldVisibility();

        
        var resultAsync = classGraph.scanAsync(Dashboard.executorService, 10);
        var result = resultAsync.get();
        
        for (ClassInfo classInfo : result.getClassesWithFieldAnnotation(MappingType.class)) {
            for (FieldInfo fieldInfo : classInfo.getFieldInfo().filter(fieldInfo -> fieldInfo.hasAnnotation(MappingType.class))) {
                if (!fieldInfo.isStatic()) continue;

                var field = fieldInfo.loadClassAndGetField();
                if (field.get(null) == null) continue;

                mappings.add((Mapping<?, ?>) field.get(null));
            }
        }
        result.close();
    }
}
