package com.arkime.elasticsearch.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DebugLogger {

    @Value("${debug.mode}")
    private final String debugMode;

    @Autowired
    public DebugLogger(@Value("${debug.mode}") String debugMode) {
        this.debugMode = debugMode;
    }

    public <T> void errorMessage(String message, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.error(message);
        }
    }

    public <T> void error(String message, Object value, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.error(message + value);
        }
    }

    public <T> void errorException(String message, Exception e, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.error(message + e);
        }
    }

    public <T> void warnMessage(String message, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.warn(message);
        }
    }

    public <T> void warn(String message, Object value, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.warn(message + value);
        }
    }

    public <T> void warnException(String message, Exception e, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.warn(message + e);
        }
    }

    public <T> void info(String message, Object value, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.info(message + value);
        }
    }

    public <T> void debug(String message, Object value, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.debug(message + value);
        }
    }

}