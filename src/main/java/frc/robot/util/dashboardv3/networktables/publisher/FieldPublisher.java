package frc.robot.util.dashboardv3.networktables.publisher;

import frc.robot.util.dashboardv3.networktables.DashboardEntry;
import frc.robot.util.dashboardv3.networktables.DashboardUtil;
import frc.robot.util.dashboardv3.networktables.mappings.Mappings;

import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public class FieldPublisher<FieldType> extends DashboardPublisher<FieldType> implements DashboardEntry {

    private final Field field;

    public FieldPublisher(String key, Field field, String config) {
        super(key, (Class<FieldType>) field.getType(), Mappings.findMappingType(field.getType()), config);

        this.field = field;
    }


    @Override
    public void update() {
        super.publish(DashboardUtil.getFieldValue(field));
    }
}
