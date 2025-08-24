package badgerlog.conversion;

import badgerlog.annotations.configuration.Configuration;
import edu.wpi.first.networktables.NetworkTableType;
import lombok.Getter;

@Getter
public abstract class Mapping<StartType, NTType> {

    private final Class<StartType> startType;
    private final Class<NTType> tableType;
    private final NetworkTableType networkTableType;

    public Mapping(Class<StartType> startType, Class<NTType> tableType, NetworkTableType ntType) {
        this.startType = startType;
        this.tableType = tableType;
        this.networkTableType = ntType;
    }

    public boolean matches(Class<?> fieldType) {
        return fieldType != null && (this.startType.isAssignableFrom(fieldType) || fieldType.isAssignableFrom(this.startType));
    }

    public abstract NTType toNT(StartType startValue, Configuration config);

    public abstract StartType toStart(NTType ntValue, Configuration config);
}
