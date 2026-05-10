package com.lemonlightmc.zenith.config.schema;

import java.util.ArrayList;
import java.util.Collection;

public class SchemaSection extends SchemaNode {

  protected Collection<SchemaNode> nodes = new ArrayList<>();

  protected Collection<SchemaNode> getNodes() {
    return nodes;
  }

  public SchemaSection(final String path, final String commet, final SchemaNode... nodes) {
    super(path, commet);
    addNodes(nodes);
  }

  public SchemaSection(final String path, final String commet, final Collection<SchemaNode> nodes) {
    super(path, commet);
    addNodes(nodes);
  }

  public SchemaSection(final String path, final String commet) {
    super(path, commet);
  }

  public SchemaSection(final String path) {
    super(path, null);
  }

  public static SchemaSection create(final String name, final String comment, final SchemaNode... nodes) {
    return new SchemaSection(name, comment, nodes);
  }

  public static SchemaSection create(final String name, final String comment, final Collection<SchemaNode> nodes) {
    return new SchemaSection(name, comment, nodes);
  }

  public static SchemaSection create(final String name, final String comment) {
    return new SchemaSection(name, comment);
  }

  public static SchemaSection create(final String name) {
    return new SchemaSection(name, null);
  }

  public SchemaSection addNodes(final SchemaNode... nodes) {
    if (nodes == null || nodes.length == 0) {
      return this;
    }
    for (final SchemaNode schemaNode : nodes) {
      if (schemaNode != null) {
        this.nodes.add(schemaNode);
      }
    }
    return this;
  }

  public SchemaSection addNodes(final Collection<SchemaNode> nodes) {
    if (nodes == null || nodes.isEmpty()) {
      return this;
    }
    nodes.removeIf(n -> n == null);
    this.nodes.addAll(nodes);
    return this;
  }

  public SchemaSection removeNodes(final Collection<String> paths) {
    if (paths == null || paths.isEmpty()) {
      return this;
    }
    this.nodes.removeIf(n -> paths.contains(n.path));
    return this;
  }

  public SchemaSection removeNodes(final SchemaNode... nodes) {
    if (nodes == null || nodes.length == 0) {
      return this;
    }
    for (final SchemaNode schemaNode : nodes) {
      if (schemaNode != null) {
        this.nodes.remove(schemaNode);
      }
    }
    return this;
  }

  public SchemaSection setNodes(final Collection<SchemaNode> nodes) {
    this.nodes = nodes;
    return this;
  }

  public boolean hasNodes() {
    return nodes != null && !nodes.isEmpty();
  }

  public SchemaSection clearNodes() {
    nodes.clear();
    return this;
  }

  public SchemaSection setComment(final String comment) {
    super.setComment(comment);
    return this;
  }
}
