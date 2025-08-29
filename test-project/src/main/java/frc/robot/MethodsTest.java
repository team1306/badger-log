package frc.robot;

import badgerlog.Dashboard;
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
        Dashboard.createSelectorFromEnum("MethodsTest/SelectorNoDefault", RobotMode.class, System.out::println);
        Dashboard.createSelectorFromEnum("MethodsTest/SelectorWithDefault", RobotMode.class, RobotMode.DISABLED, System.out::println);
        Dashboard.createAutoResettingButton("MethodsTest/AutoResetButton", CommandScheduler.getInstance().getDefaultButtonLoop()).onTrue(new PrintCommand("Auto reset button true"));
        Dashboard.createNetworkTablesButton("MethodsTest/NetworkTablesButton", CommandScheduler.getInstance().getDefaultButtonLoop()).onTrue(new PrintCommand("Network Tables button true")).onFalse(new PrintCommand("Network Tables button false"));

        Dashboard.putSendable("MethodsTest/Field", field);
    }

    @Override
    public void update() {
        double x = Dashboard.getValue("MethodsTest/PoseX", 0.0);
        Distance y = Dashboard.getValue("MethodsTest/PoseY", Inches.of(0), new Configuration().withConverter("", UnitConversions.createConverter("meters")));

        Rotation2d rotation = Dashboard.getValue("MethodsTest/Rotation", Rotation2d.fromDegrees(0));

        Pose2d pose = new Pose2d(x, y.in(Meters), rotation);
        field.setRobotPose(pose);

        Dashboard.putValue("MethodsTest/PoseX Publish", Meters.of(x), new Configuration().withConverter("", UnitConversions.createConverter("inches")));
        Dashboard.putValue("MethodsTest/PoseY Publish", y);
        Dashboard.putValue("MethodsTest/Rotation Publish", rotation);
    }

    public enum RobotMode {
        DEBUG,
        AUTOMATIC,
        MANUAL,
        DISABLED
    }
}
