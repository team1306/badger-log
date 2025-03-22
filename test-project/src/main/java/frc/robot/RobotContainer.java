// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.*;


public class RobotContainer
{

    private final Wrist wrist = new Wrist();
    private final Arm arm = new Arm();
    private final Elevator elevator = new Elevator();
    private final Intake intake = new Intake();
    
    public RobotContainer()
    {
        configureBindings();
        intake.periodic();
        wrist.periodic();
        elevator.periodic();
        arm.periodic();
    }
    
    
    private void configureBindings() {}
    
    
    public Command getAutonomousCommand()
    {
        return Commands.print("No autonomous command configured");
    }
}
