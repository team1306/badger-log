package badgerlog.networktables.publisher;

import badgerlog.Dashboard;
import badgerlog.networktables.DashboardEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;


/**
 * This class represents a sendable to be put and updated on NetworkTables.
 * The sendable is updated when the {@link #update()} method is called
 */
public class DashboardSendable implements DashboardEntry {

    private final Sendable sendable;

    /**
     * Default constructor of Dashboard Sendable
     *
     * @param key      the key for NetworkTables
     * @param sendable the value of the {@link Sendable}
     */
    public DashboardSendable(String key, Sendable sendable) {
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
