// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import badgerlog.annotations.*;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;


public class RobotContainer
{
    @Entry(EntryType.Publisher)
    @Key("RobotContainer/{id}")
    public String testEntry = "test";

    @Entry(EntryType.Publisher)
    @AutoGenerateStruct
    @Key("RobotContainer-{id}/record")
    @StructType(StructOptions.SUB_TABLE)
    public TestRecordOuter recordOuter = new TestRecordOuter(6.3, 3);
    //    public double test = 1;
    private String id;

    public RobotContainer(String id)
    {
        this.id = id;
    }
    
    public Command getAutonomousCommand()
    {
        return Commands.print("No autonomous command configured");
    }

    public record TestRecordOuter(double coolNumber, int otherNumber) {
    }

    public record TestRecordInner(double reallyInnerDouble) {
    }
}
