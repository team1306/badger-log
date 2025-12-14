// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import badgerlog.BadgerLog;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import java.util.List;

public class Robot extends TimedRobot {

    private List<Testing> testingList;
    
    public Robot() {
        testingList = List.of(new ComplexTest(), new MethodsTest(), new FieldsTest("fieldsInitial"), new FieldsTest("fieldsFinal"), new ClassTest(), new EventsTest());
    }

    @Override
    public void robotInit() {
        testingList.forEach(Testing::initialize);
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
        BadgerLog.update();

        testingList.forEach(Testing::update);
        StaticTest.staticDouble ++;
    }
}
