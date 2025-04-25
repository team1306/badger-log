package badgerlog.networktables;

/**
 * Interface defining objects capable of synchronizing data with NetworkTables.
 * Implementations handle either publishing data to or retrieving data from NetworkTables entries.
 */
public interface Updater {
    /**
     * Performs a synchronization operation with NetworkTables. This may involve either
     * pushing local values to the network or updating local state with NetworkTables values.
     */
    void update();
}
