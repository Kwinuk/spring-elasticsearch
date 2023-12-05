package com.arkime.common.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Debugger {

    @Value("${debug.mode}")
    private final String debugMode;

    @Autowired
    public Debugger(@Value("${debug.mode}") String debugMode) {
        this.debugMode = debugMode;
    }

    public <T> void error(String message, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.error(message);
        }
    }

    public <T> void error(String message, Object value, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.error(message, value);
        }
    }

    public <T> void error(String message, Object value, Object value2, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.error(message, value, value2);
        }
    }

    public <T> void error(String message, Object value, Object value2, Object value3, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.error(message, value, value2, value3);
        }
    }

    public <T> void warn(String message, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.warn(message);
        }
    }

    public <T> void warn(String message, Object value, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.warn(message, value);
        }
    }

    public <T> void warn(String message, Object value, Object value2, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.warn(message, value, value2);
        }
    }

    public <T> void warn(String message, Object value, Object value2, Object value3, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.warn(message, value, value2, value3);
        }
    }

    public <T> void info(String message, Object value, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.info(message, value);
        }
    }

    public <T> void info(String message, Object value, Object value2, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.info(message, value, value2);
        }
    }

    public <T> void info(String message, Object value, Object value2, Object value3, Class<T> className) {
        final Logger log = LogManager.getLogger(className);

        if ("on".equalsIgnoreCase(debugMode)) {
            log.info(message, value, value2, value3);
        }
    }

}