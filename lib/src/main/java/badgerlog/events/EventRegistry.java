package badgerlog.events;

import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableEvent.Kind;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

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

        networkTableInstance.addListener(metadata.keys(), validMessages, (ntEvent) -> addNetworkTablesWatcherEvent(event, ntEvent));
        watcherData.add(new WatcherData(event, metadata));
    }

    public static void registerInterceptor(InterceptorEvent<?> event, EventMetadata metadata) {
        interceptorData.add(new InterceptorData(event, metadata));
    }

    @SuppressWarnings("unchecked")
    public static void addNetworkTablesWatcherEvent(WatcherEvent<?> watcherEvent, NetworkTableEvent event) {
        Object value = event.valueData.value.getValue();
        if (value == null) return;
        Class<?> type = value.getClass();
        
        if(!watcherEvent.matches(type)){
            return;
        }
        
        EventData<Object> data = new EventData<>(event.valueData.getTopic()
                .getName(), Timer.getFPGATimestamp(), null, event.valueData.value.getValue());
        eventQueue.add(new WatcherPair<>((WatcherEvent<Object>) watcherEvent, data));
    }

    public static List<? extends InterceptorEvent<?>> getInterceptorData(String name, String key, Class<?> type) {
        Predicate<InterceptorData> matchesIdentifier = data -> (!data.metadata().name().isBlank() && data.metadata().name().equals(name)) || Arrays.stream(data.metadata()
                .keys()).anyMatch(value -> value.contains(key));

        var matchingInterceptor = interceptorData.stream()
                .filter(matchesIdentifier)
                .filter(data -> data.interceptorEvent.matches(type))
                .sorted(Comparator.comparingInt(value -> value.metadata.priority()))
                .map(InterceptorData::interceptorEvent)
                .toList();

        return matchingInterceptor.isEmpty() ? List.of(createBlankInterceptorEvent(type)) : matchingInterceptor;
    }

    @SuppressWarnings("unchecked")
    public static InterceptorEvent<Object> createBlankInterceptorEvent(Class<?> type) {
        return new InterceptorEvent<>((Class<Object>) type, EventData::newValue);
    }

    private record WatcherPair<T>(WatcherEvent<T> watcher, EventData<T> data) {}

    private record WatcherData(WatcherEvent<?> watcherEvent, EventMetadata data) {}

    private record InterceptorData(InterceptorEvent<?> interceptorEvent, EventMetadata metadata) {}
}
