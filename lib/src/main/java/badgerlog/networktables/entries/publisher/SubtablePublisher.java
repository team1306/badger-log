package badgerlog.networktables.entries.publisher;

import edu.wpi.first.util.struct.Struct;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/// [Publisher] implementing the alternative method of putting a [Struct] to NetworkTables.
///   
///   
/// It uses subtables on NetworkTables to put values to. Should be more friendly with dashboards such as Elastic.
/// The only {@linkplain Struct Structs} that are supported are ones that are **only doubles**, or **compositions of types with only doubles **
///
/// @param <T> the starting type before mapping
public final class SubtablePublisher<T> implements Publisher<T> {

    private final Struct<T> struct;

    private final List<Publisher<Double>> publishers;
    private final ByteBuffer buffer;

    /// The default constructor for [SubtablePublisher]
    ///
    /// @param key    the key for NetworkTables
    /// @param struct the [] Struct to use for the tables on NetworkTables
    public SubtablePublisher(String key, Struct<T> struct) {
        this.struct = struct;

        publishers = new ArrayList<>();
        buffer = ByteBuffer.allocate(struct.getSize());

        createPublishers(struct, key);
    }

    @Override
    public void publishValue(T value) {
        struct.pack(buffer, value);

        buffer.rewind();
        for (Publisher<Double> publisher : publishers) {
            publisher.publishValue(buffer.getDouble());
        }

        buffer.clear();
    }

    /// A utility method to recursively create [Publisher] objects. This order should be the order the [ByteBuffer] is packed in the [Struct]
    ///
    /// @param baseStruct the base struct to search through
    /// @param currentKey the current NetworkTables key
    private void createPublishers(Struct<?> baseStruct, String currentKey) {
        for (Struct<?> nestedStruct : baseStruct.getNested()) {
            createPublishers(nestedStruct, currentKey + "/" + nestedStruct.getTypeName());
        }

        for (String part : baseStruct.getSchema().split(";")) {
            if (!part.startsWith("double")) continue;

            publishers.add(new ValuePublisher<>(currentKey + "/" + part.split(" ")[1], double.class, null));
        }
    }
}
