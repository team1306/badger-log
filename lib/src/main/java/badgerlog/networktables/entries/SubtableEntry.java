package badgerlog.networktables.entries;

import badgerlog.entry.Configuration;
import com.google.common.base.Splitter;
import edu.wpi.first.util.struct.Struct;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Combined publisher/subscriber implementation for struct values stored in nested NetworkTables subtables.
 * Represents the second of three struct handling methods, decomposing structs into individual double entries
 * within hierarchical subtables. Supports structs
 * containing only doubles or nested structs of doubles.
 *
 * @param <T> Struct type implementing {@link edu.wpi.first.util.struct.StructSerializable}
 */
public final class SubtableEntry<T> implements Subscriber<T>, Publisher<T> {

    private final Struct<T> struct;

    private final List<ValueEntry<Double>> entries;
    private final ByteBuffer buffer;

    /**
     * Constructs a subtable handler and initializes NetworkTables entries with default values.
     *
     * @param key          Base NetworkTables path for struct data hierarchy
     * @param struct       Struct definition describing data layout
     * @param defaultValue Initial values to populate in subtables
     */
    public SubtableEntry(String key, Struct<T> struct, T defaultValue) {
        this.struct = struct;

        entries = new ArrayList<>();
        buffer = ByteBuffer.allocate(struct.getSize());

        struct.pack(buffer, defaultValue);
        buffer.rewind();

        createEntries(struct, key);
    }

    @Override
    public T retrieveValue() {
        buffer.clear();

        for (Subscriber<Double> subscriber : entries) {
            buffer.putDouble(subscriber.retrieveValue());
        }
        buffer.rewind();
        return struct.unpack(buffer);
    }

    @Override
    public void publishValue(T value) {
        struct.pack(buffer, value);

        buffer.rewind();
        for (Publisher<Double> publisher : entries) {
            publisher.publishValue(buffer.getDouble());
        }

        buffer.clear();
    }

    /**
     * Recursively creates ValueEntry objects mirroring the struct's schema. Maintains
     * native packing order through depth-first traversal of nested structs. Automatically
     * skips non-double fields in schema definitions.
     *
     * @param baseStruct Struct hierarchy to analyze
     * @param currentKey Current NetworkTables path component
     */
    private void createEntries(Struct<?> baseStruct, String currentKey) {
        for (Struct<?> nestedStruct : baseStruct.getNested()) {
            createEntries(nestedStruct, currentKey + "/" + nestedStruct.getTypeName());
        }

        for (String part : Splitter.on(";").splitToList(baseStruct.getSchema())) {
            if (!part.startsWith("double")) continue;
            entries.add(new ValueEntry<>(currentKey + "/" + Splitter.on(" ").splitToList(part).get(1), double.class, buffer.getDouble(), Configuration.defaultConfiguration));
        }
    }
}
