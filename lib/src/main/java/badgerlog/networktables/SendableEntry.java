package badgerlog.networktables;

import badgerlog.Dashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;

/**
 * Handles publishing and updating {@link Sendable} objects in NetworkTables.Synchronizes Sendable data through periodic updates.
 */
public final class SendableEntry implements Updater, NTCloseable {

    private final Sendable sendable;

    /**
     * Creates a Sendable entry and publishes it to a NetworkTables subtable.
     *
     * @param key      NetworkTables key path for the Sendable data
     * @param sendable The {@link Sendable} object to publish
     */
    public SendableEntry(String key, Sendable sendable) {
        this.sendable = sendable;

        NetworkTable dataTable = Dashboard.defaultTable.getSubTable(key);
        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(dataTable);
        SendableRegistry.publish(sendable, builder);
        builder.startListeners();

        dataTable.getEntry(".name").setString(key);
    }

    @Override
    public void update() {
        SendableRegistry.update(sendable);
    }

    @Override
    public void close() {
        SendableRegistry.remove(sendable);
    }
}
