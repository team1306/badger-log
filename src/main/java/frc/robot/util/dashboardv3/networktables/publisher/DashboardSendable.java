package frc.robot.util.dashboardv3.networktables.publisher;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import frc.robot.util.dashboardv3.Dashboard;
import frc.robot.util.dashboardv3.networktables.DashboardEntry;

public class DashboardSendable implements DashboardEntry {

    public final Sendable sendable;

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
