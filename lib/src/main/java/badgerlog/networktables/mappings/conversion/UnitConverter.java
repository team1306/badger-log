package badgerlog.networktables.mappings.conversion;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;

import javax.annotation.Nonnull;

public interface UnitConverter<T extends Unit> {
    double convertTo(@Nonnull Measure<T> value);

    @Nonnull Measure<T> convertFrom(double value);
}
