package badgerlog.events;

import edu.wpi.first.networktables.NetworkTableEvent.Kind;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class EventRegistry {
    private static final List<InterceptorEvent<?>> interceptorEvents = new ArrayList<>();
    private static final List<WatcherEvent<?>> watcherEvents = new ArrayList<>();
    
    public static void updateEvents(){
        NetworkTableInstance.getDefault().addListener(new String[] {""}, EnumSet.of(Kind.kValueAll), (value) -> {
            System.out.println(value.topicInfo.name);
        });
    }
    
    public static void addInterceptorEvent(InterceptorEvent<?> event){
        interceptorEvents.add(event);
        //Todo event starting logic
    }
    
    public static void addWatcherEvent(WatcherEvent<?> event){
        watcherEvents.add(event);
        //todo event starting logic
    }
    
}
