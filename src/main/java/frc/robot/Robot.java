// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.util.dashboardv3.Dashboard;
import frc.robot.util.dashboardv3.entry.Config;
import frc.robot.util.dashboardv3.entry.Entry;
import frc.robot.util.dashboardv3.entry.EntryType;
import frc.robot.util.dashboardv3.networktables.mappings.UnitMappings;

import static edu.wpi.first.units.Units.Inches;

public class Robot extends TimedRobot {
    private Command autonomousCommand;

    private final RobotContainer robotContainer;
    
    @Entry(type = EntryType.Publisher)
    @Config(UnitMappings.RotationConfiguration.DEGREES)
    private static Rotation2d rotation2d = Rotation2d.fromDegrees(90);
    
    @Entry(type = EntryType.Subscriber)
    @Config(UnitMappings.DistanceConfiguration.INCHES)
    private static Distance distance = Inches.of(1);

    public Robot() {
        robotContainer = new RobotContainer();
        Dashboard.getNetworkTablesButton("TestButton", CommandScheduler.getInstance().getDefaultButtonLoop()).whileTrue(new RepeatCommand(Commands.print("Test1")));
        Dashboard.getAutoResettingButton("TestAutoResetButton", CommandScheduler.getInstance().getDefaultButtonLoop()).onTrue(Commands.print("Test2"));

    }


    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
        Dashboard.update();
        rotation2d = rotation2d.plus(Rotation2d.fromDegrees(1));
        
        Dashboard.putValue("TestValue", distance, "inches");
    }


    @Override
    public void disabledInit() {
    }


    @Override
    public void disabledPeriodic() {
    }


    @Override
    public void disabledExit() {
    }


    @Override
    public void autonomousInit() {
        autonomousCommand = robotContainer.getAutonomousCommand();

        if (autonomousCommand != null) {
            autonomousCommand.schedule();
        }
    }


    @Override
    public void autonomousPeriodic() {
    }


    @Override
    public void autonomousExit() {
    }


    @Override
    public void teleopInit() {
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
    }


    @Override
    public void teleopPeriodic() {
    }


    @Override
    public void teleopExit() {
    }


    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }


    @Override
    public void testPeriodic() {
    }


    @Override
    public void testExit() {
    }
}
