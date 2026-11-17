package com.lemonlightmc.zenith.config.schema;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import com.lemonlightmc.zenith.additive.logger.GlobalLogger;

/**
 * Represents a typed schema node that stores a value, default, and optional validator.
 *
 * @param <T> the value type represented by this schema pair
 */
public class SchemaPair<T> extends SchemaNode {

  /** The current parsed value for this schema pair. */
  public T value;
  /** The default value used when parsing or validation fails. */
  public T def;
  /** The schema type describing this pair's value. */
  public SchemaType<T> type;
  /** An optional validator applied to parsed values. */
  public Predicate<T> validator;

  /**
   * Creates a new schema pair.
   *
   * @param path the configuration path for this pair
   * @param type the schema type describing this pair's value
   * @param def the default value for this pair
   * @param commet the optional comment for this pair
   * @param validator the optional validator applied to parsed values
   * @throws IllegalArgumentException if the path is null or empty, or if the type is null
   */
  public SchemaPair(final String path, final SchemaType<T> type, final T def, final String commet,
      final Predicate<T> validator) {
    super(path, commet);
    if (path == null || path.isEmpty()) {
      throw new IllegalArgumentException("Schema Pair Path cannot be null or empty");
    }
    if (type == null) {
      throw new IllegalArgumentException("Schema Pair Type cannot be null");
    }
    this.type = type;
    this.def = def;
    this.validator = validator;
  }

  /**
   * Creates a new schema pair without a validator.
   *
   * @param path the configuration path for this pair
   * @param type the schema type describing this pair's value
   * @param def the default value for this pair
   * @param commet the optional comment for this pair
   */
  public SchemaPair(final String path, final SchemaType<T> type, final T def, final String commet) {
    this(path, type, def, commet, null);
  }

  /**
   * Creates a new schema pair without a comment or validator.
   *
   * @param path the configuration path for this pair
   * @param type the schema type describing this pair's value
   * @param def the default value for this pair
   */
  public SchemaPair(final String path, final SchemaType<T> type, final T def) {
    this(path, type, def, null, null);
  }

  /**
   * Creates a new schema pair without a default, comment, or validator.
   *
   * @param path the configuration path for this pair
   * @param type the schema type describing this pair's value
   */
  public SchemaPair(final String path, final SchemaType<T> type) {
    this(path, type, null, null, null);
  }

  /**
   * Creates a new schema pair.
   *
   * @param <T> the value type represented by the schema pair
   * @param path the configuration path for this pair
   * @param type the schema type describing this pair's value
   * @param def the default value for this pair
   * @param comment the optional comment for this pair
   * @param validator the optional validator applied to parsed values
   * @return a new {@link SchemaPair} instance
   */
  public static <T> SchemaPair<T> create(final String path, final SchemaType<T> type, final T def,
      final String comment, final Predicate<T> validator) {
    return new SchemaPair<>(path, type, def, comment, validator);
  }

  /**
   * Creates a new schema pair without a validator.
   *
   * @param <T> the value type represented by the schema pair
   * @param path the configuration path for this pair
   * @param type the schema type describing this pair's value
   * @param def the default value for this pair
   * @param comment the optional comment for this pair
   * @return a new {@link SchemaPair} instance
   */
  public static <T> SchemaPair<T> create(final String path, final SchemaType<T> type, final T def,
      final String comment) {
    return new SchemaPair<>(path, type, def, comment, null);
  }

  /**
   * Creates a new schema pair without a comment or validator.
   *
   * @param <T> the value type represented by the schema pair
   * @param path the configuration path for this pair
   * @param type the schema type describing this pair's value
   * @param def the default value for this pair
   * @return a new {@link SchemaPair} instance
   */
  public static <T> SchemaPair<T> create(final String path, final SchemaType<T> type, final T def) {
    return new SchemaPair<>(path, type, def, null, null);
  }

  /**
   * Creates a new schema pair without a default, comment, or validator.
   *
   * @param <T> the value type represented by the schema pair
   * @param path the configuration path for this pair
   * @param type the schema type describing this pair's value
   * @return a new {@link SchemaPair} instance
   */
  public static <T> SchemaPair<T> create(final String path, final SchemaType<T> type) {
    return new SchemaPair<>(path, type, null, null, null);
  }

  /**
   * Parses the given object into this pair's value type and applies validation.
   *
   * @param obj the object to parse
   * @return the parsed value, or the default value if parsing or validation fails
   */
  public T parse(final Object obj) {
    value = type.parse(obj);
    if (value == null || (validator != null && validator.test(value))) {
      GlobalLogger.warn("Validation failed for path " + path + " with value " + value);
      this.value = def;
    }
    return value;
  }

  /**
   * Parses and stores the given object as this pair's current value.
   *
   * @param obj the object to parse
   */
  public void fillValue(final Object obj) {
    this.value = parse(obj);
  }

  /**
   * Casts this schema pair to another schema type if the types are equal.
   *
   * @param <E> the target value type
   * @param type the target schema type
   * @return this pair cast to the target type, or {@code null} if the types differ
   */
  @SuppressWarnings("unchecked")
  public <E> SchemaPair<E> cast(final SchemaType<E> type) {
    return this.type.equals(type) ? (SchemaPair<E>) this : null;
  }

  /**
   * Returns this pair's current value as the requested schema type when compatible.
   *
   * @param <E> the target value type
   * @param t the target schema type
   * @return an optional containing the value or default when types match; otherwise empty
   */
  @SuppressWarnings("unchecked")
  public <E> Optional<E> toType(final SchemaType<E> t) {
    if (!this.type.equals(t)) {
      return Optional.empty();
    }
    return Optional.of(value == null ? (E) def : (E) this.value);
  }

  /**
   * Returns the current value of this schema pair.
   *
   * @return the current value
   */
  public T getValue() {
    return value;
  }

  /**
   * Sets the current value of this schema pair.
   *
   * @param value the value to store
   * @return this schema pair for chaining
   */
  public SchemaPair<T> setValue(final T value) {
    this.value = value;
    return this;
  }

  /**
   * Returns the default value of this schema pair.
   *
   * @return the default value
   */
  public Object getDefault() {
    return def;
  }

  /**
   * Sets the default value of this schema pair.
   *
   * @param def the default value to store
   */
  public void setDefault(final T def) {
    this.def = def;
  }

  /**
   * Returns the schema type of this pair.
   *
   * @return the schema type
   */
  public SchemaType<T> getType() {
    return type;
  }

  /**
   * Checks whether this pair uses the given schema type.
   *
   * @param type the schema type to compare
   * @return {@code true} if the types are equal; {@code false} otherwise
   */
  public boolean isType(final SchemaType<?> type) {
    return this.type.equals(type);
  }

  /**
   * Returns the validator applied to parsed values.
   *
   * @return the validator, or {@code null} if none is configured
   */
  public Predicate<T> getValidator() {
    return validator;
  }

  /**
   * Sets the validator applied to parsed values.
   *
   * @param validator the validator to apply
   */
  public void setValidator(final Predicate<T> validator) {
    this.validator = validator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 31 * super.hashCode() + ((value == null) ? 0 : value.hashCode());
    result = 31 * result + ((def == null) ? 0 : def.hashCode());
    return 31 * result + ((validator == null) ? 0 : validator.hashCode());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
      return false;
    }
    final SchemaPair<?> other = (SchemaPair<?>) obj;
    return Objects.equals(value, other.value) && Objects.equals(def, other.def) && type.equals(other.type)
        && Objects.equals(validator, other.validator);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "SchemaPair [path=" + path + ", type=" + type + ", value=" + value + ", def=" + def + "]";
  }
}
