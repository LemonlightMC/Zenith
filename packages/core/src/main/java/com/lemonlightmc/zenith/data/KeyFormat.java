package com.lemonlightmc.zenith.data;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.jetbrains.annotations.Contract;

/**
 * A factory to create {@link Key}s using configured defaults.
 *
 * <p>
 * The configuration options include:
 * </p>
 * <dl>
 * <dt>namespace</dt>
 * <dd>The namespace to be used as fallback, when no other namespace is
 * specified</dd>
 * <dt>separator</dt>
 * <dd>The character separating namespace and value</dd>
 * </dl>
 *
 * <p>
 * Example usage of this class:
 * </p>
 *
 * <pre>{@code
 * KeyFormat format = keyFormat("adventure", ':');
 * Key key = format.key("translations");
 * format.asString(key); // -> "adventure:translations"
 * }</pre>
 *
 * @see Key#key(String)
 */
public record KeyFormat(@KeyPattern.Namespace String namespace, char separator) implements Namespaced {
    static final KeyFormat MINECRAFT_INSTANCE = keyFormat(Key.MINECRAFT_NAMESPACE, Key.DEFAULT_SEPARATOR);

    @SuppressWarnings("PatternValidation") // impossible to validate since the character is variable
    static Key parseKey(final String string, final char separator, final String fallbackNamespace) {
        final int index = string.indexOf(separator);
        return Key.key(
                index >= 1 ? string.substring(0, index) : fallbackNamespace,
                index >= 0 ? string.substring(index + 1) : string);
    }

    /**
     * Gets the default minecraft key format instance.
     *
     * <p>
     * This uses {@link Key#MINECRAFT_NAMESPACE} and {@link Key#DEFAULT_SEPARATOR}.
     * </p>
     *
     * @return the instance
     */
    public static KeyFormat minecraft() {
        return MINECRAFT_INSTANCE;
    }

    /**
     * Creates a key format with a fallback namespace from a {@link Namespaced}
     * instance.
     *
     * @param namespaced the namespaced instance to get the namespace from
     * @return the key format
     * @throws IllegalArgumentException if the namespace contains an invalid
     *                                  character
     * @see #keyFormat(String)
     */
    @SuppressWarnings("PatternValidation") // The namespace is tested later.
    public static KeyFormat keyFormat(final Namespaced namespaced) {
        return keyFormat()
                .namespace(namespaced)
                .build();
    }

    /**
     * Creates a key format with a fallback {@code namespace} and the
     * {@link Key#DEFAULT_SEPARATOR}.
     *
     * @param namespace the namespace to be used as fallback in
     *                  {@link #parse(String)}
     * @return the key format
     * @throws IllegalArgumentException if the namespace contains an invalid
     *                                  character
     */
    public static KeyFormat keyFormat(@KeyPattern.Namespace final String namespace) {
        return keyFormat()
                .namespace(namespace)
                .build();
    }

    /**
     * Creates a key format.
     *
     * @param namespaced the namespaced instance to get the namespace from
     * @param separator  the separator to bse used when parsing keys
     * @return the key format
     * @throws IllegalArgumentException if the namespace contains an invalid
     *                                  character
     * @see #keyFormat(String, char)
     */
    @SuppressWarnings("PatternValidation") // The namespace is tested later.
    public static KeyFormat keyFormat(final Namespaced namespaced, final char separator) {
        return keyFormat()
                .namespace(namespaced)
                .separator(separator)
                .build();
    }

    /**
     * Creates a key format.
     *
     * @param namespace the namespace to be used as fallback in
     *                  {@link #parse(String)}
     * @param separator the separator to be used when parsing keys using
     *                  {@link #parse(String)}
     * @return the key format
     * @throws IllegalArgumentException if the namespace contains an invalid
     *                                  character
     */
    public static KeyFormat keyFormat(@KeyPattern.Namespace String namespace, final char separator) {
        return keyFormat()
                .namespace(namespace)
                .separator(separator)
                .build();
    }

    /**
     * Creates a key format builder.
     *
     * @return the builder
     */
    public static KeyFormatBuilder keyFormat() {
        return new KeyFormatBuilder();
    }

    /**
     * Gets the separator of this key format.
     *
     * @return the separator
     */
    @Override
    public char separator() {
        return separator;
    }

    /**
     * Gets the fallback namespace of this key format.
     *
     * @return the namespace
     */
    @KeyPattern.Namespace
    @Override
    public String namespace() {
        return namespace;
    }

    /**
     * Creates a key from this format.
     *
     * <p>
     * This will create a key with {@link #namespace()} and {@code value}.
     * </p>
     *
     * @param value the value of the key
     * @return the key
     * @throws InvalidKeyException if the namespace or value contains an invalid
     *                             character
     */
    public Key key(@KeyPattern.Value final String value) {
        return Key.key(this.namespace, value);
    }

    /**
     * Creates a key from this format.
     *
     * <p>
     * This will parse {@code string} as a key, using {@link #separator()} as a
     * separator between namespace and value.
     * </p>
     *
     * <p>
     * The namespace is optional. If you do not provide one (for example, if you
     * provide just {@code player} or {@code separator + "player"}
     * as the string) then {@link #namespace()} will be used as a namespace and
     * {@code string} will be used as the value,
     * removing the separator if necessary.
     * </p>
     *
     * @param string the string
     * @return the key
     * @throws InvalidKeyException if the namespace or value contains an invalid
     *                             character
     */
    public Key parse(final String string) {
        return parseKey(string, this.separator, this.namespace);
    }

    /**
     * Checks if {@code string} can be parsed into a {@link Key} using this formats
     * {@link #separator()}.
     * If no namespace is specified in {@code string}, the fallback namespace
     * {@link #namespace()} is used.
     *
     * @param string the string
     * @return {@code true} if {@code string} can be parsed into a key using this
     *         format, {@code false} otherwise
     * 
     */
    public boolean parseable(final String string) {
        if (string == null) {
            return false;
        }
        final int index = string.indexOf(this.separator);
        final String namespace = index >= 1 ? string.substring(0, index) : this.namespace;
        final String value = index >= 0 ? string.substring(index + 1) : string;
        return Key.parseableNamespace(namespace) && Key.parseableValue(value);
    }

    /**
     * Turns a {@link Key} into its string representation using the configuration of
     * this format.
     *
     * @param key the key
     * @return the string representation of the key
     */
    public String asString(final Key key) {
        if (key == null) {
            return null;
        }
        return key.namespace() + this.separator + key.value();
    }

    /**
     * Turns a {@link Key} into its string representation in minimal form using the
     * configuration of this format.
     *
     * <p>
     * If the {@link Key#namespace()} of the key is {@link #namespace()}, only the
     * {@link Key#value()} will be returned.
     * </p>
     *
     * @param key the key
     * @return the minimal string representation of the key
     */
    public String asMinimalString(final Key key) {
        if (key == null) {
            return null;
        }
        if (key.namespace().equals(this.namespace)) {
            return key.value();
        }
        return key.namespace() + this.separator + key.value();
    }

    /**
     * A builder for a key format.
     *
     */
    public static final class KeyFormatBuilder {
        private String namespace = null;
        private char separator = Key.DEFAULT_SEPARATOR;

        public static KeyFormatBuilder of() {
            return new KeyFormatBuilder();
        }

        /**
         * Sets the fallback namespace of this builder from a {@link Namespaced}
         * instance.
         *
         * @param namespaced the namespaced instance
         * @return this builder
         * @throws IllegalArgumentException if the namespace contains an invalid
         *                                  character
         * @see #namespace(String)
         * 
         */
        @Contract("_ -> this")
        @SuppressWarnings("PatternValidation") // The namespace is tested later.
        public KeyFormatBuilder namespace(final Namespaced namespaced) {
            if (namespaced == null) {
                this.namespace = null;
                return this;
            }
            return this.namespace(namespaced.namespace());
        }

        /**
         * Sets the fallback namespace of this builder.
         *
         * <p>
         * This namespace is used in {@link #parse(String)} when the input string
         * doesn't specify a namespace.
         * </p>
         *
         * <p>
         * Defaults to {@link Key#MINECRAFT_NAMESPACE}
         * </p>
         *
         * @param namespace the namespace
         * @return this builder
         * @throws IllegalArgumentException if the namespace contains an invalid
         *                                  character
         */
        @Contract("_ -> this")
        public KeyFormatBuilder namespace(@KeyPattern.Namespace final String namespace) {
            if (namespace == null || namespace.isBlank()) {
                this.namespace = null;
                return this;
            }
            Key.checkNamespace(namespace).ifPresent(index -> {
                if (index == -1) {
                    throw new IllegalArgumentException(String.format("'%s' is not a valid namespace", namespace));
                } else {
                    final char character = namespace.charAt(index);
                    throw new IllegalArgumentException(String.format(
                            "Illegal character in namespace '%s' at index %d ('%s', bytes: %s)",
                            namespace,
                            index,
                            character,
                            Arrays.toString(String.valueOf(character).getBytes(StandardCharsets.UTF_8))));
                }
            });
            this.namespace = namespace;
            return this;
        }

        /**
         * Sets the separator of this builder.
         *
         * <p>
         * The separator is used to separate namespace and value in
         * {@link #parse(String)}.
         * </p>
         *
         * <p>
         * Defaults to {@link Key#DEFAULT_SEPARATOR}
         * </p>
         *
         * @param separator the separator
         * @return this builder
         */
        @Contract("_ -> this")
        public KeyFormatBuilder separator(final char separator) {
            this.separator = separator;
            return this;
        }

        /**
         * Builds the key format.
         *
         * @return the key format
         */
        @Contract(value = "-> new", pure = true)
        public KeyFormat build() {
            return new KeyFormat(this.namespace == null ? Key.MINECRAFT_NAMESPACE : namespace, this.separator);
        }
    }
}