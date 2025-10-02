package badgerlog.events;

import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableEvent.Kind;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventRegistry {
    private static final Queue<WatcherPair<?>> eventQueue = new ConcurrentLinkedQueue<>();
    private static final List<WatcherData> watcherData = new ArrayList<>();
    private static final List<InterceptorData> interceptorData = new ArrayList<>();

    public static void updateEvents() {
        while (!eventQueue.isEmpty()) {
            WatcherPair<?> queuedEvent = eventQueue.poll();
            if (queuedEvent == null) {
                continue;
            }

            handleWatcher(queuedEvent);
        }
    }

    @SuppressWarnings("unchecked")
    private static void handleWatcher(WatcherPair<?> event) {
        WatcherEvent<Object> watcher = (WatcherEvent<Object>) event.watcher();
        EventData<Object> data = (EventData<Object>) event.data();

        watcher.invoke(data);
    }

    private static final NetworkTableInstance networkTableInstance = NetworkTableInstance.getDefault();

    public static void registerWatcher(WatcherEvent<?> event, EventMetadata metadata) {
        EnumSet<Kind> validMessages = EnumSet.of(
                switch (metadata.type()) {
                    case INCOMING -> Kind.kValueRemote;
                    case OUTGOING -> Kind.kValueLocal;
                    case ALL -> Kind.kValueAll;
                });

        networkTableInstance.addListener(new String[] {metadata.key()}, validMessages, (ntEvent) -> addNetworkTablesWatcherEvent(event, ntEvent));
    }

    public static void registerInterceptor(EventMetadata metadata, InterceptorEvent<?> event) {

    }
    
    public static void addNetworkTablesWatcherEvent(WatcherEvent<?> watcherEvent, NetworkTableEvent event){
        EventData<Object> data = new EventData<>(event.topicInfo.name, Timer.getFPGATimestamp(), null, event.valueData.value.getValue());
//        eventQueue.add(new WatcherPair<>(watcherEvent, data));
    }

    private record WatcherPair<T>(WatcherEvent<T> watcher, EventData<T> data) {}

    private record WatcherData(EventMetadata data, WatcherEvent<?> watcherEvent) {}

    private record InterceptorData(EventMetadata data, InterceptorEvent<?> watcherEvent) {}
}
