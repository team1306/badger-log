package badgerlog.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventRegistry {
    private static final List<NTEvent> events = new ArrayList<>();
    private static final Queue<NTEvent> eventQueue = new ConcurrentLinkedQueue<>();
    
    public static void updateEvents(){
        
        for(NTEvent event : eventQueue){
            NTEvent queuedEvent = eventQueue.poll();
            if (queuedEvent == null) {
                continue;
            }
            
            handleEvent(queuedEvent);
        }
    }
    
    private static void handleEvent(NTEvent queuedEvent){
        if(queuedEvent instanceof WatcherEvent<?> event){
            //todo invoke the event
        }
        else if (queuedEvent instanceof InterceptorEvent<?> event){
            //todo invoke the event
        }    }
    
    public static void addInterceptorEvent(InterceptorEvent<?> event){
        events.add(event);
        //Todo event starting logic
    }
    
    public static void addWatcherEvent(WatcherEvent<?> event){
        events.add(event);
        //todo event starting logic
    }
    
}
