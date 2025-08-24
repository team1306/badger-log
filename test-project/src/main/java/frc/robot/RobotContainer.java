// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import badgerlog.Dashboard;
import badgerlog.annotations.*;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;


public class RobotContainer
{
    @Entry(EntryType.Publisher)
    @Key("RobotContainer/{id}")
    public String testEntry = "test";

    @Entry(EntryType.Publisher)
    @AutoGenerateStruct
    @StructType(StructOptions.SUB_TABLE)
    public TestRecordOuter recordOuter = new TestRecordOuter(new TestRecordInner(1.24, new TestRecordInnerInner(5)), new Pose2d(new Translation2d(1, 2), Rotation2d.k180deg), 6, 3);
    //    public double test = 1;
    private String id;

    @Entry(EntryType.Publisher)
    @AutoGenerateStruct
    @StructType(StructOptions.SUB_TABLE)
    private TestEnum testEnum = TestEnum.FUN;

    public enum TestEnum {
        FUN,
        LAST,
        TEST
    }

    public RobotContainer(String id)
    {
        this.id = id;
        Dashboard.createSelectorFromEnum("Selector/Enums", TestEnum.class, value -> {
            testEnum = (TestEnum) value;
            System.out.println(testEnum);
        });
    }
    
    public Command getAutonomousCommand()
    {
        return Commands.print("No autonomous command configured");
    }

    public record TestRecordOuter(TestRecordInner inner, Pose2d pose2d, double coolNumber,
                                  int otherNumber) {
    }

    public record TestRecordInner(double reallyInnerDouble, TestRecordInnerInner tester) {
    }

    public record TestRecordInnerInner(int superDuperInt) {
    }
}
