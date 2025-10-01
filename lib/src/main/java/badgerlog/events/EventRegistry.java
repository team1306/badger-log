package badgerlog.events;

import edu.wpi.first.units.measure.Time;
import edu.wpi.first.wpilibj.Timer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static edu.wpi.first.units.Units.Milliseconds;
import static edu.wpi.first.units.Units.Seconds;

public class EventRegistry {
    private static final Queue<EventPair<?>> eventQueue = new ConcurrentLinkedQueue<>();

    public static void updateEvents() {
        Time initialTime = Seconds.of(Timer.getFPGATimestamp());
        while (!eventQueue.isEmpty()) {
            Time nowTime = Seconds.of(Timer.getFPGATimestamp());
            if (nowTime.minus(initialTime).in(Milliseconds) > 5) {
                break;
            }

            EventPair<?> queuedEvent = eventQueue.poll();
            if (queuedEvent == null) {
                continue;
            }

            handleEvent(queuedEvent);
        }
    }

    private static <T> void handleEvent(EventPair<T> queuedEvent) {
        if (queuedEvent.event() instanceof WatcherEvent<?> event) {
            ((WatcherEvent<T>) event).invoke(queuedEvent.data);
        } else if (queuedEvent.event() instanceof InterceptorEvent<?> event) {
            ((InterceptorEvent<T>) event).invoke(queuedEvent.data);
        }
    }

    public static void addEvent(NTEvent event) {
        //Todo event queue logic
    }

    private record EventPair<T>(NTEvent event, EventData<T> data) {}
}
