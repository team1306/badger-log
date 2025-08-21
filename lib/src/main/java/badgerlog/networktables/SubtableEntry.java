package badgerlog.networktables;

import edu.wpi.first.util.struct.Struct;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Combined publisher/subscriber implementation for struct values stored in nested NetworkTables subtables.
 * Represents the second of three struct handling methods, decomposing structs into individual double entries
 * within hierarchical subtables. Supports structs
 * containing only doubles or nested structs of doubles.
 *
 * @param <T> Struct type implementing {@link edu.wpi.first.util.struct.StructSerializable}
 */
@SuppressWarnings("unchecked")
public final class SubtableEntry<T> implements NTEntry<T> {

    private final Struct<T> struct;

    private final Map<NTEntry<?>, Subtables.PrimType<?>> entries;
    private final ByteBuffer buffer;

    private final String key;

    /**
     * Constructs a subtable handler and initializes NetworkTables entries with default values.
     *
     * @param key          Base NetworkTables path for struct data hierarchy
     * @param struct       Struct definition describing data layout
     * @param defaultValue Initial values to populate in subtables
     */
    public SubtableEntry(String key, Struct<T> struct, T defaultValue) {
        this.struct = struct;
        this.key = key;

        buffer = ByteBuffer.allocate(struct.getSize());

        entries = Subtables.createEntries(struct, key, defaultValue);
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
}
