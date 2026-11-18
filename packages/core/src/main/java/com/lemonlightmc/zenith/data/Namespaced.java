package com.lemonlightmc.zenith.data;

/**
 * Something that has a namespace.
 */
public interface Namespaced {
    /**
     * Gets the namespace.
     *
     * @return the namespace
     */
    @KeyPattern.Namespace
    String namespace();
}