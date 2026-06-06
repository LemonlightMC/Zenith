package com.lemonlightmc.zenith.config.schema;

import java.util.Optional;
import java.util.function.Predicate;

import com.lemonlightmc.zenith.messages.Logger;

public class SchemaPair<T> extends SchemaNode {

  public T value;
  public T def;
  public SchemaType<T> type;
  public Predicate<T> validator;

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

  public SchemaPair(final String path, final SchemaType<T> type, final T def, final String commet) {
    this(path, type, def, commet, null);
  }

  public SchemaPair(final String path, final SchemaType<T> type, final T def) {
    this(path, type, def, null, null);
  }

  public SchemaPair(final String path, final SchemaType<T> type) {
    this(path, type, null, null, null);
  }

  public static <T> SchemaPair<T> create(final String path, final SchemaType<T> type, final T def,
      final String comment, final Predicate<T> validator) {
    return new SchemaPair<>(path, type, def, comment, validator);
  }

  public static <T> SchemaPair<T> create(final String path, final SchemaType<T> type, final T def,
      final String comment) {
    return new SchemaPair<>(path, type, def, comment, null);
  }

  public static <T> SchemaPair<T> create(final String path, final SchemaType<T> type, final T def) {
    return new SchemaPair<>(path, type, def, null, null);
  }

  public static <T> SchemaPair<T> create(final String path, final SchemaType<T> type) {
    return new SchemaPair<>(path, type, null, null, null);
  }

  public T parse(final Object obj) {
    value = type.parse(obj);
    if (value == null || (validator != null && validator.test(value))) {
      Logger.warn("Validation failed for path " + path + " with value " + value);
      this.value = def;
    }
    return value;
  }

  public void fillValue(final Object obj) {
    this.value = parse(obj);
  }

  @SuppressWarnings("unchecked")
  public <E> SchemaPair<E> cast(final SchemaType<E> type) {
    return this.type.equals(type) ? (SchemaPair<E>) this : null;
  }

  @SuppressWarnings("unchecked")
  public <E> Optional<E> toType(final SchemaType<E> t) {
    if (!this.type.equals(t)) {
      return Optional.empty();
    }
    return Optional.of(value == null ? (E) def : (E) this.value);
  }

  public T getValue() {
    return value;
  }

  public SchemaPair<T> setValue(final T value) {
    this.value = value;
    return this;
  }

  public Object getDefault() {
    return def;
  }

  public void setDefault(final T def) {
    this.def = def;
  }

  public SchemaType<T> getType() {
    return type;
  }

  public boolean isType(final SchemaType<?> type) {
    return this.type.equals(type);
  }

  public Predicate<T> getValidator() {
    return validator;
  }

  public void setValidator(final Predicate<T> validator) {
    this.validator = validator;
  }

  @Override
  public int hashCode() {
    int result = 31 * super.hashCode() + ((value == null) ? 0 : value.hashCode());
    result = 31 * result + ((def == null) ? 0 : def.hashCode());
    return 31 * result + ((validator == null) ? 0 : validator.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
      return false;
    }
    final SchemaPair<?> other = (SchemaPair<?>) obj;
    if (value == null && other.value != null || def == null && other.def != null
        || validator == null && other.validator != null) {
      return false;
    }
    return value.equals(other.value) && def.equals(other.def) && validator.equals(other.validator);
  }

  @Override
  public String toString() {
    return "SchemaPair [value=" + value + ", def=" + def + ", validator=" + validator + "]";
  }
}
