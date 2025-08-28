package badgerlog.annotations;

/**
 * Represents all the possible options for publishing structs to NetworkTables.
 */
public enum StructType {
    /**
     * Uses the built-in NetworkTables struct publishing
     */
    STRUCT,
    /**
     * Uses a custom implementation of subtable struct publishing. It creates entries for all values in the struct's schema
     */
    SUB_TABLE,
    /**
     * Uses a mapping to convert the struct to a double or double array
     */
    MAPPING,
}