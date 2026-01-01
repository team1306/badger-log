package badgerlog.transformations;

import badgerlog.utilities.Members;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;
import edu.wpi.first.units.Units;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UnitConversions {

    private static final Map<String, Unit> units = new HashMap<>();

    static {
        Field[] fields = Units.class.getFields();

        HashMap<Unit, Set<String>> parts = Arrays.stream(fields)
                .collect(Collectors.toSet())
                .stream()
                .map(Members::getFieldValue)
                .map(Unit.class::cast)
                .collect(
                        HashMap::new, (map, unit) -> map.put(unit, new HashSet<>()), HashMap::putAll);

        for (Field field : fields) {
            Unit unit = (Unit) Members.getFieldValue(field);
            parts.get(unit).add(unit.name().toLowerCase(Locale.ROOT));
            parts.get(unit).add(unit.symbol().toLowerCase(Locale.ROOT));
            parts.get(unit).add(field.getName().toLowerCase(Locale.ROOT));
        }

        for (Unit part : parts.keySet()) {
            Set<String> aliases = parts.get(part);
            aliases.forEach(alias -> units.put(alias, part));
        }
    }

    private UnitConversions() {
    }

    public static badgerlog.transformations.Mapping<?, ?> createMapping(String unit){
        unit = unit.toLowerCase(Locale.ROOT);

        if (units.get(unit) == null) {
            throw new IllegalArgumentException(String.format("Unit type: (%s) may not exist", unit));
        }
        return createMapping(units.get(unit));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Unit> badgerlog.transformations.Mapping<Measure<T>, ?> createMapping(T unit){
        return new badgerlog.transformations.Mapping<>((value) -> value.in(unit), (value) -> (Measure<T>) unit.of(value));
    }
}
