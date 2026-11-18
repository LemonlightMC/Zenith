package com.lemonlightmc.zenith.exceptions;

import java.io.Serial;

/**
 * This exception is thrown when an invalid namespace and/or value has been
 * detected while creating a {@link Key}.
 */
public final class InvalidKeyException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -5413304087321449434L;
    private final String keyNamespace;
    private final String keyValue;

    public InvalidKeyException(final String keyNamespace, final String keyValue, final String message) {
        super(message);
        this.keyNamespace = keyNamespace;
        this.keyValue = keyValue;
    }

    /**
     * Gets the invalid key, as a string.
     *
     * @return a key
     */
    public String keyNamespace() {
        return this.keyNamespace;
    }

    /**
     * Gets the invalid key, as a string.
     *
     * @return a key
     */
    public String keyValue() {
        return this.keyValue;
    }
}