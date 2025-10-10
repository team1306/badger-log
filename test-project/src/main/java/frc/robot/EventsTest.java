package frc.robot;

import badgerlog.annotations.*;
import badgerlog.events.EventData;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class EventsTest implements Testing{
    
    @Entry(EntryType.SUBSCRIBER)
    private int watcherTest = 1;

    @Entry(EntryType.SUBSCRIBER)
    @Struct(StructType.STRUCT)
    private Pose2d robotPose = Pose2d.kZero;
    
    @Override
    public void initialize() {

    }

    @Override
    public void update() {
        robotPose = new Pose2d(new Translation2d(), Rotation2d.fromRadians(Math.random()));
    }

    @Watcher(type = Double.class, keys = "/BadgerLog/EventsTest", eventType = EventType.ALL)
    private void pose2dWatcher(EventData<Double> data){
        System.out.println("EVENT value for Pose 2d : "+data + "-> Fired");
    }
    
    @Watcher(type = Long.class, keys = "/BadgerLog/EventsTest", eventType = EventType.ALL)
    private void integerWatcher(EventData<Integer> data){
        System.out.println("EVENT value: "+data + "-> Fired");
    }
}
