// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import badgerlog.Dashboard;
import badgerlog.annotations.*;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;


public class RobotContainer {
    //    public double test = 1;
    private final String id;
    @Entry(EntryType.PUBLISHER)
    @Key("RobotContainer/{id}")
    public String testEntry = "test";

    @Entry(EntryType.PUBLISHER)
    @AutoGenerateStruct
    @Struct(StructType.SUB_TABLE)
    public TestRecordOuter recordOuter = new TestRecordOuter(new TestRecordInner(1.24, new TestRecordInnerInner(5)), new Pose2d(new Translation2d(1, 2), Rotation2d.k180deg), 6, 3);

    @Entry(EntryType.PUBLISHER)
    @AutoGenerateStruct
    @Struct(StructType.STRUCT)
    private TestEnum testEnum = TestEnum.FUN;

    public RobotContainer(String id) {
        this.id = id;
        Dashboard.createSelectorFromEnum("Selector/Enums", TestEnum.class, value -> {
            testEnum = (TestEnum) value;
            System.out.println(testEnum);
        });

        Dashboard.createAutoResettingButton("Sendable Button", CommandScheduler.getInstance().getDefaultButtonLoop())
                .onTrue(new InstantCommand(() -> {
                    Field2d field2d = new Field2d();
                    Dashboard.putSendable("Field2d", field2d);

                    field2d.setRobotPose(new Pose2d(Math.random() * 54, Math.random() * 27, new Rotation2d()));
                }).ignoringDisable(true));
    }

    public Command getAutonomousCommand() {
        return Commands.print("No autonomous command configured");
    }

    public enum TestEnum {
        FUN,
        LAST,
        TEST
    }

    public record TestRecordOuter(TestRecordInner inner, Pose2d pose2d, double coolNumber,
                                  int otherNumber) {
    }

    public record TestRecordInner(double reallyInnerDouble, TestRecordInnerInner tester) {
    }

    public record TestRecordInnerInner(int superDuperInt) {
    }
}
