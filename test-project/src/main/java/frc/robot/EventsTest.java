package frc.robot;

import badgerlog.annotations.Entry;
import badgerlog.annotations.EntryType;
import badgerlog.annotations.Event;
import badgerlog.annotations.EventType;
import badgerlog.annotations.Interceptor;
import badgerlog.annotations.Watcher;
import badgerlog.events.EventData;

public class EventsTest implements Testing{
    
    @Entry(EntryType.SUBSCRIBER)
    private int watcherTest = 1;

    @Event("IntTest")
    @Entry(EntryType.SUBSCRIBER)
    private int interceptorTest = 1;
    
    @Override
    public void initialize() {

    }

    @Override
    public void update() {
    }
    
    @Watcher(type = int.class, keys = "/BadgerLog/EventsTest", eventType = EventType.ALL)
    private void integerWatcher(EventData<Integer> data){
        System.out.println("EVENT value: "+data + "-> Fired");
    }
    
    @Interceptor(type = Integer.class, name = "IntTest", keys = "interceptorTest", priority = 4)
    private Object clampingInterceptor(EventData<Integer> data){
        System.out.println("Intercepted");
        return Math.max(data.newValue(), 0);
    }
}
