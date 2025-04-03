package badgerlog.networktables.entries;

import badgerlog.Dashboard;
import badgerlog.networktables.Updater;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;

/**
 * An implementation of an {@link Updater} to allow {@link Sendable Sendables} to be updated properly from NetworkTables
 */
public final class SendableEntry implements Updater {

    private final Sendable sendable;

    /**
     * Default constructor for {@link SendableEntry}
     *
     * @param key      the key for NetworkTables
     * @param sendable the {@link Sendable} to be put on NetworkTables
     * @see Sendable
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
}
