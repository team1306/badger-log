package badgerlog.events;

/**
 * Holds data relevant to one specific value change
 *
 * @param key the key on NetworkTables
 * @param timestamp the FPGA timestamp when the value changed
 * @param newValue the value the event is firing for
 * @param <T> the type of the event
 */
public record EventData<T>(String key, double timestamp, T newValue) {
}
