package com.marlowcrystal.util;

import com.mojang.logging.LogUtils;

public class Logger {

    private final static String PREFIX = "[MarlowCrystal] ";
    private final org.slf4j.Logger logger = LogUtils.getLogger();

    public final void info(final String message) {
        logger.info(PREFIX + "{}", message);
    }

    public final void warn(final String message) {
        logger.warn(PREFIX + "{}", message);
    }

    public final void error(final String message) {
        logger.error(PREFIX + "{}", message);
    }

    public final void debug(final String message) {
        logger.debug(PREFIX + "{}", message);
    }
}
