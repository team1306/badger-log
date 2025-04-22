package badgerlog.networktables.entries.publisher;

import com.google.common.base.Splitter;
import edu.wpi.first.util.struct.Struct;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Publisher} implementing the alternative method of putting a {@link Struct} to NetworkTables.
 * <br /> <br/>
 * It uses subtables on NetworkTables to put values to. Should be more friendly with dashboards such as Elastic.
 * The only {@linkplain Struct Structs} that are supported are ones that are <b>only doubles</b>, or <b>compositions of types with only doubles </b>
 *
 * @param <T> the starting type before mapping
 */
public final class SubtablePublisher<T> implements Publisher<T> {

    private final Struct<T> struct;

    private final List<Publisher<Double>> publishers;
    private final ByteBuffer buffer;

    /**
     * The default constructor for {@link SubtablePublisher}
     *
     * @param key    the key for NetworkTables
     * @param struct the {@link} Struct to use for the tables on NetworkTables
     */
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

    /**
     * A utility method to recursively create {@link Publisher} objects. This order should be the order the {@link ByteBuffer} is packed in the {@link Struct}
     *
     * @param baseStruct the base struct to search through
     * @param currentKey the current NetworkTables key
     */
    private void createPublishers(Struct<?> baseStruct, String currentKey) {
        for (Struct<?> nestedStruct : baseStruct.getNested()) {
            createPublishers(nestedStruct, currentKey + "/" + nestedStruct.getTypeName());
        }

        for (String part : Splitter.on(";").splitToList(baseStruct.getSchema())) {
            if (!part.startsWith("double")) continue;
            publishers.add(new ValuePublisher<>(currentKey + "/" + Splitter.on(" ").splitToList(part).get(1), double.class, null));
        }
    }
}
