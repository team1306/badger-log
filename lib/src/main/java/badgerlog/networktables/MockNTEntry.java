package badgerlog.networktables;

public record MockNTEntry(NTEntry<?> realEntry) implements NT, AutoCloseable {

    public MockNTEntry() {
        this(null);
    }
    
    @Override
    public void close() throws Exception {
        if(realEntry != null) realEntry.close();
    }
}
