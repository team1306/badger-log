package frc.robot.comparison;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.Inches;

public class SmartDashboardCompare extends SubsystemBase {

    private static double kG = 0.045, kV = 0;
    private final static double MAX_VELOCITY = 225, MAX_ACCELERATION = 500; // placeholder

    private static ProfiledPIDController pid = new ProfiledPIDController(0.05, 0, 0.0015, new TrapezoidProfile.Constraints(MAX_VELOCITY, MAX_ACCELERATION));
    private ElevatorFeedforward feedforward;
    
    /*
    Motor initialization, configuration, and constants
     */

    private static double conversionFactor = 54.75 / 140.83;
    private static double maxHeightInches = 54.5;

    private static Distance targetHeight = Inches.of(0);
    private static Distance currentHeight = Inches.of(0);

    public SmartDashboardCompare() {
        
        /*
        Initialization related code
         */

        SmartDashboard.putNumber("kG", kG);
        SmartDashboard.putNumber("kV", kV);

        SmartDashboard.putData("PIDController", pid);

        SmartDashboard.putNumber("conversionFactor", conversionFactor);
        SmartDashboard.putNumber("maxHeightInches", maxHeightInches);
        feedforward = new ElevatorFeedforward(0, kG, kV, 0);
    }

    @Override
    public void periodic() {
        kG = SmartDashboard.getNumber("kG", kG);
        kV = SmartDashboard.getNumber("kV", kV);
        feedforward = new ElevatorFeedforward(0, kG, kV, 0);

        conversionFactor = SmartDashboard.getNumber("conversionFactor", conversionFactor);
        maxHeightInches = SmartDashboard.getNumber("maxHeightInches", maxHeightInches);

        SmartDashboard.putNumber("targetHeight", targetHeight.in(Inches));
        SmartDashboard.putNumber("currentHeight", currentHeight.in(Inches));
        
        /*
        Motor calculations
         */

    }
    
    /*
    Other elevator related code
     */
}
