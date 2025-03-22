package frc.robot.subsystems;

import badgerlog.entry.Entry;
import badgerlog.entry.EntryType;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class Arm extends SubsystemBase  {
    
    @Entry(type = EntryType.Subscriber)
    private static double kG = 0.03, kV = 0;
    
    private static final double MAX_VELOCITY = Double.MAX_VALUE, MAX_ACCELERATION = Double.MAX_VALUE;
    
    private final double MIN_ANGLE = -30, MAX_ANGLE = 90;

    private final Rotation2d OFFSET = Rotation2d.fromDegrees(-160+4), TOLERANCE = Rotation2d.fromDegrees(0.1);

    @Entry(type = EntryType.Publisher)
    private static Rotation2d targetAngle = Rotation2d.kZero;

    @Entry(type = EntryType.Publisher)
    public static Rotation2d currentAngle = Rotation2d.kZero;

    @Entry(type = EntryType.Sendable)
    private static ProfiledPIDController profiledPIDController = new ProfiledPIDController(0.02, 0, 0.0001, new TrapezoidProfile.Constraints(MAX_VELOCITY, MAX_ACCELERATION));
    private ArmFeedforward feedforward;

    private final Alert encoderUnpluggedAlert = new Alert("Arm encoder detected unplugged", AlertType.kError);
    
    /**
     * The arm is mounted on the elevator and moves the wrist and intake to place and pick up coral.
     * Hardware: The arm has one Talon FX motor controller and an Absolute Analog Encoder
     * Controllers: Feedforward and Profiled PID Controller.
     */
    public Arm() {

        feedforward = new ArmFeedforward(0, kG, kV, 0);

        profiledPIDController.setTolerance(TOLERANCE.getDegrees());
        setTargetAngle(Rotation2d.kZero);
    }

    @Override
    public void periodic() {
        feedforward = new ArmFeedforward(0, kG, kV, 0);

        double pidOutput = profiledPIDController.calculate(getCurrentAngle().getDegrees(), targetAngle.getDegrees());
        final State state = profiledPIDController.getSetpoint();
        double feedforwardOutput = feedforward.calculate(getCurrentAngle().getRadians(), Math.toRadians(state.velocity));
        double motorOutput = pidOutput + feedforwardOutput;

        currentAngle = getCurrentAngle();
    }

    /**
     * Gets if the arm is at its setpoint.
     * @return true if the arm is at its setpoint (with a tolerance of course).
     */
    public boolean atSetpoint() {
        return Math.abs(currentAngle.minus(targetAngle).getDegrees()) < TOLERANCE.getDegrees();
    }

    /**
     * Gets the current angle of the arm.
     * @return the rotation of the arm.
     */
    public Rotation2d getCurrentAngle() {
        return Rotation2d.fromRotations(0).minus(OFFSET);
    }

    /**
     * Sets the target angle of the arm.
     * @param setpoint the rotation for the arm setpoint.
     */
    public void setTargetAngle(Rotation2d setpoint) {
        targetAngle = Rotation2d.fromRadians(MathUtil.clamp(setpoint.getRadians(), MIN_ANGLE, MAX_ANGLE));
    }
}