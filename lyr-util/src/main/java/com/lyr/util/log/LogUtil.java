package com.lyr.util.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogUtil {
    private static final Logger ROOT_LOGGER =
            (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    public static void setLogLevelAtRoot(final Level level) {
        ROOT_LOGGER.setLevel(level);
    }

    public static String optionalToString(final Optional<?> optional) {
        return optional.isEmpty() ? "unspecified" : optional.get().toString();
    }

    public static <T> String optionalToString(final Optional<?> optional, final T defaultValue) {
        return optional.isEmpty() ? defaultValue.toString() : optional.get().toString();
    }
}
