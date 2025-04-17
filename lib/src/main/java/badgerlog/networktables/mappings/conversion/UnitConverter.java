package badgerlog.networktables.mappings.conversion;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;

public interface UnitConverter<T extends Unit> {
    double convertTo(Measure<T> value);

    Measure<T> convertFrom(double value);
}
