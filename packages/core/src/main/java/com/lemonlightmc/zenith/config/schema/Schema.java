package com.lemonlightmc.zenith.config.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.lemonlightmc.zenith.config.schema.BuildSchema.BuildSchemaSection;

public class Schema extends SchemaSection {
  public Schema(final String header, final SchemaNode... nodes) {
    super("", header, nodes);
  }

  public Schema(final String header, final Collection<SchemaNode> nodes) {
    super("", header, nodes);
  }

  public Schema(final String header) {
    super("", header);
  }

  public Schema() {
    super("", null);
  }

  public static Schema create(final String header, final SchemaNode... nodes) {
    return new Schema(header, nodes);
  }

  public static Schema create(final String header, final Collection<SchemaNode> nodes) {
    return new Schema(header, nodes);
  }

  public static Schema create(final String header) {
    return new Schema(header);
  }

  public static Schema create() {
    return new Schema();
  }

  public static SchemaSection section(final String name, final String comment, final SchemaNode... nodes) {
    return new SchemaSection(name, comment, nodes);
  }

  public static SchemaSection section(final String name, final SchemaNode... nodes) {
    return new SchemaSection(name, null, nodes);
  }

  public static SchemaSection section(final String name, final String comment) {
    return new SchemaSection(name, comment);
  }

  public static SchemaSection section(final String name) {
    return new SchemaSection(name, null);
  }

  public static <T> SchemaPair<T> pair(final String name, final SchemaType<T> type, final T def,
      final String comment, final Predicate<T> validator) {
    return SchemaPair.create(name, type, def, comment, validator);
  }

  public static <T> SchemaPair<T> pair(final String name, final SchemaType<T> type, final T def,
      final String comment) {
    return SchemaPair.create(name, type, def, comment, null);
  }

  public static <T> SchemaPair<T> pair(final String name, final SchemaType<T> type, final T def) {
    return SchemaPair.create(name, type, def, null, null);
  }

  public static <T> SchemaPair<T> pair(final String name, final SchemaType<T> type) {
    return SchemaPair.create(name, type, null, null, null);
  }

  @Override
  public Schema addNodes(final SchemaNode... nodes) {
    super.addNodes(nodes);
    return this;
  }

  @Override
  public Schema removeNodes(final Collection<String> paths) {
    super.removeNodes(paths);
    return this;
  }

  @Override
  public Schema removeNodes(final SchemaNode... nodes) {
    super.removeNodes(nodes);
    return this;
  }

  @Override
  public Schema setNodes(final Collection<SchemaNode> nodes) {
    super.setNodes(nodes);
    return this;
  }

  @Override
  public Schema clearNodes() {
    super.clearNodes();
    return this;
  }

  public Schema setHeader(final String comment) {
    super.setComment(comment);
    return this;
  }

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
