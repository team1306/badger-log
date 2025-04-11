package frc.robot;

import badgerlog.Dashboard;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;

public class DashboardMethods {
    
    public void init(){
        Dashboard.getAutoResettingButton("Reset button", CommandScheduler.getInstance().getActiveButtonLoop()).onTrue(Commands.print("this is a win"));

        Dashboard.getNetworkTablesButton("Fake button", CommandScheduler.getInstance().getActiveButtonLoop()).onTrue(Commands.print("button on")).onFalse(Commands.print("button off"));
    }
    
    public void update() {
        int random = (int) (Math.random() * 100);
        System.out.println(Dashboard.getValue("Boolean!", false));

        Dashboard.putValue("Test Text", "Test"+ random);
        
        Dashboard.putValue("Pose", new Pose2d(Math.random(), 4, Rotation2d.kZero));
        
    }
}
