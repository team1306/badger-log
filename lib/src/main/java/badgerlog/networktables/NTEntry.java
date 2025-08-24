package badgerlog.networktables;

public interface NTEntry<T> extends NTCloseable, NT {
    void publishValue(T value);

    T retrieveValue();

    String getKey();
}
