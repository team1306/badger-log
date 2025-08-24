package badgerlog.networktables;

@SuppressWarnings("unused")
public interface NTEntry<T> extends NTCloseable, NT {
    void publishValue(T value);
    T retrieveValue();
    String getKey();
}
