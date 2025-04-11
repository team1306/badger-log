package frc.robot;

import badgerlog.DashboardConfig;
import badgerlog.entry.Entry;
import badgerlog.entry.EntryType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class StructEntries {
    
    @Entry(type = EntryType.Subscriber, structOptions = DashboardConfig.StructOptions.STRUCT)
    private static Pose2d pose = new Pose2d();
    
    @Entry(key = "Rotate", type = EntryType.Publisher, structOptions = DashboardConfig.StructOptions.STRUCT)
    private static Rotation2d rotation = new Rotation2d();

    @Entry(type = EntryType.Subscriber, structOptions = DashboardConfig.StructOptions.SUB_TABLE)
    private static ChassisSpeeds speed = new ChassisSpeeds();

    @Entry(type = EntryType.Publisher, structOptions = DashboardConfig.StructOptions.SUB_TABLE)
    private static Translation3d translation = new Translation3d();
    
    public void update(){
        System.out.println(pose);
        System.out.println(speed);
        
        rotation = rotation.plus(Rotation2d.fromDegrees(pose.getRotation().getDegrees()));
        translation = translation.plus(new Translation3d(Math.random(), Math.random(), Math.random()));
    }
}
