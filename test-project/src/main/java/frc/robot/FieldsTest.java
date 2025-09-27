package frc.robot;

import badgerlog.annotations.AutoGenerateStruct;
import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;
import badgerlog.annotations.Key;
import badgerlog.annotations.Struct;
import badgerlog.annotations.StructType;
import badgerlog.annotations.Table;
import badgerlog.annotations.UnitConversion;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Millimeter;

@Table("Auto - {descriptor}")
public class FieldsTest implements Testing {

    @Entry(EntryType.PUBLISHER)
    public static int basicInteger = 1;

    @Entry(EntryType.SUBSCRIBER)
    @Key("AutoWaitTime")
    public double waitTime = 0;

    @Entry(EntryType.SUBSCRIBER)
    @Key("Modules/{descriptor}/Module P")
    public double moduleP = 0;

    @Entry(EntryType.PUBLISHER)
    @Struct(StructType.STRUCT)
    public Rotation2d rotation2d = Rotation2d.fromDegrees(1306);

    @Entry(EntryType.PUBLISHER)
    @UnitConversion("meters")
    public Distance height = Inches.of(10);

    @Entry(EntryType.SUBSCRIBER)
    @UnitConversion(value = "in", converterId = "translation")
    @UnitConversion(value = "radian", converterId = "rotation")
    @Struct(StructType.MAPPING)
    public Pose2d robotPose = Pose2d.kZero;

    @Entry(EntryType.PUBLISHER)
    @AutoGenerateStruct
    public CustomRecord record = new CustomRecord(3.4, 3);

    public record CustomRecord(double value, int count) {
    }


    private final String descriptor;

    public FieldsTest(String descriptor) {
        this.descriptor = descriptor;
    }


    @Override
    public void initialize() {

    }

    @Override
    public void update() {
        basicInteger += (int)(Math.random() * 10);
        height = Millimeter.of(2).plus(height);
        rotation2d = Rotation2d.fromDegrees(1).plus(rotation2d);
        this.record = new CustomRecord(Math.random(), (int) (Math.random() * 4 + 2));
    }
    
    @Entry(EntryType.PUBLISHER)
    private double getBasicInteger(){
        return basicInteger * 0.333f;
    }
}
