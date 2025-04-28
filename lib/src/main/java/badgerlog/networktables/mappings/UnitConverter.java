package badgerlog.networktables.mappings;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;

import javax.annotation.Nonnull;

/**
 * Defines bidirectional conversion between numeric values and {@link Measure} objects of a specific unit type.
 *
 * @param <T> Unit type to convert to/from
 */
public interface UnitConverter<T extends Unit> {
    /**
     * Converts a {@link Measure} to its numeric value in this converter's unit.
     *
     * @param value Measurement to convert
     * @return Numeric value in this converter's unit
     */
    double convertTo(@Nonnull Measure<T> value);

    /**
     * Converts a numeric value to a {@link Measure} in this converter's unit.
     *
     * @param value Numeric value in this converter's unit
     * @return Measurement object wrapping the value
     */
    @Nonnull Measure<T> convertFrom(double value);
}
