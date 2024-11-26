package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging;

import lombok.Getter;

@Getter
public class LogEntry {

    private final Level level;
    private final Throwable error;
    private final String message;
    private final Class<?> clazz;

    public LogEntry(Level level, String message, Throwable error, Class<?> clazz) {
        this.level = level;
        this.error = error;
        this.message = message;
        this.clazz = clazz;
    }

    public enum Level {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}
