package frc.robot;

import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;
import badgerlog.annotations.EventType;
import badgerlog.annotations.RawWatcher;
import badgerlog.annotations.Struct;
import badgerlog.annotations.StructType;
import badgerlog.annotations.Watched;
import badgerlog.annotations.Watcher;
import badgerlog.events.EventData;
import edu.wpi.first.math.geometry.Pose2d;

public class EventsTest implements Testing{
    
    @Entry(EntryType.SUBSCRIBER)
    @Watched("integer")
    private int watcherTest = 1;

    @Entry(EntryType.SUBSCRIBER)
    @Struct(StructType.SUB_TABLE)
    @Watched("pose2d")
    private Pose2d robotPose = Pose2d.kZero;
    
    @Override
    public void initialize() {

    }

    @Override
    public void update() {

    }

    @Watcher(type = Pose2d.class, name = "pose2d")
    private void pose2dWatcher(EventData<Double> data){
        System.out.println("EVENT value for Pose 2d : "+data + "-> Fired");
    }
    
    @Watcher(type = int.class, name = "integer")
    private void integerWatcher(EventData<Integer> data){
        System.out.println("EVENT value: "+data + "-> Fired");
    }

    @RawWatcher(type = void.class, keys = "/BadgerLog/EventsTest", eventType = EventType.ALL)
    private void integerRawWatcher(EventData<Integer> data){
        System.out.println("EVENT value RAW: "+data + "-> Fired");
    }
}
