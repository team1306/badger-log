package badgerlog;

import badgerlog.annotations.configuration.Configuration;
import badgerlog.networktables.*;
import badgerlog.processing.CheckedNetworkTablesMap;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import java.util.Optional;
import java.util.function.Consumer;

public final class Dashboard {

    private static final CheckedNetworkTablesMap activeEntries = new CheckedNetworkTablesMap();

    public static DashboardConfig config = DashboardConfig.defaultConfig;
    public static NetworkTable defaultTable = NetworkTableInstance.getDefault().getTable("BadgerLog");

    private Dashboard() {
    }

    public static void configure(DashboardConfig config) {
        Dashboard.config = config;
    }

    public static <T extends Enum<T>> Optional<SendableChooser<Enum<T>>> createSelectorFromEnum(String key, Class<T> tEnum, Consumer<Enum<T>> onValueChange) {
        if (validateEnum(tEnum)) {
            System.err.println(key + " was trying to create an enum selector, but failed. SKIPPING");
            return Optional.empty();
        }
        return createSelectorFromEnum(key, tEnum, tEnum.getEnumConstants()[0], onValueChange);
    }

    public static <T extends Enum<T>> Optional<SendableChooser<Enum<T>>> createSelectorFromEnum(String key, Class<T> tEnum, Enum<T> defaultValue, Consumer<Enum<T>> onValueChange) {
        if (validateEnum(tEnum)) {
            System.err.println(key + " was trying to create an enum selector, but failed. SKIPPING");
            return Optional.empty();
        }
        SendableChooser<Enum<T>> chooser = new SendableChooser<>();
        chooser.setDefaultOption(defaultValue.toString(), defaultValue);
        for (Enum<T> value : tEnum.getEnumConstants()) {
            if (value == defaultValue) continue;
            chooser.addOption(value.toString(), value);
        }
        chooser.onChange(onValueChange);
        onValueChange.accept(defaultValue);
        Dashboard.putSendable(key, chooser);
        return Optional.of(chooser);
    }

    private static <T extends Enum<T>> boolean validateEnum(Class<T> tEnum) {
        if (!tEnum.isEnum()) {
            System.err.println("Tried to create an enum selector, but the class was not an enum");
            return true;
        }
        if (tEnum.getEnumConstants().length == 0) {
            System.err.println("Tried to create an enum selector, but the enum had no values");
            return true;
        }
        return false;
    }

    public static void addNetworkTableEntry(String key, NT entry) {
        activeEntries.put(key, entry);
    }

    public static void update() {
        activeEntries.getUpdaters().values().forEach(Updater::update);
    }

    public static Trigger getNetworkTablesButton(String key, EventLoop eventLoop) {
        var subscriber = new ValueEntry<>(key, boolean.class, false, new Configuration());
        return new Trigger(eventLoop, subscriber::retrieveValue);
    }

    public static Trigger getAutoResettingButton(String key, EventLoop eventLoop) {
        return getNetworkTablesButton(key, eventLoop)
                .onTrue(Commands.waitSeconds(0.25).andThen(new InstantCommand(() -> putValue(key, false)).ignoringDisable(true)));
    }

    public static <T> void putValue(String key, T value) {
        putValue(key, value, new Configuration());
    }

    public static <T> void putValue(String key, T value, Configuration config) {
        NTEntry<T> entry = createEntryIfNotPresent(key, value, config);

        entry.publishValue(value);
    }

    public static <T> T getValue(String key, T defaultValue) {
        return getValue(key, defaultValue, new Configuration());
    }

    public static <T> T getValue(String key, T defaultValue, Configuration config) {
        NTEntry<T> entry = createEntryIfNotPresent(key, defaultValue, config);

        return entry.retrieveValue();
    }

    @SuppressWarnings("unchecked")
    private static <T> NTEntry<T> createEntryIfNotPresent(String key, T defaultValue, Configuration config) {
        NTEntry<T> entry;
        if (!activeEntries.containsKey(key)) {
            entry = EntryFactory.createNetworkTableEntryFromValue(key, defaultValue, config);
            addNetworkTableEntry(key, entry);
        } else {
            try {
                entry = (NTEntry<T>) activeEntries.getNTEntry(key);
            } catch (ClassCastException e) {
                activeEntries.remove(key);
                return createEntryIfNotPresent(key, defaultValue, config);
            }
        }
        return entry;
    }

    public static void putSendable(String key, Sendable sendable) {
        activeEntries.put(key, new SendableEntry(key, sendable));
    }
}
