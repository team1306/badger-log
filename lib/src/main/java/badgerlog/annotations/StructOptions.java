package badgerlog.annotations;

import edu.wpi.first.util.struct.Struct;

/**
 * Options for changing how {@link Struct} are published to NetworkTables
 */
public enum StructOptions {
    /**
     * Publish the struct as is and registering the schema with NetworkTables
     */
    STRUCT,
    /**
     * Publish the struct as a collection of subtables
     */
    SUB_TABLE,
    /**
     * Publish the struct as a value mapped to a NetworkTableType
     */
    MAPPING,
}