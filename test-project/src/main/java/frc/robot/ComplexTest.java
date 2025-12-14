package frc.robot;

import badgerlog.BadgerLog;
import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class ComplexTest implements Testing {

    @Entry(EntryType.INTELLIGENT)
    private boolean testBoolean = false;

    @Override
    public void initialize() {
        BadgerLog.createNetworkTablesButton("ComplexTest/ButtonBoolean", CommandScheduler.getInstance()
                        .getDefaultButtonLoop())
                .onTrue(new InstantCommand(() -> testBoolean = true).ignoringDisable(true))
                .onFalse(new InstantCommand(() -> testBoolean = false).ignoringDisable(true));
    }

    @Override
    public void update() {
//        System.out.println("testBoolean = " + testBoolean);
    }
}
