package badgerlog.annotations.configuration;

import badgerlog.annotations.Table;

/**
 * Handles the {@link Table} annotation.
 */
public class TableHandler implements ConfigHandler<Table> {
    @Override
    public void process(Table annotation, Configuration config) {
        config.withTable(annotation.value());
    }
}
