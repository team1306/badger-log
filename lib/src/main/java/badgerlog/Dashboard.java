package badgerlog;

import badgerlog.annotations.configuration.Configuration;
import badgerlog.networktables.EntryFactory;
import badgerlog.networktables.NT;
import badgerlog.networktables.NTEntry;
import badgerlog.networktables.NTUpdatable;
import badgerlog.networktables.SendableEntry;
import badgerlog.networktables.ValueEntry;
import badgerlog.utilities.CheckedNetworkTablesMap;
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

/**
 * Provides utility methods for interacting with NetworkTables.
 */
@SuppressWarnings({"unused", "resource"})
public final class Dashboard {

    public static final NetworkTable defaultTable = NetworkTableInstance.getDefault().getTable("BadgerLog");
    private static final CheckedNetworkTablesMap activeEntries = new CheckedNetworkTablesMap();

    public static DashboardConfig config = DashboardConfig.defaultConfig;

    private Dashboard() {
    }

    /**
     * Changes the default configuration options to use when the specific version is missing on a field.
     *
     * @param config the configuration to use as a default
     */
    public static void configure(DashboardConfig config) {
        Dashboard.config = config;
    }

    /**
     * {@code defaultValue} defaults to the first defined Enum constant
     *
     * @see #createSelectorFromEnum(String, Class, Enum, Consumer)
     */
    public static <T extends Enum<T>> Optional<SendableChooser<Enum<T>>> createSelectorFromEnum(String key, Class<T> tEnum, Consumer<Enum<T>> onValueChange) {
        if (validateEnum(tEnum)) {
            System.err.println(key + " was trying to create an enum selector, but failed. SKIPPING");
            return Optional.empty();
        }
        return createSelectorFromEnum(key, tEnum, tEnum.getEnumConstants()[0], onValueChange);
    }

    /**
     * Creates a {@link SendableChooser} that contains the name of each Enum constant as an option.
     *
     * @param key the key on NetworkTables
     * @param tEnum the class of the Enum
     * @param startingValue the starting Enum value to use on the SendableChooser
     * @param onValueChange a {@link Consumer} that gets called on startup, and whenever the selector changes with the
     * value it changed to
     * @param <T> the type of the Enum
     *
     * @return the created and published SendableChooser
     */
    public static <T extends Enum<T>> Optional<SendableChooser<Enum<T>>> createSelectorFromEnum(String key, Class<T> tEnum, Enum<T> startingValue, Consumer<Enum<T>> onValueChange) {
        if (validateEnum(tEnum)) {
            System.err.println(key + " was trying to create an enum selector, but failed. SKIPPING");
            return Optional.empty();
        }
        SendableChooser<Enum<T>> chooser = new SendableChooser<>();
        chooser.setDefaultOption(startingValue.toString(), startingValue);
        for (Enum<T> value : tEnum.getEnumConstants()) {
            if (value == startingValue) {
                continue;
            }
            chooser.addOption(value.toString(), value);
        }
        chooser.onChange(onValueChange);
        onValueChange.accept(startingValue);
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

    /**
     * Adds an implementation of {@link NT} to the keymap to keep track of created entries.
     *
     * @param key the key on NetworkTables
     * @param entry the implementation of NT to put into the map
     */
    public static void addNetworkTableEntry(String key, NT entry) {
        activeEntries.put(key, entry);
    }

    /**
     * Removes a key from the list of entries to be updated. Any publishers or subscribers are closed, and the entry is
     * removed.
     *
     * @param key the key on NetworkTables
     *
     * @return whether the entry existed
     */
    public static boolean removeNetworkTableEntry(String key) {
        return activeEntries.remove(key) != null;
    }

    /**
     * Updates all the {@link NT} entries that also implement {@link NTUpdatable}.
     * This method is used to update NetworkTables or the robot code with any changed values.
     * <p>Should be called in {@code Robot.robotPeriodic}</p>
     */
    public static void update() {
        activeEntries.getUpdaters().values().forEach(NTUpdatable::update);
    }

    /**
     * Creates a {@link Trigger} instance that is bound to a boolean value at {@code key} on NetworkTables.
     *
     * @param key the key on NetworkTables
     * @param eventLoop the eventLoop to bind the Trigger to
     *
     * @return a Trigger with a toggle based on a boolean NetworkTables entry
     */
    public static Trigger createNetworkTablesButton(String key, EventLoop eventLoop) {
        var subscriber = new ValueEntry<>(key, boolean.class, false, new Configuration());
        addNetworkTableEntry(key, subscriber);
        return new Trigger(eventLoop, subscriber::retrieveValue);
    }

    /**
     * Creates a {@link Trigger} that resets its NetworkTables entry to false, after being true for 0.25 seconds.
     *
     * @see #createNetworkTablesButton(String, EventLoop)
     */
    public static Trigger createAutoResettingButton(String key, EventLoop eventLoop) {
        return createNetworkTablesButton(key, eventLoop).onTrue(Commands.waitSeconds(0.25)
                .andThen(new InstantCommand(() -> putValue(key, false)).ignoringDisable(true)));
    }


    /**
     * {@code config} defaults to the base configuration
     *
     * @see #putValue(String, Object, Configuration)
     */
    public static <T> void putValue(String key, T value) {
        putValue(key, value, new Configuration());
    }

    /**
     * Publishes a value to NetworkTables with a specific configuration.
     * <p>Annotations should be preferred over this method if possible.</p>
     *
     * @param key the key on NetworkTables
     * @param value the value to publish
     * @param config the configuration to use for the {@link ValueEntry}
     * @param <T> the type of the value
     */
    public static <T> void putValue(String key, T value, Configuration config) {
        NTEntry<T> entry = createEntryIfNotPresent(key, value, config);

        entry.publishValue(value);
    }

    /**
     * {@code config} defaults to the base configuration
     *
     * @see #getValue(String, Object, Configuration)
     */
    public static <T> T getValue(String key, T defaultValue) {
        return getValue(key, defaultValue, new Configuration());
    }

    /**
     * Gets a value from NetworkTables with a specific configuration.
     * <p>Annotations should be preferred over this method if possible.</p>
     *
     * @param key the key on NetworkTables
     * @param defaultValue the value to use if the entry is missing on NetworkTables
     * @param config the configuration to use for the {@link ValueEntry}
     * @param <T> the type of the value
     *
     * @return the value on NetworkTables if present, otherwise the value specified
     */
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
            entry = (NTEntry<T>) activeEntries.getNTEntry(key);

            if (entry == null) {
                throw new NullPointerException("Entry with name " + key + " either is using a key that is already used, or the entry is just invalid");
            }
        }
        return entry;
    }

    /**
     * Adds a {@link Sendable} to the active entries to be updated.
     *
     * @param key the key on NetworkTables
     * @param sendable the Sendable to put on NetworkTables
     */
    public static void putSendable(String key, Sendable sendable) {
        addNetworkTableEntry(key, new SendableEntry(key, sendable));
    }
}
