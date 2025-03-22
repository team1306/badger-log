package frc.robot.subsystems;

import badgerlog.entry.Entry;
import badgerlog.entry.EntryType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {

    @Entry(type = EntryType.Publisher)
    private static double targetSpeed = 0;

    @Entry(type = EntryType.Publisher)
    private static boolean sensorReading = false;
    
    /**
     * The intake is mounted on the wrist and used to pick up coral and release it to score.
     * Hardware: The intake has one Talon FX motor controller and a beam-break sensor.
     * Controllers: None
     */
    public Intake() {
    }

    @Override
    public void periodic() {

    }

    /**
     * Sets the target speed of the intake.
     * @param targetSpeed the speed for the intake setpoint (-1 - 1).
     */
    public void setTargetSpeed(double targetSpeed) {
        Intake.targetSpeed = targetSpeed;
    }

    /**
     * Gets the reading from the beam-break sensor on the intake.
     * @return true if the sensor detects an object in the intake
     */
    public boolean getSensorReading() {
        return sensorReading;
    }
}