package com.lemonlightmc.zenith.data;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.OptionalInt;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.IZenithPlugin;
import com.lemonlightmc.zenith.exceptions.InvalidKeyException;

/**
 * An identifying object used to fetch and/or store unique objects.
 *
 * <p>
 * A key consists of:
 * </p>
 * <dl>
 * <dt>namespace</dt>
 * <dd>in most cases this should be your plugin or organization name</dd>
 * <dt>value</dt>
 * <dd>what this key leads to, e.g "translations" or
 * "entity.firework_rocket.blast"</dd>
 * </dl>
 *
 * <p>
 * Valid characters for namespaces are
 * <a href="https://regexr.com/5ibbm">{@code [a-z0-9_.-]}</a>.
 * As of Minecraft 26.1, {@code ..} is not a valid namespace.
 * Although this is a valid namespace in earlier versions, Adventure will throw
 * an exception if you attempt to use it.
 * </p>
 *
 * <p>
 * Valid characters for values are
 * <a href="https://regexr.com/5if3m">{@code [a-z0-9/._-]}</a>.
 * </p>
 *
 * <p>
 * Some examples of possible custom keys:
 * </p>
 * <ul>
 * <li>my_plugin:translations</li>
 * <li>my_plugin:weapon.amazing-weapon_damage-attribute</li>
 * <li>my_organization:music.song_1</li>
 * <li>my_organization:item.magic_button</li>
 * </ul>
 */
public record Key(@KeyPattern.Namespace String namespace, @KeyPattern.Value String value)
        implements Comparable<Key>, Namespaced, Keyed {
    /**
     * The namespace for Minecraft.
     */
    public static final String MINECRAFT_NAMESPACE = "minecraft";
    /**
     * The default namespace and value separator.
     */
    public static final char DEFAULT_SEPARATOR = ':';

    static final Comparator<? super Key> COMPARATOR = Comparator.comparing(Key::value).thenComparing(Key::namespace);

    public Key {
        checkError("namespace", namespace, namespace, value, checkNamespace(namespace), KeyPattern.NAMESPACE_PATTERN);
        checkError("value", value, namespace, value, checkValue(value), KeyPattern.VALUE_PATTERN);
    }

    /**
     * Creates a key.
     *
     * <p>
     * This will parse {@code string} as a key, using {@code :} as a separator
     * between the namespace and the value.
     * </p>
     *
     * <p>
     * The namespace is optional. If you do not provide one (for example, if you
     * provide just {@code player} or {@code :player}
     * as the string) then {@link #MINECRAFT_NAMESPACE} will be used as a namespace
     * and {@code string} will be used as the value,
     * removing the colon if necessary.
     * </p>
     *
     * @param string the string
     * @return the key
     * @throws InvalidKeyException if the namespace or value contains an invalid
     *                             character
     */
    public static Key key(@KeyPattern final String string) {
        return key(string, DEFAULT_SEPARATOR);
    }

    /**
     * Creates a key.
     *
     * <p>
     * This will parse {@code string} as a key, using {@code character} as a
     * separator between the namespace and the value.
     * </p>
     *
     * <p>
     * The namespace is optional. If you do not provide one (for example, if you
     * provide {@code player} or {@code character + "player"}
     * as the string) then {@link #MINECRAFT_NAMESPACE} will be used as a namespace
     * and {@code string} will be used as the value,
     * removing the provided separator character if necessary.
     * </p>
     *
     * @param string    the string
     * @param character the character that separates the namespace from the value
     * @return the key
     * @throws InvalidKeyException if the namespace or value contains an invalid
     *                             character
     */
    @SuppressWarnings("PatternValidation") // impossible to validate since the character is variable
    public static Key key(final String string, final char separator) {
        if (string == null) {
            throw new IllegalArgumentException("String cannot be null");
        }
        final int index = string.indexOf(separator);
        return key(
                index >= 1 ? string.substring(0, index) : MINECRAFT_NAMESPACE,
                index >= 0 ? string.substring(index + 1) : string);
    }

    /**
     * Creates a key.
     *
     * @param namespaced the namespace source
     * @param value      the value
     * @return the key
     * @throws InvalidKeyException if the namespace or value contains an invalid
     *                             character
     */
    @SuppressWarnings("PatternValidation") // The namespace is tested later.
    public static Key key(final Namespaced namespaced, @KeyPattern.Value final String value) {
        if (namespaced == null) {
            return new Key(MINECRAFT_NAMESPACE, value);
        }
        return key(namespaced.namespace(), value);
    }

    /**
     * Creates a key.
     *
     * @param namespaced the namespace source
     * @param value      the value
     * @return the key
     * @throws InvalidKeyException if the namespace or value contains an invalid
     *                             character
     */
    @SuppressWarnings("PatternValidation") // The namespace is tested later.
    public static Key key(final Plugin plugin, @KeyPattern.Value final String value) {
        if (plugin == null) {
            return new Key(MINECRAFT_NAMESPACE, value);
        }
        return key(plugin.getName().toLowerCase(), value);
    }

    /**
     * Creates a key.
     *
     * @param namespaced the namespace source
     * @param value      the value
     * @return the key
     * @throws InvalidKeyException if the namespace or value contains an invalid
     *                             character
     */
    @SuppressWarnings("PatternValidation") // The namespace is tested later.
    public static Key key(final IZenithPlugin plugin, @KeyPattern.Value final String value) {
        if (plugin == null) {
            return new Key(MINECRAFT_NAMESPACE, value);
        }
        return new Key(plugin.namespace(), value);
    }

    /**
     * Creates a key.
     *
     * @param namespace the namespace
     * @param value     the value
     * @return the key
     * @throws InvalidKeyException if the namespace or value contains an invalid
     *                             character
     */
    public static Key key(@KeyPattern.Namespace final String namespace, @KeyPattern.Value final String value) {
        return new Key(namespace == null || namespace.isBlank() ? MINECRAFT_NAMESPACE : namespace, value);
    }

    /**
     * Gets the comparator.
     *
     * <p>
     * The {@link #value() value} is compared first, followed by the
     * {@link #namespace() namespace}.
     * </p>
     *
     * @return a comparator for keys
     */
    public static Comparator<? super Key> comparator() {
        return COMPARATOR;
    }

    /**
     * Checks if {@code string} can be parsed into a {@link Key}.
     *
     * @param string the input string
     * @return {@code true} if {@code string} can be parsed into a {@link Key},
     *         {@code false} otherwise
     */
    public static boolean parseable(final String string) {
        return KeyFormat.minecraft().parseable(string);
    }

    /**
     * Checks if {@code value} is a valid namespace.
     *
     * @param namespace the string to check
     * @return {@code true} if {@code value} is a valid namespace, {@code false}
     *         otherwise
     */
    public static boolean parseableNamespace(final String namespace) {
        return checkNamespace(namespace).isEmpty();
    }

    /**
     * Checks if {@code value} is a valid namespace.
     *
     * <p>
     * The return value is as follows:
     * </p>
     * <ul>
     * <li>{@link OptionalInt#empty()} if {@code value} is a valid namespace</li>
     * <li>{@link OptionalInt#of(int)} with an int of {@code -1} if {@code value} is
     * invalid because entire namespace is invalid</li>
     * <li>{@link OptionalInt#of(int)} if {@code value} contains invallid
     * characters, containing the index of the start of the invalid character</li>
     * </ul>
     *
     * @param namespace the string to check
     * @return an optional int indicating the result of the check
     */
    public static OptionalInt checkNamespace(final String namespace) {
        if (namespace == null) {
            return OptionalInt.of(-1);
        }
        if (namespace.equals("..")) {
            return OptionalInt.of(-1);
        }
        for (int i = 0, length = namespace.length(); i < length; i++) {
            if (!allowedInNamespace(namespace.charAt(i))) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Checks if {@code value} is a valid value.
     *
     * @param value the string to check
     * @return {@code true} if {@code value} is a valid value, {@code false}
     *         otherwise
     */
    public static boolean parseableValue(final String value) {
        return checkValue(value).isEmpty();
    }

    /**
     * Checks if {@code value} is a valid value.
     *
     * @param value the string to check
     * @return {@link OptionalInt#empty()} if {@code value} is a valid value,
     *         otherwise an {@code OptionalInt} containing the index of an invalid
     *         character
     */
    public static OptionalInt checkValue(final String value) {
        if (value == null) {
            return OptionalInt.of(-1);
        }
        for (int i = 0, length = value.length(); i < length; i++) {
            if (!allowedInValue(value.charAt(i))) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Checks if {@code value} is a valid character in a namespace.
     *
     * @param character the character to check
     * @return {@code true} if {@code value} is a valid character in a namespace,
     *         {@code false} otherwise
     */
    private static boolean allowedInNamespace(final char character) {
        return character == '_' || character == '-' || (character >= 'a' && character <= 'z')
                || (character >= '0' && character <= '9') || character == '.';
    }

    /**
     * Checks if {@code value} is a valid character in a value.
     *
     * @param character the character to check
     * @return {@code true} if {@code value} is a valid character in a value,
     *         {@code false} otherwise
     */
    private static boolean allowedInValue(final char character) {
        return character == '_' || character == '-' || (character >= 'a' && character <= 'z')
                || (character >= '0' && character <= '9') || character == '.' || character == '/';
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // It's okay, this is internal, and it's fine anyway.
    private static void checkError(final String name, final String checkPart, final String namespace,
            final String value,
            final OptionalInt index, final String pattern) {
        if (checkPart == null) {
            throw new InvalidKeyException(namespace, value, String.format(
                    "'%s' cannot be null",
                    name));
        }
        if (index.isPresent()) {
            final int indexValue = index.getAsInt();
            if (indexValue == -1) {
                throw new InvalidKeyException(namespace, value, String.format(
                        "'%s' is not a valid value for %s",
                        checkPart,
                        name));
            } else {
                final char character = checkPart.charAt(indexValue);
                throw new InvalidKeyException(namespace, value, String.format(
                        "Non " + pattern + " character in %s of Key[%s] at index %d ('%s', bytes: %s)",
                        name,
                        namespace + DEFAULT_SEPARATOR + value,
                        indexValue,
                        character,
                        Arrays.toString(String.valueOf(character).getBytes(StandardCharsets.UTF_8))));
            }
        }
    }

    /**
     * Returns the string representation of this key.
     *
     * @return the string representation
     */
    public String asString() {
        return KeyFormat.minecraft().asString(this);
    }

    /**
     * Returns the string representation of this key in minimal form.
     *
     * <p>
     * If the {@link #namespace()} of this key is {@link #MINECRAFT_NAMESPACE}, only
     * the {@link #value()} will be returned.
     * </p>
     *
     * @return the string representation
     */
    public String asMinimalString() {
        return KeyFormat.minecraft().asMinimalString(this);
    }

    public NamespacedKey toBukkit() {
        return new NamespacedKey(namespace, value);
    }

    @Override
    public int compareTo(final Key that) {
        return comparator().compare(this, that);
    }

    @Override
    public Key key() {
        return this;
    }

    @Override
    public String toString() {
        return this.asString();
    }
}