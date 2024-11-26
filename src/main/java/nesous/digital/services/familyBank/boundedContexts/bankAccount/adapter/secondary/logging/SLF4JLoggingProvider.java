package nesous.digital.services.familyBank.boundedContexts.bankAccount.adapter.secondary.logging;


import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging.LogEntry;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.logging.LoggingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SLF4JLoggingProvider implements LoggingProvider {

    @Override
    public void log(LogEntry logEntry) {
        Throwable error = logEntry.getError();
        LogEntry.Level level = logEntry.getLevel();
        String message = logEntry.getMessage();
        Logger logger = LoggerFactory.getLogger(logEntry.getClazz());

        switch (level) {
            case TRACE:
                logger.trace(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            case WARN:
                logger.warn(message, error);
                break;
            case ERROR:
                logger.error(message, error);
                break;
            default:
                logger.info(message);
        }
    }
}
