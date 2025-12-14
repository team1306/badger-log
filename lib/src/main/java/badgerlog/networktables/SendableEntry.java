package badgerlog.networktables;

import badgerlog.BadgerLog;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;

/**
 * Creates and manages a {@link Sendable} object, allowing it to be periodically updated.
 */
public final class SendableEntry implements NTUpdatable, AutoCloseable {

    private final Sendable sendable;

    /**
     * Constructs a new SendableEntry, registering and publishing the {@link Sendable}
     *
     * @param key the key to use on NetworkTables
     * @param sendable the Sendable to publish
     */
    public SendableEntry(String key, Sendable sendable) {
        this.sendable = sendable;

        NetworkTable dataTable = BadgerLog.defaultTable.getSubTable(key);
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
