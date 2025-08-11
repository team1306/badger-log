package frc.robot.comparison;

import badgerlog.entry.Entry;
import badgerlog.entry.EntryType;
import badgerlog.entry.handlers.UnitConversion;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.Inches;

public class BadgerLogCompare extends SubsystemBase {

    @Entry(EntryType.Subscriber)
    private static double kG = 0.045, kV = 0;
    private final static double MAX_VELOCITY = 225, MAX_ACCELERATION = 500; // placeholder

    @Entry(EntryType.Sendable)
    private static ProfiledPIDController pid = new ProfiledPIDController(0.05, 0, 0.0015, new TrapezoidProfile.Constraints(MAX_VELOCITY, MAX_ACCELERATION));
    private ElevatorFeedforward feedforward;
    
    /*
    Motor initialization, configuration, and constants
     */

    @Entry(EntryType.Subscriber)
    private static double conversionFactor = 54.75 / 140.83;

    @Entry(EntryType.Subscriber)
    private static double maxHeightInches = 54.5;

    @Entry(EntryType.Publisher)
    @UnitConversion("Inches")
    private static Distance targetHeight = Inches.of(0);

    @Entry(EntryType.Publisher)
    @UnitConversion("Inches")
    private static Distance currentHeight = Inches.of(0);

    public BadgerLogCompare() {
        
        /*
        Initialization related code
         */

        feedforward = new ElevatorFeedforward(0, kG, kV, 0);
    }

    @Override
    public void periodic() {
        feedforward = new ElevatorFeedforward(0, kG, kV, 0);
        
        /*
        Motor calculations
         */

    }
    
    /*
    Other elevator related code
     */
}
