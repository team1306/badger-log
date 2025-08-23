package badgerlog.networktables;

public interface NTEntry<T> {
    void publishValue(T value);

    T retrieveValue();

    String getKey();

    void close();
}
