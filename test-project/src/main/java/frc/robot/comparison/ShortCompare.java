package frc.robot.comparison;

import badgerlog.entry.Entry;
import badgerlog.entry.EntryType;
import badgerlog.entry.handlers.Key;
import badgerlog.entry.handlers.UnitConversion;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;

@SuppressWarnings("ALL")
public class ShortCompare {

    public static class BadgerLogComparison extends SubsystemBase {
        @Entry(EntryType.Subscriber)
        private static double conversionFactor = 54.75 / 140.83;

        // Could be a Distance
        @Entry(EntryType.Subscriber)
        private static double maxHeightInches = 54.5;

        @Entry(EntryType.Publisher)
        @UnitConversion("m")
        @Key("Comparison/TargetHeightMeters")
        private static Distance targetHeightMeters = Inches.of(0);

        @Entry(EntryType.Publisher)
        @UnitConversion("Inches")
        private static Distance currentHeight = Inches.of(0);
    }

    public static class SmartDashboardComparison extends SubsystemBase {
        private static double conversionFactor = 54.75 / 140.83;

        private static double maxHeightInches = 54.5;

        private static Distance targetHeightMeters = Inches.of(0);
        private static Distance currentHeight = Inches.of(0);

        public SmartDashboardComparison() {
            SmartDashboard.putNumber("SmartDashboardComparison/conversionFactor", conversionFactor);
            SmartDashboard.putNumber("SmartDashboardComparison/maxHeightInches", maxHeightInches);
        }

        @Override
        public void periodic() {
            conversionFactor = SmartDashboard.getNumber("SmartDashboardComparison/conversionFactor", conversionFactor);
            maxHeightInches = SmartDashboard.getNumber("SmartDashboardComparison/maxHeightInches", maxHeightInches);

            SmartDashboard.putNumber("SmartDashboardComparison/targetHeightMeters", targetHeightMeters.in(Meters));
            SmartDashboard.putNumber("SmartDashboardComparison/currentHeight", currentHeight.in(Inches));
        }
    }
}
