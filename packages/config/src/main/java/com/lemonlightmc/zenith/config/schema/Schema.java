package com.lemonlightmc.zenith.config.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.lemonlightmc.zenith.config.schema.BuildSchema.BuildSchemaSection;

/**
 * Defines the structure of a configuration, including its sections and typed values.
 */
public class Schema extends SchemaSection {
  /**
   * Creates a new schema with the given header and child nodes.
   *
   * @param header the schema header comment
   * @param nodes the child schema nodes
   */
  public Schema(final String header, final SchemaNode... nodes) {
    super("", header, nodes);
  }

  /**
   * Creates a new schema with the given header and child nodes.
   *
   * @param header the schema header comment
   * @param nodes the child schema nodes
   */
  public Schema(final String header, final Collection<SchemaNode> nodes) {
    super("", header, nodes);
  }

  /**
   * Creates a new schema with the given header.
   *
   * @param header the schema header comment
   */
  public Schema(final String header) {
    super("", header);
  }

  /**
   * Creates a new empty schema.
   */
  public Schema() {
    super("", null);
  }

  /**
   * Creates a new schema with the given header and child nodes.
   *
   * @param header the schema header comment
   * @param nodes the child schema nodes
   * @return a new {@link Schema} instance
   */
  public static Schema create(final String header, final SchemaNode... nodes) {
    return new Schema(header, nodes);
  }

  /**
   * Creates a new schema with the given header and child nodes.
   *
   * @param header the schema header comment
   * @param nodes the child schema nodes
   * @return a new {@link Schema} instance
   */
  public static Schema create(final String header, final Collection<SchemaNode> nodes) {
    return new Schema(header, nodes);
  }

  /**
   * Creates a new schema with the given header.
   *
   * @param header the schema header comment
   * @return a new {@link Schema} instance
   */
  public static Schema create(final String header) {
    return new Schema(header);
  }

  /**
   * Creates a new empty schema.
   *
   * @return a new {@link Schema} instance
   */
  public static Schema create() {
    return new Schema();
  }

  /**
   * Creates a new schema section.
   *
   * @param name the section path
   * @param comment the optional section comment
   * @param nodes the child schema nodes
   * @return a new {@link SchemaSection} instance
   */
  public static SchemaSection section(final String name, final String comment, final SchemaNode... nodes) {
    return new SchemaSection(name, comment, nodes);
  }

  /**
   * Creates a new schema section without a comment.
   *
   * @param name the section path
   * @param nodes the child schema nodes
   * @return a new {@link SchemaSection} instance
   */
  public static SchemaSection section(final String name, final SchemaNode... nodes) {
    return new SchemaSection(name, null, nodes);
  }

  /**
   * Creates a new schema section without child nodes.
   *
   * @param name the section path
   * @param comment the optional section comment
   * @return a new {@link SchemaSection} instance
   */
  public static SchemaSection section(final String name, final String comment) {
    return new SchemaSection(name, comment);
  }

  /**
   * Creates a new schema section without a comment or child nodes.
   *
   * @param name the section path
   * @return a new {@link SchemaSection} instance
   */
  public static SchemaSection section(final String name) {
    return new SchemaSection(name, null);
  }

  /**
   * Creates a new typed schema pair.
   *
   * @param <T> the value type represented by the schema pair
   * @param name the pair path
   * @param type the schema type describing the pair's value
   * @param def the default value
   * @param comment the optional comment
   * @param validator the optional validator
   * @return a new {@link SchemaPair} instance
   */
  public static <T> SchemaPair<T> pair(final String name, final SchemaType<T> type, final T def,
      final String comment, final Predicate<T> validator) {
    return SchemaPair.create(name, type, def, comment, validator);
  }

  /**
   * Creates a new typed schema pair without a validator.
   *
   * @param <T> the value type represented by the schema pair
   * @param name the pair path
   * @param type the schema type describing the pair's value
   * @param def the default value
   * @param comment the optional comment
   * @return a new {@link SchemaPair} instance
   */
  public static <T> SchemaPair<T> pair(final String name, final SchemaType<T> type, final T def,
      final String comment) {
    return SchemaPair.create(name, type, def, comment, null);
  }

  /**
   * Creates a new typed schema pair without a comment or validator.
   *
   * @param <T> the value type represented by the schema pair
   * @param name the pair path
   * @param type the schema type describing the pair's value
   * @param def the default value
   * @return a new {@link SchemaPair} instance
   */
  public static <T> SchemaPair<T> pair(final String name, final SchemaType<T> type, final T def) {
    return SchemaPair.create(name, type, def, null, null);
  }

  /**
   * Creates a new typed schema pair without a default, comment, or validator.
   *
   * @param <T> the value type represented by the schema pair
   * @param name the pair path
   * @param type the schema type describing the pair's value
   * @return a new {@link SchemaPair} instance
   */
  public static <T> SchemaPair<T> pair(final String name, final SchemaType<T> type) {
    return SchemaPair.create(name, type, null, null, null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Schema addNodes(final SchemaNode... nodes) {
    super.addNodes(nodes);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Schema removeNodes(final Collection<String> paths) {
    super.removeNodes(paths);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Schema removeNodes(final SchemaNode... nodes) {
    super.removeNodes(nodes);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Schema setNodes(final Collection<SchemaNode> nodes) {
    super.setNodes(nodes);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Schema clearNodes() {
    super.clearNodes();
    return this;
  }

  /**
   * Sets the schema header comment.
   *
   * @param comment the header comment
   * @return this schema for chaining
   */
  public Schema setHeader(final String comment) {
    super.setComment(comment);
    return this;
  }

  /**
   * Builds a flattened {@link BuildSchema} representation of this schema.
   *
   * @return the built schema
   */
  public BuildSchema build() {
    final List<SchemaNode> buildNodes = new ArrayList<>();
    buildSection(buildNodes, this, "");
    return BuildSchema.from(buildNodes, comment, null);
  }

  private static void buildSection(final List<SchemaNode> buildNodes, final SchemaSection section,
      final String currentPath) {
    final int marker = buildNodes.size();
    buildNodes.add(null);
    for (final SchemaNode schemaNode : section.nodes) {
      if (schemaNode instanceof final SchemaPair<?> pair) {
        if (!currentPath.isEmpty()) {
          pair.path = currentPath + '.' + pair.path;
        }
        buildNodes.add(pair);
      } else if (schemaNode instanceof final SchemaSection section2) {
        buildSection(buildNodes, section2, currentPath.isEmpty() ? section2.path : currentPath + '.' + section2.path);
      }
    }
    buildNodes.set(marker, new BuildSchemaSection(currentPath, section.comment, marker - buildNodes.size()));
  }
}
