package frc.robot.subsystems;

import badgerlog.entry.Entry;
import badgerlog.entry.EntryType;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
//
public class Wrist extends SubsystemBase  {
    private final double MIN_ANGLE = -100, MAX_ANGLE = 100;

    private final Rotation2d OFFSET = Rotation2d.fromDegrees(-120);
    
    @Entry(type = EntryType.Subscriber)
    private static double offsetRight = 0D;

    private final Rotation2d TOLERANCE = Rotation2d.fromDegrees(0);

    @Entry(type = EntryType.Publisher)
    private static Rotation2d targetAngle = Rotation2d.fromDegrees(0);

    @Entry(type = EntryType.Publisher)
    public static Rotation2d currentAngle = Rotation2d.fromDegrees(0);
    
    @Entry(type = EntryType.Sendable)
    private static PIDController pidController = new PIDController(0.4, 0, 0.0001);

    private final Alert encoderUnpluggedAlert = new Alert("Arm encoder detected unplugged", AlertType.kError);

    /**
     * The wrist is mounted on the arm and rotates the intake to place and pick up coral.
     * Hardware: The wrist has one Talon FX motor controller and an Absolute Analog Encoder.
     * Controllers: Normal PID Controller.
     */
    public Wrist() {
        pidController.setTolerance(TOLERANCE.getRadians());
        
        setTargetAngle(Rotation2d.kZero);
    }

    @Entry(type = EntryType.Publisher)
    private static double motorOutput = 0;

    @Override
    public void periodic() {        
        double pidOutput = pidController.calculate(currentAngle.getRadians(), targetAngle.getRadians());

        motorOutput = pidOutput;
    }

    /**
     * Gets if the arm is at its setpoint using the PID controller.
     * @return true if the arm is at its setpoint.
     */
    public boolean atSetpoint() {
        return Math.abs(currentAngle.minus(targetAngle).getDegrees()) < TOLERANCE.getDegrees(); 
    }

    /**
     * Sets the target angle of the wrist.
     * @param setpoint the rotation for the wrist setpoint.
     */
    public void setTargetAngle(Rotation2d setpoint) {
        targetAngle = Rotation2d.fromRadians(MathUtil.clamp(setpoint.getRadians(), MIN_ANGLE, MAX_ANGLE));
    }
}