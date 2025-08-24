package badgerlog;

import badgerlog.annotations.StructOptions;
import lombok.Getter;

@Getter
public final class DashboardConfig {

    public static final DashboardConfig defaultConfig = new DashboardConfig();

    public static final DashboardConfig smartDashboardConfig = new DashboardConfig()
            .withStructOptions(StructOptions.STRUCT);

    private StructOptions structOptions = StructOptions.SUB_TABLE;

    private DashboardConfig() {
    }

    public DashboardConfig withStructOptions(StructOptions structOptions) {
        this.structOptions = structOptions;
        return this;
    }
}
