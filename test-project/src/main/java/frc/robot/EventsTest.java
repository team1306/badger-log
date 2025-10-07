package frc.robot;

import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;
import badgerlog.annotations.EventType;
import badgerlog.annotations.Watcher;
import badgerlog.events.EventData;

public class EventsTest implements Testing{
    
    @Entry(EntryType.SUBSCRIBER)
    private int watcherTest = 1;
    
    
    @Override
    public void initialize() {

    }

    @Override
    public void update() {
    }
    
    @Watcher(type = int.class, keys = "/BadgerLog/EventsTest", eventType = EventType.ALL)
    private void integerWatcher(EventData<Integer> data){
        System.out.println("EVENT value: "+data + "\n Fired");
    }
}
