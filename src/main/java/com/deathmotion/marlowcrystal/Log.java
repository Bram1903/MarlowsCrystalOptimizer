package com.deathmotion.marlowcrystal;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class Log {

    private static final String PREFIX = "[MarlowCrystal] ";

    private static final Logger LOGGER = LogUtils.getLogger();

    private Log() {
    }

    public static void info(String message) {
        LOGGER.info("{}{}", PREFIX, message);
    }

    public static void warn(String message) {
        LOGGER.warn("{}{}", PREFIX, message);
    }

    public static void error(String message) {
        LOGGER.error("{}{}", PREFIX, message);
    }
}
