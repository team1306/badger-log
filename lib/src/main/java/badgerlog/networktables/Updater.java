package badgerlog.networktables;

/**
 * Interface representing a type that can be updated from NetworkTables.
 */
public interface Updater {
    /**
     * Publishes or gets a value from NetworkTables
     */
    void update();
}
