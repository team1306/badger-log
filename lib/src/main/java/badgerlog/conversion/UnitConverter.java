package badgerlog.conversion;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;

/**
 * Internal interface used by BadgerLog to create a Unit mapping.
 *
 * @param <T> the unit type
 */
public interface UnitConverter<T extends Unit> {
    /**
     * Convert from a Measure to a double.
     *
     * @param value the measure to convert
     *
     * @return the double value in the specified unit
     */
    double convertTo(Measure<T> value);

    /**
     * Convert from a double to a Measure using the Unit.
     *
     * @param value the double value to convert
     *
     * @return the Measure converted from a double using the Unit
     */
    Measure<T> convertFrom(double value);
}
