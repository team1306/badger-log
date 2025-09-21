package badgerlog.annotations.configuration;

import badgerlog.annotations.Table;

public class TableHandler implements ConfigHandler<Table>{
    @Override
    public void process(Table annotation, Configuration config) {
        config.withTable(annotation.value());
    }
}
