package badgerlog.networktables;

import edu.wpi.first.util.struct.Struct;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Implements the {@code StructType.SUB_TABLE} type for NetworkTables.
 *
 * <p>It creates an entry for every primitive type in the struct schema, and puts it under different subtables based off
 * the nesting in the schema.</p>
 *
 * @param <T> the type to use. Does not need to be a valid NetworkTableType
 */
@SuppressWarnings("unchecked")
public final class SubtableEntry<T> implements NTEntry<T> {

    private final Struct<T> struct;

    private final Map<NTEntry<?>, Subtables.PrimType<?>> entries;
    private final ByteBuffer buffer;

    private final String key;

    /**
     * Constructs a new SubtableEntry, creating all the entries on NetworkTables under the specified key.
     *
     * <p>This initially publishes the {@code initialValue} to make the entry appear on NetworkTables.</p>
     *
     * @param key the top level key to use on NetworkTables, all other entries will be nested under it.
     * @param struct the struct to use for creating the entries
     * @param initialValue the initial value to be published to NetworkTables
     */
    public SubtableEntry(String key, Struct<T> struct, T initialValue) {
        this.struct = struct;
        this.key = key;

        buffer = ByteBuffer.allocate(struct.getSize());

        entries = Subtables.createEntries(struct, key, initialValue);
    }

    @SneakyThrows
    @Override
    public void close() {
        for (NTEntry<?> entry : entries.keySet()) {
            entry.close();
        }
    }

    @Override
    public void publishValue(T value) {
        buffer.clear();
        struct.pack(buffer, value);

        buffer.rewind();
        for (Map.Entry<NTEntry<?>, Subtables.PrimType<?>> entry : entries.entrySet()) {
            Subtables.Unpacker<Object> unpacker = (Subtables.Unpacker<Object>) entry.getValue().unpacker();
            NTEntry<Object> ntEntry = (NTEntry<Object>) entry.getKey();
            ntEntry.publishValue(unpacker.unpack(buffer));
        }

        buffer.clear();
    }

    @Override
    public T retrieveValue() {
        buffer.clear();

        for (Map.Entry<NTEntry<?>, Subtables.PrimType<?>> entry : entries.entrySet()) {
            Subtables.Packer<Object> packer = (Subtables.Packer<Object>) entry.getValue().packer();
            NTEntry<Object> ntEntry = (NTEntry<Object>) entry.getKey();
            packer.pack(buffer, ntEntry.retrieveValue());
        }

        buffer.rewind();
        return struct.unpack(buffer);
    }

    @Override
    public String getKey() {
        return key;
    }
}
