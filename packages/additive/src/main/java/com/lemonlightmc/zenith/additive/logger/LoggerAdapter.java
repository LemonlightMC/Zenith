package com.lemonlightmc.zenith.additive.logger;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.DefaultLoggingEventBuilder;

/**
 * This class is responsible for adapting the JUL logger instance against the
 * SLF4J {@link org.slf4j.Logger} interface.
 */
public final class LoggerAdapter implements Logger {

    /**
     * Marker for method entry tracing.
     */
    public static final Marker ENTRY_MARKER = MarkerFactory.getMarker("ENTER");

    /**
     * Marker for method exit tracing.
     */
    public static final Marker EXIT_MARKER = MarkerFactory.getMarker("EXIT");

    /**
     * Marker for exception tracing.
     */
    public static final Marker EXCEPTION_MARKER = MarkerFactory.getMarker("EXCEPTION");

    /**
     * Marker for throwing exceptions.
     */
    public static final Marker THROWING_MARKER = MarkerFactory.getMarker("THROWING");

    /**
     * Marker for catching exceptions.
     */
    public static final Marker CATCHING_MARKER = MarkerFactory.getMarker("CATCHING");

    static {
        CATCHING_MARKER.add(EXCEPTION_MARKER);
        THROWING_MARKER.add(EXCEPTION_MARKER);
    }

    private record CallerLocation(
            String sourceClassName, String sourceMethodName) {
    }

    private static final String FQCN = LoggerAdapter.class.getName();
    private static final String[] LOGGER_IMPL_CLASS_NAMES = {
            LoggerAdapter.class.getName(),
            Logger.class.getName(),
            DefaultLoggingEventBuilder.class.getName()
    };

    private static java.util.logging.Level julLevel(final org.slf4j.event.Level slf4jLevel) {
        if (slf4jLevel == null) {
            return null;
        }
        return switch (slf4jLevel) {
            case TRACE -> java.util.logging.Level.FINEST;
            case DEBUG -> java.util.logging.Level.FINE;
            case INFO -> java.util.logging.Level.INFO;
            case WARN -> java.util.logging.Level.WARNING;
            case ERROR -> java.util.logging.Level.SEVERE;
        };
    }

    private final transient java.util.logging.Logger logger;

    private final String name;

    private org.slf4j.event.Level level = null;

    public LoggerAdapter(final java.util.logging.Logger logger, final String name, final org.slf4j.event.Level level) {
        this.name = name;
        this.logger = logger;
        try {
            this.level = level;
            logger.setLevel(julLevel(level));
        } catch (final SecurityException e) {
            // Ignore the exception and continue
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    public org.slf4j.event.Level getLevel() {
        return this.level;
    }

    public void setLevel(final org.slf4j.event.Level level) {
        final Level julLevel = logger.getLevel();
        try {
            if (julLevel == null || julLevel.intValue() != julLevel(level).intValue()) {
                logger.setLevel(julLevel(level));
            }
            this.level = level;
        } catch (final SecurityException e) {
            // Ignore the exception and continue
        }
    }

    public String getFullyQualifiedCallerName() {
        return getClass().getName();
    }

    @Override
    public int hashCode() {
        return 31 * (31 + logger.hashCode()) + name.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final LoggerAdapter other = (LoggerAdapter) obj;
        return logger.equals(other.logger) && name.equals(other.name);
    }

    @Override
    public String toString() {
        return "LoggerAdapter [name=" + name + ", level=" + logger.getLevel().getName() + "]";
    }

    /**
     * Sets the parent logger for this logger.
     * If the parent logger is already set, this method does nothing.
     * 
     * @param parent The parent logger to set.
     * @throws NullPointerException if the parent logger is null.
     */
    public Logger setParent(final java.util.logging.Logger parent) {
        if (logger.getParent() == null) {
            logger.setParent(parent);
        }
        return this;
    }

    /**
     * Sets the parent logger for this logger.
     * If the parent logger is already set, this method does nothing.
     * 
     * @param parent The parent logger to set.
     * @throws NullPointerException if the parent logger is null.
     */
    public Logger setParent(final Logger parent) {
        if (parent != null && logger.getParent() == null && parent instanceof final LoggerAdapter adapter) {
            logger.setParent(adapter.logger);
        }
        return this;
    }

    public boolean isEnabled(final org.slf4j.event.Level level) {
        return logger.isLoggable(julLevel(level));
    }

    public boolean isEnabled(final org.slf4j.event.Level level, final Marker marker) {
        return logger.isLoggable(julLevel(level));
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return logger.isLoggable(Level.FINEST);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return logger.isLoggable(Level.SEVERE);
    }

    public boolean isFatalEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    public boolean isFatalEnabled(final Marker marker) {
        return logger.isLoggable(Level.SEVERE);
    }

    public <T extends Throwable> T throwing(final org.slf4j.event.Level level, final T throwable) {
        handleLogging(julLevel(level), THROWING_MARKER, "throwing", null, throwable);
        return throwable;
    }

    public <T extends Throwable> T throwing(final T throwable) {
        handleLogging(Level.SEVERE, THROWING_MARKER, "throwing", null, throwable);
        return throwable;
    }

    public void catching(final org.slf4j.event.Level level, final Throwable throwable) {
        handleLogging(julLevel(level), CATCHING_MARKER, "catching", null, throwable);
    }

    public void catching(final Throwable throwable) {
        handleLogging(Level.SEVERE, CATCHING_MARKER, "catching", null, throwable);
    }

    @Override
    public void trace(final String msg) {
        if (isTraceEnabled()) {
            handleLogging(Level.FINEST, null, msg, null, null);
        }
    }

    @Override
    public void trace(final String msg, final Object arg) {
        if (isTraceEnabled()) {
            handleArgArray(Level.FINEST, null, msg, arg);
        }
    }

    @Override
    public void trace(final String msg, final Object arg1, final Object arg2) {
        if (isTraceEnabled()) {
            handleArgArray(Level.FINEST, null, msg, arg1, arg2);
        }
    }

    @Override
    public void trace(final String msg, final Object... arguments) {
        if (isTraceEnabled()) {
            handleArgArray(Level.FINEST, null, msg, arguments);
        }
    }

    @Override
    public void trace(final String msg, final Throwable t) {
        if (isTraceEnabled()) {
            handleArgArray(Level.FINEST, null, msg, t);
        }
    }

    public void trace(final String msg, final Supplier<?>... paramSuppliers) {
        if (isTraceEnabled()) {
            handleArgArray(Level.FINEST, null, msg, paramSuppliers);
        }
    }

    public void trace(final Supplier<?> msgSupplier) {
        if (isTraceEnabled()) {
            handleLogging(Level.FINEST, null, String.valueOf(msgSupplier.get()), null, null);
        }
    }

    @Override
    public void trace(final Marker marker, final String msg) {
        if (isTraceEnabled(marker)) {
            handleLogging(Level.FINEST, marker, msg, null, null);
        }
    }

    @Override
    public void trace(final Marker marker, final String msg, final Object arg) {
        if (isTraceEnabled(marker)) {
            handleArgArray(Level.FINEST, marker, msg, arg);
        }
    }

    @Override
    public void trace(final Marker marker, final String msg, final Object arg1, final Object arg2) {
        if (isTraceEnabled(marker)) {
            handleArgArray(Level.FINEST, marker, msg, arg1, arg2);
        }
    }

    @Override
    public void trace(final Marker marker, final String msg, final Object... argArray) {
        if (isTraceEnabled(marker)) {
            handleArgArray(Level.FINEST, marker, msg, argArray);
        }
    }

    public void trace(final Marker marker, final String msg, final Throwable t) {
        if (isTraceEnabled(marker)) {
            handleArgArray(Level.FINEST, marker, msg, t);
        }
    }

    public void trace(final Object message) {
        if (isTraceEnabled()) {
            handleLogging(Level.FINER, null, String.valueOf(message), null, null);
        }
    }

    public void trace(final Object message, final Throwable throwable) {
        if (isTraceEnabled()) {
            handleLogging(Level.FINER, null, String.valueOf(message), null, throwable);
        }
    }

    public void trace(final String message, final Object p0, final Object p1, final Object p2) {
        if (isTraceEnabled()) {
            handleArgArray(Level.FINER, null, message, p0, p1, p2);
        }
    }

    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (isTraceEnabled()) {
            handleArgArray(Level.FINER, null, message, p0, p1, p2, p3);
        }
    }

    public void trace(final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isTraceEnabled()) {
            handleLogging(Level.FINER, null, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void trace(final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        if (isTraceEnabled()) {
            handleArgArray(Level.FINER, null, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void trace(final Marker marker, final Object message) {
        if (isTraceEnabled(marker)) {
            handleLogging(Level.FINER, marker, String.valueOf(message), null, null);
        }
    }

    public void trace(final Marker marker, final Object message, final Throwable throwable) {
        if (isTraceEnabled(marker)) {
            handleLogging(Level.FINER, marker, String.valueOf(message), null, throwable);
        }
    }

    public void trace(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        if (isTraceEnabled(marker)) {
            handleArgArray(Level.FINER, marker, message, paramSuppliers);
        }
    }

    public void trace(final Marker marker, final Supplier<?> messageSupplier) {
        if (isTraceEnabled(marker)) {
            handleLogging(Level.FINER, marker, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void trace(final Marker marker, final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        if (isTraceEnabled(marker)) {
            handleArgArray(Level.FINER, marker, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void trace(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isTraceEnabled(marker)) {
            handleLogging(Level.FINER, marker, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        if (isTraceEnabled(marker)) {
            handleArgArray(Level.FINER, marker, message, p0, p1, p2);
        }
    }

    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        if (isTraceEnabled(marker)) {
            handleArgArray(Level.FINER, marker, message, p0, p1, p2, p3);
        }
    }

    public void debug(final String msg) {
        if (isDebugEnabled()) {
            handleLogging(Level.FINE, null, msg, null, null);
        }
    }

    public void debug(final String msg, final Object arg) {
        if (isDebugEnabled()) {
            handleArgArray(Level.FINE, null, msg, arg);
        }
    }

    public void debug(final String msg, final Object arg1, final Object arg2) {
        if (isDebugEnabled()) {
            handleArgArray(Level.FINE, null, msg, arg1, arg2);
        }
    }

    public void debug(final String msg, final Object... arguments) {
        if (isDebugEnabled()) {
            handleArgArray(Level.FINE, null, msg, arguments);
        }
    }

    public void debug(final String msg, final Throwable t) {
        if (isDebugEnabled()) {
            handleArgArray(Level.FINE, null, msg, t);
        }
    }

    public void debug(final Marker marker, final String msg) {
        if (isDebugEnabled(marker)) {
            handleLogging(Level.FINE, marker, msg, null, null);
        }
    }

    public void debug(final Marker marker, final String msg, final Object arg) {
        if (isDebugEnabled(marker)) {
            handleArgArray(Level.FINE, marker, msg, arg);
        }
    }

    public void debug(final Marker marker, final String msg, final Object arg1, final Object arg2) {
        if (isDebugEnabled(marker)) {
            handleArgArray(Level.FINE, marker, msg, arg1, arg2);
        }
    }

    public void debug(final Marker marker, final String msg, final Object... arguments) {
        if (isDebugEnabled(marker)) {
            handleArgArray(Level.FINE, marker, msg, arguments);
        }
    }

    public void debug(final Marker marker, final String msg, final Throwable t) {
        if (isDebugEnabled(marker)) {
            handleArgArray(Level.FINE, marker, msg, t);
        }
    }

    public void debug(final Marker marker, final Object message) {
        if (isDebugEnabled(marker)) {
            handleLogging(Level.FINE, marker, String.valueOf(message), null, null);
        }
    }

    public void debug(final Marker marker, final Object message, final Throwable throwable) {
        if (isDebugEnabled(marker)) {
            handleLogging(Level.FINE, marker, String.valueOf(message), null, throwable);
        }
    }

    public void debug(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        if (isDebugEnabled(marker)) {
            handleArgArray(Level.FINE, marker, message, paramSuppliers);
        }
    }

    public void debug(final Marker marker, final Supplier<?> messageSupplier) {
        if (isDebugEnabled(marker)) {
            handleLogging(Level.FINE, marker, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void debug(final Marker marker, final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        if (isDebugEnabled(marker)) {
            handleArgArray(Level.FINE, marker, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void debug(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isDebugEnabled(marker)) {
            handleLogging(Level.FINE, marker, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void debug(final Object message) {
        if (isDebugEnabled()) {
            handleLogging(Level.FINE, null, String.valueOf(message), null, null);
        }
    }

    public void debug(final Object message, final Throwable throwable) {
        if (isDebugEnabled()) {
            handleLogging(Level.FINE, null, String.valueOf(message), null, throwable);
        }
    }

    public void debug(final String message, final Supplier<?>... paramSuppliers) {
        if (isDebugEnabled()) {
            handleArgArray(Level.FINE, null, message, paramSuppliers);
        }
    }

    public void debug(final Supplier<?> messageSupplier) {
        if (isDebugEnabled()) {
            handleLogging(Level.FINE, null, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void debug(final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        if (isDebugEnabled()) {
            handleArgArray(Level.FINE, null, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void debug(final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isDebugEnabled()) {
            handleLogging(Level.FINE, null, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        if (isDebugEnabled(marker)) {
            handleArgArray(Level.FINE, marker, message, p0, p1, p2);
        }
    }

    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        if (isDebugEnabled(marker)) {
            handleArgArray(Level.FINE, marker, message, p0, p1, p2, p3);
        }
    }

    public void debug(final String message, final Object p0, final Object p1, final Object p2) {
        if (isDebugEnabled()) {
            handleArgArray(Level.FINE, null, message, p0, p1, p2);
        }
    }

    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (isDebugEnabled()) {
            handleArgArray(Level.FINE, null, message, p0, p1, p2, p3);
        }
    }

    public void info(final String msg) {
        if (isInfoEnabled()) {
            handleLogging(Level.INFO, null, msg, null, null);
        }
    }

    public void info(final String msg, final Object arg) {
        if (isInfoEnabled()) {
            handleArgArray(Level.INFO, null, msg, arg);
        }
    }

    public void info(final String msg, final Object arg1, final Object arg2) {
        if (isInfoEnabled()) {
            handleArgArray(Level.INFO, null, msg, arg1, arg2);
        }
    }

    public void info(final String msg, final Object... arguments) {
        if (isInfoEnabled()) {
            handleArgArray(Level.INFO, null, msg, arguments);
        }
    }

    public void info(final String msg, final Throwable t) {
        if (isInfoEnabled()) {
            handleArgArray(Level.INFO, null, msg, t);
        }
    }

    public void info(final Marker marker, final String msg) {
        if (isInfoEnabled(marker)) {
            handleLogging(Level.INFO, marker, msg, null, null);
        }
    }

    public void info(final Marker marker, final String msg, final Object arg) {
        if (isInfoEnabled(marker)) {
            handleArgArray(Level.INFO, marker, msg, arg);
        }
    }

    public void info(final Marker marker, final String msg, final Object arg1, final Object arg2) {
        if (isInfoEnabled(marker)) {
            handleArgArray(Level.INFO, marker, msg, arg1, arg2);
        }
    }

    public void info(final Marker marker, final String msg, final Object... arguments) {
        if (isInfoEnabled(marker)) {
            handleArgArray(Level.INFO, marker, msg, arguments);
        }
    }

    public void info(final Marker marker, final String msg, final Throwable t) {
        if (isInfoEnabled(marker)) {
            handleArgArray(Level.INFO, marker, msg, t);
        }
    }

    public void info(final Marker marker, final Object message) {
        if (isInfoEnabled(marker)) {
            handleLogging(Level.INFO, marker, String.valueOf(message), null, null);
        }
    }

    public void info(final Marker marker, final Object message, final Throwable throwable) {
        if (isInfoEnabled(marker)) {
            handleLogging(Level.INFO, marker, String.valueOf(message), null, throwable);
        }
    }

    public void info(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        if (isInfoEnabled(marker)) {
            handleArgArray(Level.INFO, marker, message, paramSuppliers);
        }
    }

    public void info(final Marker marker, final Supplier<?> messageSupplier) {
        if (isInfoEnabled(marker)) {
            handleLogging(Level.INFO, marker, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void info(final Marker marker, final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        if (isInfoEnabled(marker)) {
            handleArgArray(Level.INFO, marker, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void info(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isInfoEnabled(marker)) {
            handleLogging(Level.INFO, marker, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void info(final Object message) {
        if (isInfoEnabled()) {
            handleLogging(Level.INFO, null, String.valueOf(message), null, null);
        }
    }

    public void info(final Object message, final Throwable throwable) {
        if (isInfoEnabled()) {
            handleLogging(Level.INFO, null, String.valueOf(message), null, throwable);
        }
    }

    public void info(final String message, final Supplier<?>... paramSuppliers) {
        if (isInfoEnabled()) {
            handleArgArray(Level.INFO, null, message, paramSuppliers);
        }
    }

    public void info(final Supplier<?> messageSupplier) {
        if (isInfoEnabled()) {
            handleLogging(Level.INFO, null, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void info(final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        if (isInfoEnabled()) {
            handleArgArray(Level.INFO, null, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void info(final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isInfoEnabled()) {
            handleLogging(Level.INFO, null, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        if (isInfoEnabled(marker)) {
            handleArgArray(Level.INFO, marker, message, p0, p1, p2);
        }
    }

    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        if (isInfoEnabled(marker)) {
            handleArgArray(Level.INFO, marker, message, p0, p1, p2, p3);
        }
    }

    public void info(final String message, final Object p0, final Object p1, final Object p2) {
        if (isInfoEnabled()) {
            handleArgArray(Level.INFO, null, message, p0, p1, p2);
        }
    }

    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (isInfoEnabled()) {
            handleArgArray(Level.INFO, null, message, p0, p1, p2, p3);
        }
    }

    public void warn(final String msg) {
        if (isWarnEnabled()) {
            handleLogging(Level.WARNING, null, msg, null, null);
        }
    }

    public void warn(final String msg, final Object arg) {
        if (isWarnEnabled()) {
            handleArgArray(Level.WARNING, null, msg, arg);
        }
    }

    public void warn(final String msg, final Object arg1, final Object arg2) {
        if (isWarnEnabled()) {
            handleArgArray(Level.WARNING, null, msg, arg1, arg2);
        }
    }

    public void warn(final String msg, final Object... arguments) {
        if (isWarnEnabled()) {
            handleArgArray(Level.WARNING, null, msg, arguments);
        }
    }

    public void warn(final String msg, final Throwable t) {
        if (isWarnEnabled()) {
            handleArgArray(Level.WARNING, null, msg, t);
        }
    }

    public void warn(final Marker marker, final String msg) {
        if (isWarnEnabled(marker)) {
            handleLogging(Level.WARNING, marker, msg, null, null);
        }
    }

    public void warn(final Marker marker, final String msg, final Object arg) {
        if (isWarnEnabled(marker)) {
            handleArgArray(Level.WARNING, marker, msg, arg);
        }
    }

    public void warn(final Marker marker, final String msg, final Object arg1, final Object arg2) {
        if (isWarnEnabled(marker)) {
            handleArgArray(Level.WARNING, marker, msg, arg1, arg2);
        }
    }

    public void warn(final Marker marker, final String msg, final Object... arguments) {
        if (isWarnEnabled(marker)) {
            handleArgArray(Level.WARNING, marker, msg, arguments);
        }
    }

    public void warn(final Marker marker, final String msg, final Throwable t) {
        if (isWarnEnabled(marker)) {
            handleArgArray(Level.WARNING, marker, msg, t);
        }
    }

    public void warn(final Marker marker, final Object message) {
        if (isWarnEnabled(marker)) {
            handleLogging(Level.WARNING, marker, String.valueOf(message), null, null);
        }
    }

    public void warn(final Marker marker, final Object message, final Throwable throwable) {
        if (isWarnEnabled(marker)) {
            handleLogging(Level.WARNING, marker, String.valueOf(message), null, throwable);
        }
    }

    public void warn(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        if (isWarnEnabled(marker)) {
            handleArgArray(Level.WARNING, marker, message, paramSuppliers);
        }
    }

    public void warn(final Marker marker, final Supplier<?> messageSupplier) {
        if (isWarnEnabled(marker)) {
            handleLogging(Level.WARNING, marker, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void warn(final Marker marker, final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        if (isWarnEnabled(marker)) {
            handleArgArray(Level.WARNING, marker, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void warn(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isWarnEnabled(marker)) {
            handleLogging(Level.WARNING, marker, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void warn(final Object message) {
        if (isWarnEnabled()) {
            handleLogging(Level.WARNING, null, String.valueOf(message), null, null);
        }
    }

    public void warn(final Object message, final Throwable throwable) {
        if (isWarnEnabled()) {
            handleLogging(Level.WARNING, null, String.valueOf(message), null, throwable);
        }
    }

    public void warn(final String message, final Supplier<?>... paramSuppliers) {
        if (isWarnEnabled()) {
            handleArgArray(Level.WARNING, null, message, paramSuppliers);
        }
    }

    public void warn(final Supplier<?> messageSupplier) {
        if (isWarnEnabled()) {
            handleLogging(Level.WARNING, null, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void warn(final Supplier<?> messageSupplie, final Supplier<?>... paramSuppliers) {
        if (isWarnEnabled()) {
            handleArgArray(Level.WARNING, null, String.valueOf(messageSupplie.get()), paramSuppliers);
        }
    }

    public void warn(final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isWarnEnabled()) {
            handleLogging(Level.WARNING, null, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        if (isWarnEnabled(marker)) {
            handleArgArray(Level.WARNING, marker, message, p0, p1, p2);
        }
    }

    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        if (isWarnEnabled(marker)) {
            handleArgArray(Level.WARNING, marker, message, p0, p1, p2, p3);
        }
    }

    public void warn(final String message, final Object p0, final Object p1, final Object p2) {
        if (isWarnEnabled()) {
            handleArgArray(Level.WARNING, null, message, p0, p1, p2);
        }
    }

    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (isWarnEnabled()) {
            handleArgArray(Level.WARNING, null, message, p0, p1, p2, p3);
        }
    }

    public void error(final String msg) {
        if (isErrorEnabled()) {
            handleLogging(Level.SEVERE, null, msg, null, null);
        }
    }

    public void error(final String msg, final Object arg) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, msg, arg);
        }
    }

    public void error(final String msg, final Object arg1, final Object arg2) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, msg, arg1, arg2);
        }
    }

    public void error(final String msg, final Object... arguments) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, msg, arguments);
        }
    }

    public void error(final String msg, final Throwable t) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, msg, t);
        }
    }

    public void error(final Marker marker, final String msg) {
        if (isErrorEnabled(marker)) {
            handleLogging(Level.SEVERE, marker, msg, null, null);
        }
    }

    public void error(final Marker marker, final String msg, final Object arg) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, msg, arg);
        }
    }

    public void error(final Marker marker, final String msg, final Object arg1, final Object arg2) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, msg, arg1, arg2);
        }
    }

    public void error(final Marker marker, final String msg, final Object... arguments) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, msg, arguments);
        }
    }

    public void error(final Marker marker, final String msg, final Throwable t) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, msg, t);
        }
    }

    public void error(final Marker marker, final Object message) {
        if (isErrorEnabled(marker)) {
            handleLogging(Level.SEVERE, marker, String.valueOf(message), null, null);
        }
    }

    public void error(final Marker marker, final Object message, final Throwable throwable) {
        if (isErrorEnabled(marker)) {
            handleLogging(Level.SEVERE, marker, String.valueOf(message), null, throwable);
        }
    }

    public void error(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, message, paramSuppliers);
        }
    }

    public void error(final Marker marker, final Supplier<?> messageSupplier) {
        if (isErrorEnabled(marker)) {
            handleLogging(Level.SEVERE, marker, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void error(final Marker marker, final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void error(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isErrorEnabled(marker)) {
            handleLogging(Level.SEVERE, marker, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void error(final Object message) {
        if (isErrorEnabled()) {
            handleLogging(Level.SEVERE, null, String.valueOf(message), null, null);
        }
    }

    public void error(final Object message, final Throwable throwable) {
        if (isErrorEnabled()) {
            handleLogging(Level.SEVERE, null, String.valueOf(message), null, throwable);
        }
    }

    public void error(final String message, final Supplier<?>... paramSuppliers) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, message, paramSuppliers);
        }
    }

    public void error(final Supplier<?> messageSupplier) {
        if (isErrorEnabled()) {
            handleLogging(Level.SEVERE, null, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void error(final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void error(final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isErrorEnabled()) {
            handleLogging(Level.SEVERE, null, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, message, p0, p1, p2);
        }
    }

    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, message, p0, p1, p2, p3);
        }
    }

    public void error(final String message, final Object p0, final Object p1, final Object p2) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, message, p0, p1, p2);
        }
    }

    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, message, p0, p1, p2, p3);
        }
    }

    public void fatal(final Marker marker, final Object message) {
        if (isErrorEnabled(marker)) {
            handleLogging(Level.SEVERE, marker, String.valueOf(message), null, null);
        }
    }

    public void fatal(final Marker marker, final Object message, final Throwable throwable) {
        if (isErrorEnabled(marker)) {
            handleLogging(Level.SEVERE, marker, String.valueOf(message), null, throwable);
        }
    }

    public void fatal(final Marker marker, final String message) {
        if (isErrorEnabled(marker)) {
            handleLogging(Level.SEVERE, marker, message, null, null);
        }
    }

    public void fatal(final Marker marker, final String message, final Object... params) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, message, params);
        }
    }

    public void fatal(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, message, paramSuppliers);
        }
    }

    public void fatal(final Marker marker, final String message, final Throwable throwable) {
        if (isErrorEnabled(marker)) {
            handleLogging(Level.SEVERE, marker, message, null, throwable);
        }
    }

    public void fatal(final Marker marker, final Supplier<?> messageSupplier) {
        if (isErrorEnabled(marker)) {
            handleLogging(Level.SEVERE, marker, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void fatal(final Marker marker, final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void fatal(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isErrorEnabled(marker)) {
            handleLogging(Level.SEVERE, marker, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void fatal(final Object message) {
        if (isErrorEnabled()) {
            handleLogging(Level.SEVERE, null, String.valueOf(message), null, null);
        }
    }

    public void fatal(final Object message, final Throwable throwable) {
        if (isErrorEnabled()) {
            handleLogging(Level.SEVERE, null, String.valueOf(message), null, throwable);
        }
    }

    public void fatal(final String message) {
        if (isErrorEnabled()) {
            handleLogging(Level.SEVERE, null, message, null, null);
        }
    }

    public void fatal(final String message, final Object... params) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, message, params);
        }
    }

    public void fatal(final String message, final Supplier<?>... paramSuppliers) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, message, paramSuppliers);
        }
    }

    public void fatal(final String message, final Throwable throwable) {
        if (isErrorEnabled()) {
            handleLogging(Level.SEVERE, null, message, null, throwable);
        }
    }

    public void fatal(final Supplier<?> messageSupplier) {
        if (isErrorEnabled()) {
            handleLogging(Level.SEVERE, null, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void fatal(final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void fatal(final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isErrorEnabled()) {
            handleLogging(Level.SEVERE, null, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void fatal(final Marker marker, final String message, final Object p0) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, message, p0);
        }
    }

    public void fatal(final Marker marker, final String message, final Object p0, final Object p1) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, message, p0, p1);
        }
    }

    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, message, p0, p1, p2);
        }
    }

    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        if (isErrorEnabled(marker)) {
            handleArgArray(Level.SEVERE, marker, message, p0, p1, p2, p3);
        }
    }

    public void fatal(final String message, final Object p0) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, message, p0);
        }
    }

    public void fatal(final String message, final Object p0, final Object p1) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, message, p0, p1);
        }
    }

    public void fatal(final String message, final Object p0, final Object p1, final Object p2) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, message, p0, p1, p2);
        }
    }

    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (isErrorEnabled()) {
            handleArgArray(Level.SEVERE, null, message, p0, p1, p2, p3);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final Object message) {
        if (isEnabled(level, marker)) {
            handleLogging(level, marker, String.valueOf(message), null, null);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final Object message,
            final Throwable throwable) {
        if (isEnabled(level, marker)) {
            handleLogging(level, marker, String.valueOf(message), null, throwable);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final String message) {
        if (isEnabled(level, marker)) {
            handleLogging(level, marker, message, null, null);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final String message,
            final Object... params) {
        if (isEnabled(level, marker)) {
            handleArgArray(julLevel(level), marker, message, params);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final String message,
            final Supplier<?>... paramSuppliers) {
        if (isEnabled(level, marker)) {
            handleArgSupplier(julLevel(level), marker, message, paramSuppliers);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final String message,
            final Throwable throwable) {
        if (isEnabled(level, marker)) {
            handleLogging(level, marker, message, null, throwable);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final Supplier<?> messageSupplier) {
        if (isEnabled(level, marker)) {
            handleLogging(level, marker, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void log(final Marker marker, final Supplier<?> messageSupplier, final Supplier<?>... paramSuppliers) {
        // ponytail: TempLogger omits Level; uses adapter level or INFO
        final org.slf4j.event.Level effective = this.level != null ? this.level : org.slf4j.event.Level.INFO;
        if (isEnabled(effective, marker)) {
            handleArgSupplier(julLevel(effective), marker, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final Supplier<?> messageSupplier,
            final Throwable throwable) {
        if (isEnabled(level, marker)) {
            handleLogging(level, marker, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void log(final org.slf4j.event.Level level, final Object message) {
        if (isEnabled(level)) {
            handleLogging(level, null, String.valueOf(message), null, null);
        }
    }

    public void log(final org.slf4j.event.Level level, final Object message, final Throwable throwable) {
        if (isEnabled(level)) {
            handleLogging(level, null, String.valueOf(message), null, throwable);
        }
    }

    public void log(final org.slf4j.event.Level level, final String message) {
        if (isEnabled(level)) {
            handleLogging(level, null, message, null, null);
        }
    }

    public void log(final org.slf4j.event.Level level, final String message, final Object... params) {
        if (isEnabled(level)) {
            handleArgArray(julLevel(level), null, message, params);
        }
    }

    public void log(final org.slf4j.event.Level level, final String message, final Supplier<?>... paramSuppliers) {
        if (isEnabled(level)) {
            handleArgSupplier(julLevel(level), null, message, paramSuppliers);
        }
    }

    public void log(final org.slf4j.event.Level level, final String message, final Throwable throwable) {
        if (isEnabled(level)) {
            handleLogging(level, null, message, null, throwable);
        }
    }

    public void log(final org.slf4j.event.Level level, final Supplier<?> messageSupplier) {
        if (isEnabled(level)) {
            handleLogging(level, null, String.valueOf(messageSupplier.get()), null, null);
        }
    }

    public void log(final org.slf4j.event.Level level, final Supplier<?> messageSupplier,
            final Supplier<?>... paramSuppliers) {
        if (isEnabled(level)) {
            handleArgSupplier(julLevel(level), null, String.valueOf(messageSupplier.get()), paramSuppliers);
        }
    }

    public void log(final org.slf4j.event.Level level, final Supplier<?> messageSupplier, final Throwable throwable) {
        if (isEnabled(level)) {
            handleLogging(level, null, String.valueOf(messageSupplier.get()), null, throwable);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final String message, final Object p0) {
        if (isEnabled(level, marker)) {
            handleArgArray(julLevel(level), marker, message, p0);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final String message, final Object p0,
            final Object p1) {
        if (isEnabled(level, marker)) {
            handleArgArray(julLevel(level), marker, message, p0, p1);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2) {
        if (isEnabled(level, marker)) {
            handleArgArray(julLevel(level), marker, message, p0, p1, p2);
        }
    }

    public void log(final org.slf4j.event.Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2,
            final Object p3) {
        if (isEnabled(level, marker)) {
            handleArgArray(julLevel(level), marker, message, p0, p1, p2, p3);
        }
    }

    public void log(final org.slf4j.event.Level level, final String message, final Object p0) {
        if (isEnabled(level)) {
            handleArgArray(julLevel(level), null, message, p0);
        }
    }

    public void log(final org.slf4j.event.Level level, final String message, final Object p0, final Object p1) {
        if (isEnabled(level)) {
            handleArgArray(julLevel(level), null, message, p0, p1);
        }
    }

    public void log(final org.slf4j.event.Level level, final String message, final Object p0, final Object p1,
            final Object p2) {
        if (isEnabled(level)) {
            handleArgArray(julLevel(level), null, message, p0, p1, p2);
        }
    }

    public void log(final org.slf4j.event.Level level, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3) {
        if (isEnabled(level)) {
            handleArgArray(julLevel(level), null, message, p0, p1, p2, p3);
        }
    }

    public EntryMessage traceEntry() {
        if (isTraceEnabled(ENTRY_MARKER)) {
            return EntryMessage.of().log(this);
        }
        return null;
    }

    public EntryMessage traceEntry(final String format, final Object... params) {
        if (isTraceEnabled(ENTRY_MARKER)) {
            return EntryMessage.of(format, params).log(this);
        }
        return null;
    }

    public EntryMessage traceEntry(final Supplier<?>... paramSuppliers) {
        if (isTraceEnabled(ENTRY_MARKER)) {
            return EntryMessage.of(null, resolveArgSupplier(paramSuppliers)).log(this);
        }
        return null;
    }

    public EntryMessage traceEntry(final String format, final Supplier<?>... paramSuppliers) {
        if (isTraceEnabled(ENTRY_MARKER)) {
            return EntryMessage.of(format, resolveArgSupplier(paramSuppliers)).log(this);
        }
        return null;
    }

    public void traceExit() {
        if (isTraceEnabled(ENTRY_MARKER)) {
            handleExit((String) null, (Object) null);
        }
    }

    public <R> R traceExit(final R result) {
        if (isTraceEnabled(ENTRY_MARKER)) {
            handleExit((String) null, (Object) result);
        }
        return result;
    }

    public <R> R traceExit(final String format, final R result) {
        if (isTraceEnabled(ENTRY_MARKER)) {
            handleExit(format, result);
        }
        return result;
    }

    public <R> R traceExit(final EntryMessage message, final R result) {
        if (isTraceEnabled(ENTRY_MARKER)) {
            handleExit(message, result);
        }
        return result;
    }

    protected void handleLogging(
            final Level level,
            final Marker marker,
            final String msg,
            final Object[] args,
            final Throwable throwable) {

        final LogRecord julLogRecord = new LogRecord(level, MessageFormatter.basicArrayFormat(msg, args));
        julLogRecord.setLoggerName(name);
        julLogRecord.setThrown(throwable);

        final CallerLocation callerLocation = inferCallerLocation();
        if (callerLocation != null) {
            julLogRecord.setSourceClassName(callerLocation.sourceClassName());
            julLogRecord.setSourceMethodName(callerLocation.sourceMethodName());
        }

        logger.log(julLogRecord);
    }

    protected void handleLogging(
            final org.slf4j.event.Level level,
            final Marker marker,
            final String msg,
            final Object[] args,
            final Throwable throwable) {

        final LogRecord julLogRecord = new LogRecord(julLevel(level),
                MessageFormatter.basicArrayFormat(msg, args));
        julLogRecord.setLoggerName(name);
        julLogRecord.setThrown(throwable);

        final CallerLocation callerLocation = inferCallerLocation();
        if (callerLocation != null) {
            julLogRecord.setSourceClassName(callerLocation.sourceClassName());
            julLogRecord.setSourceMethodName(callerLocation.sourceMethodName());
        }

        logger.log(julLogRecord);
    }

    private void handleExit(String format, final Object result) {
        if (format == null || format.isEmpty()) {
            format = result == null ? "Exit" : "Exit with({})";
        }
        handleLogging(Level.FINE, EXIT_MARKER, format, new Object[] { result }, null);
    }

    private void handleExit(final EntryMessage message, final Object result) {
        handleLogging(Level.FINE, EXIT_MARKER, "Exit " + message.message() + ": " + String.valueOf(result),
                new Object[] { result }, null);
    }

    private void handleArgArray(final Level level, final Marker marker, final String msg,
            final Object arg1) {
        if (arg1 instanceof final Throwable throwable) {
            handleLogging(level, marker, msg, null, throwable);
        } else {
            handleLogging(level, marker, msg, new Object[] { arg1 }, null);
        }
    }

    private void handleArgArray(final Level level, final Marker marker, final String msg,
            final Object arg1, final Object arg2) {
        if (arg2 instanceof final Throwable throwable) {
            handleLogging(level, marker, msg, new Object[] { arg1 }, throwable);
        } else {
            handleLogging(level, marker, msg, new Object[] { arg1, arg2 }, null);
        }
    }

    private void handleArgArray(final Level level, final Marker marker, final String msg,
            final Object arg1, final Object arg2, final Object arg3) {
        if (arg3 instanceof final Throwable throwable) {
            handleLogging(level, marker, msg, new Object[] { arg1, arg2 }, throwable);
        } else {
            handleLogging(level, marker, msg, new Object[] { arg1, arg2, arg3 }, null);
        }
    }

    private void handleArgArray(final Level level, final Marker marker, final String msg,
            final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        if (arg4 instanceof final Throwable throwable) {
            handleLogging(level, marker, msg, new Object[] { arg1, arg2, arg3 }, throwable);
        } else {
            handleLogging(level, marker, msg, new Object[] { arg1, arg2, arg3, arg4 }, null);
        }
    }

    private void handleArgSupplier(final Level level, final Marker marker, final String msg,
            final Supplier<?>... suppliers) {
        if (suppliers == null || suppliers.length == 0) {
            handleLogging(level, marker, msg, null, null);
            return;
        } else {
            try {
                final Object[] args = resolveArgSupplier(suppliers);
                handleArgArray(level, marker, msg, args);
            } catch (final Exception e) {
                handleLogging(level, marker, msg, null, e);
            }
            final Object[] args = new Object[suppliers.length];
            for (int i = 0; i < suppliers.length; i++) {
                args[i] = suppliers[i].get();
            }
            handleArgArray(level, marker, msg, args);
        }

    }

    private Object[] resolveArgSupplier(final Supplier<?>... suppliers) {
        if (suppliers == null || suppliers.length == 0) {
            return new Object[0];
        } else {
            final Object[] newArgs = new Object[suppliers.length];
            for (int i = 0; i < suppliers.length; i++) {
                newArgs[i] = suppliers[i].get();
            }
            return newArgs;
        }
    }

    private void handleArgArray(final Level level, final Marker marker, final String msg,
            final Object[] args) {
        if (args == null) {
            handleLogging(level, marker, msg, null, null);
            return;
        }
        final int len = args.length - 1;
        if (len == -1) {
            handleLogging(level, marker, msg, null, null);
            return;
        }
        final Object lastEntry = args[len];
        if (lastEntry != null && lastEntry instanceof final Throwable throwable) {
            final Object[] trimmed = new Object[len];
            if (len > 0) {
                System.arraycopy(args, 0, trimmed, 0, len);
            }
            handleLogging(level, marker, msg, trimmed, throwable);
        } else {
            handleLogging(level, marker, msg, args, null);
        }
    }

    /**
     * Tries to infer the caller location and eventually return a
     * {@link CallerLocation} object.
     *
     * <p>
     * This implementation relies on the execution stack trace when attempting to
     * retrieve the
     * class name and method name of the caller. One "hacky" way found to retrieve
     * the execution stack
     * trace is by generating an exception stack trace through
     * {@link Throwable#getStackTrace()}. Once
     * retrieved, we simply iterate over the stack trace elements until finding the
     * caller frame.
     *
     * <p>
     * A better approach for the implementation would be to rely on the
     * {@link StackWalker} class
     * instead since that's the official and supported way for answering our
     * specific need here. If we
     * want to improve the implementation, it seems to be the way to go. The JUL
     * implementation has
     * gone into this direction, for example, since a recent version of JDK higher
     * than 8 (see the
     * {@link LogRecord}{@code #inferCaller()} method for details). Furthermore, it
     * will make the code
     * more testable (that's not fully the case with the current implementation
     * because of <code>
     * new Throwable()</code> call).
     *
     * @return The inferred caller location if found, null otherwise
     */
    private static CallerLocation inferCallerLocation() {
        // The first element is the top-most call on the execution stack
        final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();

        // First, search for a method in a logger implementation class.
        int firstLoggerImplClassIndex = -1;
        for (int i = 0; i < stackTraceElements.length; i++) {
            final String className = stackTraceElements[i].getClassName();

            if (isLoggerImplClass(className, FQCN)) {
                firstLoggerImplClassIndex = i;
                break;
            }
        }

        // Now search for the first frame called before the logger implementation
        // classes.
        int inferedCallerClassNameIndex = -1;
        for (int i = firstLoggerImplClassIndex + 1; i < stackTraceElements.length; i++) {
            final String className = stackTraceElements[i].getClassName();

            if (!isLoggerImplClass(className, FQCN)) {
                inferedCallerClassNameIndex = i;
                break;
            }
        }

        // We haven't found a suitable frame, so let's just punt. This is acceptable as
        // we are only
        // committed to making a "best effort" here.
        if (inferedCallerClassNameIndex == -1) {
            return null;
        }

        final StackTraceElement stackTraceElement = stackTraceElements[inferedCallerClassNameIndex];
        return new CallerLocation(stackTraceElement.getClassName(), stackTraceElement.getMethodName());
    }

    private static boolean isLoggerImplClass(
            final String className, final String adapterOrSubstituteCallerFqcn) {
        if (className.equals(adapterOrSubstituteCallerFqcn)) {
            return true;
        }

        for (final String loggerImplClassName : LOGGER_IMPL_CLASS_NAMES) {
            if (loggerImplClassName.equals(className)) {
                return true;
            }
        }
        return false;
    }

}