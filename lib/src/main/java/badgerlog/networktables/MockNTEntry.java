package badgerlog.networktables;

/**
 * Mirrors a real entry on NetworkTables, but has no functionality, and allows for automatic closure of associated entry.
 * 
 * @param realEntry the entry that exists on NetworkTables
 */
public record MockNTEntry(NTEntry<?> realEntry) implements NT, AutoCloseable {
    @Override
    public void close() throws Exception {
        if(realEntry != null) realEntry.close();
    }
}
