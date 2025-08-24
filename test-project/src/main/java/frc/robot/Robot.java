// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import badgerlog.Dashboard;
import badgerlog.annotations.*;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import static edu.wpi.first.units.Units.Hertz;
import static edu.wpi.first.units.Units.Millihertz;

public class Robot extends TimedRobot
{
    private Command autonomousCommand;
    
    private final RobotContainer robotContainer;
    
    @Entry(EntryType.Publisher)
    @Key("testkey")
    @UnitConversion("mHz")
    private Frequency test = Hertz.of(1);
    
    @Entry(EntryType.Publisher)
    @UnitConversion(value = "Inch", converterId = "translation")
    @UnitConversion(value = "Rotation", converterId = "rotation")
    @StructType(StructOptions.MAPPING)
    private Pose2d pose2d = new Pose2d(1, 2, Rotation2d.k180deg);
    
    @Entry(EntryType.Publisher)
    private double[] tester = {1, 2};

    @Entry(EntryType.Publisher)
    @StructType(StructOptions.SUB_TABLE)
    private Rotation2d rotation = Rotation2d.fromDegrees(180);
    
    public Robot()
    {
        robotContainer = new RobotContainer("test1");
        new RobotContainer("test4");
        new RobotContainer("thisnotatest");
        Dashboard.createSelectorFromEnum("Robot/EnumTestKey", StructOptions.class, StructOptions.STRUCT, System.out::println);
//        UnitEntries unitEntries = new UnitEntries();
//        StructEntries structEntries = new StructEntries();
//        DashboardMethods dashboardMethods = new DashboardMethods();
//        dashboardMethods.init();
//        addPeriodic(() -> {
//            unitEntries.update();
//            structEntries.update();
//            dashboardMethods.update();
//        }, 0.5);
    }
    
    
    @Override
    public void robotPeriodic()
    {
        Dashboard.update();
        CommandScheduler.getInstance().run();
        pose2d = pose2d.plus(new Transform2d(0, Math.random(), Rotation2d.k180deg));
        test = test.plus(Millihertz.of(0.4));
    }
    
    
    @Override
    public void disabledInit() {}
    
    
    @Override
    public void disabledPeriodic() {}
    
    
    @Override
    public void disabledExit() {}
    
    
    @Override
    public void autonomousInit()
    {
        autonomousCommand = robotContainer.getAutonomousCommand();
        
        if (autonomousCommand != null)
        {
            autonomousCommand.schedule();
        }
    }
    
    
    @Override
    public void autonomousPeriodic() {}
    
    
    @Override
    public void autonomousExit() {}
    
    
    @Override
    public void teleopInit()
    {
        if (autonomousCommand != null)
        {
            autonomousCommand.cancel();
        }
    }
    
    
    @Override
    public void teleopPeriodic() {}
    
    
    @Override
    public void teleopExit() {}
    
    
    @Override
    public void testInit()
    {
        CommandScheduler.getInstance().cancelAll();
    }
    
    
    @Override
    public void testPeriodic() {}
    
    
    @Override
    public void testExit() {}
}
