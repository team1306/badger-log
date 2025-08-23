package badgerlog.networktables;

import badgerlog.annotations.configuration.Configuration;
import edu.wpi.first.util.struct.Struct;

import java.nio.ByteBuffer;
import java.util.*;

public class Subtables {
    /**
     * A map of primitive types to their schema types.
     */
    private static final HashMap<String, PrimType<?>> primitiveTypeMap = new HashMap<>();

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
     * Recursively creates ValueEntry objects mirroring the struct's schema. Maintains
     * native packing order through depth-first traversal of nested structs. Automatically
     * skips non-double fields in schema definitions.
     *
     * @param baseStruct Struct hierarchy to analyze
     */
    public static <T> Map<NTEntry<?>, PrimType<?>> createEntries(Struct<T> baseStruct, String baseKey, T value) {
        ByteBuffer buffer = ByteBuffer.allocate(baseStruct.getSize());
        buffer.clear();
        baseStruct.pack(buffer, value);
        buffer.rewind();

        Map<NTEntry<?>, PrimType<?>> entries = new LinkedHashMap<>();
        boolean valid = createEntriesImpl(baseStruct, baseKey, buffer, entries, 0);
        if (!valid) {
            entries.keySet().forEach(NTEntry::close);
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
            String[] partSplit = part.split(" ", 2);

            if (!primitiveTypeMap.containsKey(partSplit[0])) {
                List<Struct<?>> structs = Arrays.stream(baseStruct.getNested()).filter(struct -> Objects.equals(struct.getTypeName(), partSplit[0])).toList();
                if (structs.size() != 1) {
                    System.out.println("INVALID Struct definition: " + baseStruct.getTypeName() + ". REMOVING ALL");
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
     * A functional interface representing a method that retrieves a value from a {@link ByteBuffer}.
     */
    @FunctionalInterface
    public interface Unpacker<T> {
        T unpack(ByteBuffer buffer);
    }

    /**
     * A functional interface representing a method that packs a value into a {@link ByteBuffer}.
     */
    @FunctionalInterface
    public interface Packer<T> {
        void pack(ByteBuffer buffer, T value);
    }

    public record PrimType<T>(String name, Class<T> type, Unpacker<T> unpacker, Packer<T> packer) {
    }
}
