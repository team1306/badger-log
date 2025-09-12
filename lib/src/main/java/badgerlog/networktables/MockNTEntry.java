package badgerlog.networktables;

public record MockNTEntry(NTEntry<?> realEntry) implements NT, AutoCloseable {

    @Override
    public void close() throws Exception {
        realEntry.close();
    }
}
