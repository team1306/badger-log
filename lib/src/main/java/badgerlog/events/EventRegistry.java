package badgerlog.events;

import edu.wpi.first.units.measure.Time;
import edu.wpi.first.wpilibj.Timer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static edu.wpi.first.units.Units.Milliseconds;
import static edu.wpi.first.units.Units.Seconds;

public class EventRegistry {
    private static final Queue<NTEvent> eventQueue = new ConcurrentLinkedQueue<>();
    
    public static void updateEvents(){
        Time initialTime = Seconds.of(Timer.getFPGATimestamp());
        while(!eventQueue.isEmpty()){
            Time nowTime = Seconds.of(Timer.getFPGATimestamp());
            if((nowTime.minus(initialTime)).in(Milliseconds) > 5){
                break;
            }
            
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
        }    
    }
    
    public static void addEvent(NTEvent event){
        //Todo event queue logic
    }
}
