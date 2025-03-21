package badgerlog.networktables;

import badgerlog.Dashboard;

/**
 * An interface for publishers, subscribers, and sendables to be updated by {@link Dashboard}
 */
public interface DashboardEntry {
    /**
     * Updates a NetworkTable entry
     */
    void update();
}
