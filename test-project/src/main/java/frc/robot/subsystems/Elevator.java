package frc.robot.subsystems;

import badgerlog.Dashboard;
import badgerlog.entry.Config;
import badgerlog.entry.Entry;
import badgerlog.entry.EntryType;
import badgerlog.networktables.mappings.UnitMappings;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.*;

public class Elevator extends SubsystemBase {
    
    private static final double SPROCKET_DIAMETER_INCHES = 1.882;

    @Entry(type = EntryType.Subscriber)
    private static double kG = 0.04, kV = 0;

    private final static double MAX_VELOCITY = 1e+9, MAX_ACCELERATION = 700; // placeholder
    private final Distance TOLERANCE = Inches.of(0.5);
    
    @Entry(type = EntryType.Sendable)
    private static ProfiledPIDController pid = new ProfiledPIDController(0.1, 0, 0.006,  new TrapezoidProfile.Constraints(MAX_VELOCITY, MAX_ACCELERATION));    
    private ElevatorFeedforward feedforward;
    
    @Entry(type = EntryType.Subscriber)
    private static double conversionFactor = 54.75 / 140.83;
    //54.75
    @Entry(type = EntryType.Subscriber)
    private static double maxHeightInches = 53.5; // placeholders
    
    @Entry(type = EntryType.Publisher)
    @Config(UnitMappings.DistanceConfiguration.INCHES)
    private static Distance targetHeight = Inches.of(0);

    @Entry(type = EntryType.Publisher)
    @Config(UnitMappings.DistanceConfiguration.INCHES)
    private static Distance currentHeight = Inches.of(0);

    private Distance offset = Inches.of(0);

    /**
     * The elevator is mounted on the robot frame and moves the arm up and down.
     * Hardware: the elevator has two Talon FX motor controllers.
     * Controllers: Feedforward and ProfiledPIDController.
     */
    public Elevator() {
        pid.setTolerance(TOLERANCE.in(Inches));

        feedforward = new ElevatorFeedforward(0, kG, kV, 0);

        zeroElevatorMotorPositions();
    }

    @Override
    public void periodic() {
        feedforward = new ElevatorFeedforward(0, kG, kV, 0);

        currentHeight = getCurrentHeight();
        
        double feedforwardOutput = feedforward.calculate(pid.getSetpoint().velocity);
        double motorOutput = feedforwardOutput;

        Dashboard.putValue("Motor Output", motorOutput);
    }
    
    /**
     * Gets whether the elevator is at its setpoint using the PID controller.
     * @return true if the elevator is at its setpoint.
     */
    public boolean atSetpoint() {
        return currentHeight.minus(targetHeight).abs(Inches) < TOLERANCE.in(Inches);
    }

    /**
     * Gets the current height of the elevator.
     * @return the elevator height in distance.
     */
    public Distance getCurrentHeight(){
        return getRawHeight().minus(offset);
    }

    private Distance getRawHeight() {
        return rotationsToDistance(getCurrentElevatorMotorPositions()).times(conversionFactor);
    }
    
    /**
     * Gets the motor positions
     * @return the rotation of the motors
     */
    public Rotation2d getCurrentElevatorMotorPositions(){
        return Rotation2d.fromRadians(0);
    }
    
    /**
     * Sets the elevator motor positions to zero
     */
    public void zeroElevatorMotorPositions(){
        
    }

    /**
     * Converts from a Rotation2d to a Distance using the sprocket diameter.
     */
    public static Distance rotationsToDistance(Rotation2d rotation) {
        return Inches.of(rotation.getRotations() * SPROCKET_DIAMETER_INCHES * Math.PI);
    }

    /**
     * Converts from a Distance to a Rotation2d using the sprocket diameter.
     */
    public static Rotation2d distanceToRotations(Distance distance) {
        return Rotation2d.fromRotations(distance.in(Inches) / (SPROCKET_DIAMETER_INCHES * Math.PI));
    }
}
