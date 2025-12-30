package badgerlog;

import badgerlog.annotations.StructType;

public class BadgerLogConfig {
    private LogLevel level;
    
    private RuntimeFailMode failMode;
    
    private StructType structType;

    public LogLevel getLevel() {
        return level;
    }

    public RuntimeFailMode getFailMode() {
        return failMode;
    }

    public StructType getStructType() {
        return structType;
    }

    public enum LogLevel {
        NONE,
        WARN,
        INFO,
        DEBUG,
    }
    
    public enum RuntimeFailMode {
        EXCEPTION,
        WARNING,
        HIDDEN,
    }
}
