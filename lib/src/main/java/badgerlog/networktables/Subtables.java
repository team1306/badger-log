package badgerlog.networktables;

import badgerlog.annotations.configuration.Configuration;
import edu.wpi.first.util.struct.Struct;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Internal class used by BadgerLog to create subtables from a Struct schema.
 */
public final class Subtables {
    private static final HashMap<String, PrimType<?>> primitiveTypeMap = new HashMap<>();

    private Subtables() {
    }

    static {
        addPrimType("int32", int.class, ByteBuffer::getInt, ByteBuffer::putInt);
        addPrimType("float64", double.class, ByteBuffer::getDouble, ByteBuffer::putDouble);
        addPrimType("float32", float.class, ByteBuffer::getFloat, ByteBuffer::putFloat);
        addPrimType("bool", boolean.class, buffer -> buffer.get() != 0, (buffer, value) -> buffer.put((byte) (value ? 1 : 0)));
        addPrimType("char", char.class, ByteBuffer::getChar, ByteBuffer::putChar);
        addPrimType("uint8", byte.class, ByteBuffer::get, ByteBuffer::put);
        addPrimType("int16", short.class, ByteBuffer::getShort, ByteBuffer::putShort);
        addPrimType("int64", long.class, ByteBuffer::getLong, ByteBuffer::putLong);
        addPrimType("double", double.class, ByteBuffer::getDouble, ByteBuffer::putDouble);

    }

    private static <T> void addPrimType(String name, Class<T> type, Unpacker<T> unpacker, Packer<T> packer) {
        PrimType<T> primType = new PrimType<>(name, type, unpacker, packer);
        primitiveTypeMap.put(name, primType);
    }

    /**
     * Creates an ordered map that contains all the entries created from the {@code struct} schema.
     * <p>Uses a buffer to pack the {@code initialValue} of the struct in, and then construct the entries from.
     * Empties the entire map if the {@code struct}'s schema is invalid.</p>
     *
     * @param struct the struct to generate the entries from
     * @param key    the key on NetworkTables
     * @param value  the initial value
     * @param <T>    the type of the struct
     * @return an ordered map containing all the entries created from the schema, ordered by how the buffer is packed
     */
    @SneakyThrows
    public static <T> Map<NTEntry<?>, PrimType<?>> createEntries(Struct<T> struct, String key, T value) {
        ByteBuffer buffer = ByteBuffer.allocate(struct.getSize());
        buffer.clear();
        struct.pack(buffer, value);
        buffer.rewind();

        Map<NTEntry<?>, PrimType<?>> entries = new LinkedHashMap<>();
        boolean valid = createEntriesImpl(struct, key, buffer, entries, 0);
        if (!valid) {
            for (NTEntry<?> entry : entries.keySet()) {
                entry.close();
            }
            entries.clear();
        }
        return entries;
    }

    @SuppressWarnings("unchecked")
    private static boolean createEntriesImpl(Struct<?> baseStruct, String currentKey, ByteBuffer packedBuffer, Map<NTEntry<?>, PrimType<?>> entries, int limit) {
        if (limit + 1 >= 1000) {
            throw new IllegalArgumentException("Infinite recursive loop for struct class: " + baseStruct.getTypeClass().getSimpleName());
        }
        boolean stillValid = true;
        for (String part : baseStruct.getSchema().split(";", -1)) {
            if (!stillValid) return false;
            if (part.isBlank()) continue;
            String[] partSplit = part.split(" ", 2);

            if (!primitiveTypeMap.containsKey(partSplit[0])) {
                List<Struct<?>> structs = Arrays.stream(baseStruct.getNested()).filter(struct -> Objects.equals(struct.getTypeName(), partSplit[0])).toList();
                if (structs.isEmpty()) {
                    System.err.println("INVALID Struct definition: " + baseStruct.getTypeName() + ". REMOVING ALL");
                    return false;
                }

                Struct<?> nestedStruct = structs.get(0);
                stillValid = createEntriesImpl(nestedStruct, currentKey + "/" + nestedStruct.getTypeName(), packedBuffer, entries, limit + 1);
                continue;
            }
            PrimType<?> primType = primitiveTypeMap.get(partSplit[0]);

            entries.put(new ValueEntry<>(currentKey + "/" + partSplit[1], (Class<Object>) primType.type, primType.unpacker.unpack(packedBuffer), new Configuration()), primType);
        }

        return true;
    }

    /**
     * Internal interface used by BadgerLog to represent the operation of unpacking a ByteBuffer. 
     * @param <T> the type to unpack
     */
    @FunctionalInterface
    public interface Unpacker<T> {
        /**
         * Unpacks a value.
         * @param buffer the buffer to unpack from
         * @return the value of the result
         */
        T unpack(ByteBuffer buffer);
    }

    /**
     * Internal interface used by BadgerLog to represent the operation of packing a ByteBuffer
     * @param <T> the type to pack
     */
    @FunctionalInterface
    public interface Packer<T> {
        /**
         * Packs a value.
         * @param buffer the buffer to pack into
         * @param value the value to pack into the buffer
         */
        void pack(ByteBuffer buffer, T value);
    }

    /**
     * Internal record used by BadgerLog to represent a base NetworkTables type with a {@link Packer} and {@link Unpacker} for buffers.
     * @param name the name of the type
     * @param type the class representing the type
     * @param unpacker the unpacker to use when unpacking
     * @param packer the packer to use when packing
     * @param <T> the type used for packing and unpacking
     */
    public record PrimType<T>(String name, Class<T> type, Unpacker<T> unpacker, Packer<T> packer) {
    }
}
