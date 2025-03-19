package frc.robot.util.dashboardv3.networktables.subscriber;

import frc.robot.util.dashboardv3.networktables.DashboardEntry;
import frc.robot.util.dashboardv3.networktables.DashboardUtil;
import frc.robot.util.dashboardv3.networktables.mappings.Mappings;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public class FieldSubscriber<FieldType> extends DashboardSubscriber<FieldType> implements DashboardEntry {

    private final Field field;

    public FieldSubscriber(String key, Field field, String config) {
        super(key, (Class<FieldType>) field.getType(), Mappings.findMappingType(field.getType()), config);
        this.field = field;
    }

    @Override
    @SneakyThrows({IllegalAccessException.class, IllegalArgumentException.class})
    public void update() {
        DashboardUtil.setFieldValue(field, super.getValue((FieldType) field.get(null)));
    }
}
