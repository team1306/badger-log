package frc.robot;

import badgerlog.BadgerLog;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.conversion.UnitConversions;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.PrintCommand;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;

public class MethodsTest implements Testing {

    private final Field2d field = new Field2d();

    @Override
    public void initialize() {
        BadgerLog.createSelectorFromEnum("MethodsTest/SelectorNoDefault", RobotMode.class, System.out::println);
        BadgerLog.createSelectorFromEnum("MethodsTest/SelectorWithDefault", RobotMode.class, RobotMode.DISABLED, System.out::println);
        BadgerLog.createAutoResettingButton("MethodsTest/AutoResetButton", CommandScheduler.getInstance().getDefaultButtonLoop()).onTrue(new PrintCommand("Auto reset button true"));
        BadgerLog.createNetworkTablesButton("MethodsTest/NetworkTablesButton", CommandScheduler.getInstance().getDefaultButtonLoop()).onTrue(new PrintCommand("Network Tables button true")).onFalse(new PrintCommand("Network Tables button false"));

        BadgerLog.putSendable("MethodsTest/Field", field);
    }

    @Override
    public void update() {
        double x = BadgerLog.getValue("MethodsTest/PoseX", 0.0);
        Distance y = BadgerLog.getValue("MethodsTest/PoseY", Inches.of(0), new Configuration().withConverter("", UnitConversions.createConverter("meters")));

        Rotation2d rotation = BadgerLog.getValue("MethodsTest/Rotation", Rotation2d.fromDegrees(0));

        Pose2d pose = new Pose2d(x, y.in(Meters), rotation);
        field.setRobotPose(pose);

        BadgerLog.putValue("MethodsTest/PoseX Publish", Meters.of(x), new Configuration().withConverter("", UnitConversions.createConverter("inches")));
        BadgerLog.putValue("MethodsTest/PoseY Publish", y);
        BadgerLog.putValue("MethodsTest/Rotation Publish", rotation);
    }

    public enum RobotMode {
        DEBUG,
        AUTOMATIC,
        MANUAL,
        DISABLED
    }
}
