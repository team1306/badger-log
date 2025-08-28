package badgerlog.networktables;

/**
 * Interface for NetworkTables classes that can and should be updated periodically.
 */
public interface NTUpdatable extends NT {
    /**
     * Updates the associated entry on NetworkTables
     */
    void update();
}
