package com.lemonlightmc.zenith.data;

import org.bukkit.NamespacedKey;

/**
 * Something that has an associated {@link NamespacedKey}.
 */
public interface KeyedBukkit {
    /**
     * Gets the key.
     *
     * @return the key
     */
    NamespacedKey key();
}
