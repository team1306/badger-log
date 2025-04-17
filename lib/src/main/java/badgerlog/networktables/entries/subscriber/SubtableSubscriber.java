package badgerlog.networktables.entries.subscriber;

import edu.wpi.first.util.struct.Struct;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Subscriber} implementing the alternative method of getting a {@link Struct} from NetworkTables.
 * <br /> <br/>
 * It uses subtables on NetworkTables to get values from. Should be more friendly with dashboards such as Elastic.
 * The only Structs that are supported are ones that are <b>only doubles</b>, or <b>compositions of types with only doubles </b>
 *
 * @param <T> the starting type before mapping
 */
public final class SubtableSubscriber<T> implements Subscriber<T> {

    private final Struct<T> struct;

    private final List<Subscriber<Double>> subscribers;
    private final ByteBuffer buffer;

    /**
     * The default constructor for {@link SubtableSubscriber}
     *
     * @param key          the key for NetworkTables
     * @param struct       the {@link} Struct to use for the tables on NetworkTables
     * @param defaultValue the default value to use for NetworkTables. This gets published immediately after startup
     */
    public SubtableSubscriber(String key, Struct<T> struct, T defaultValue) {
        this.struct = struct;

        subscribers = new ArrayList<>();
        buffer = ByteBuffer.allocate(struct.getSize());

        struct.pack(buffer, defaultValue);
        buffer.rewind();

        createSubscribers(struct, key);
    }

    @Override
    public T retrieveValue() {
        buffer.clear();

        for (Subscriber<Double> subscriber : subscribers) {
            buffer.putDouble(subscriber.retrieveValue());
        }
        buffer.rewind();
        return struct.unpack(buffer);
    }

    /**
     * A utility method to recursively create {@link Subscriber} objects. This order should be the order the {@link ByteBuffer} is packed in the {@link Struct}
     *
     * @param baseStruct the base {@link Struct} to search through
     * @param currentKey the current NetworkTables key
     */
    private void createSubscribers(Struct<?> baseStruct, String currentKey) {
        for (Struct<?> nestedStruct : baseStruct.getNested()) {
            createSubscribers(nestedStruct, currentKey + "/" + nestedStruct.getTypeName());
        }

        for (String part : baseStruct.getSchema().split(";")) {
            if (!part.startsWith("double")) continue;

            subscribers.add(new ValueSubscriber<>(currentKey + "/" + part.split(" ")[1], double.class, buffer.getDouble(), null));
        }
    }
}
