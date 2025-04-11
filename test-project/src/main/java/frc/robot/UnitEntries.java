package frc.robot;

import badgerlog.DashboardConfig;
import badgerlog.entry.Config;
import badgerlog.entry.Entry;
import badgerlog.entry.EntryType;
import badgerlog.networktables.mappings.UnitMappings;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.*;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

import static edu.wpi.first.units.Units.*;

public class UnitEntries {
    @Entry(type = EntryType.Subscriber)
    private static Distance distance = Meters.of(1);
    
    @Entry(type = EntryType.Subscriber)
    @Config(UnitMappings.FrequencyConfiguration.MILLIHERTZ)
    private static Frequency frequency = Hertz.one();
    
    @Entry(type = EntryType.Publisher)
    private static Temperature temperature = Kelvin.of(273);
    
    @Entry(type = EntryType.Subscriber)
    private static LinearAcceleration linearAcceleration = MetersPerSecondPerSecond.of(12);
    
    @Entry(type = EntryType.Publisher)
    @Config(UnitMappings.AngularVelocityConfiguration.DEGREES_PER_SECOND)
    private static AngularVelocity angularVelocity = RotationsPerSecond.of(2);

    @Entry(type = EntryType.Subscriber, structOptions = DashboardConfig.StructOptions.MAPPING)
    @Config(UnitMappings.RotationConfiguration.ROTATIONS)
    private static Rotation2d rotation = Rotation2d.fromDegrees(0);

    @Entry(type = EntryType.Sendable)
    private static Field2d field = new Field2d();

    @Entry(type = EntryType.Subscriber, structOptions = DashboardConfig.StructOptions.SUB_TABLE)
    private static Pose2d pose = Pose2d.kZero;

    public void update() {
        temperature = temperature.plus(Celsius.of(1));

        angularVelocity = angularVelocity.plus(DegreesPerSecond.of(2));
        field.setRobotPose(pose);

        System.out.println("--------");
        System.out.println(distance.baseUnitMagnitude());
        System.out.println(frequency.baseUnitMagnitude());
        System.out.println(linearAcceleration.baseUnitMagnitude());

        System.out.println(rotation);
        System.out.println(pose);
    }
}
