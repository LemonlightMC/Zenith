package com.lemonlightmc.zenith.config.schema;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a schema node that groups child schema nodes into a section.
 */
public class SchemaSection extends SchemaNode {

  /** The child nodes contained in this section. */
  protected Collection<SchemaNode> nodes = new ArrayList<>();

  /**
   * Returns the child nodes contained in this section.
   *
   * @return the child schema nodes
   */
  protected Collection<SchemaNode> getNodes() {
    return nodes;
  }

  /**
   * Creates a new schema section with the given child nodes.
   *
   * @param path the section path
   * @param commet the optional section comment
   * @param nodes the child nodes to add
   */
  public SchemaSection(final String path, final String commet, final SchemaNode... nodes) {
    super(path, commet);
    addNodes(nodes);
  }

  /**
   * Creates a new schema section with the given child nodes.
   *
   * @param path the section path
   * @param commet the optional section comment
   * @param nodes the child nodes to add
   */
  public SchemaSection(final String path, final String commet, final Collection<SchemaNode> nodes) {
    super(path, commet);
    addNodes(nodes);
  }

  /**
   * Creates a new schema section without child nodes.
   *
   * @param path the section path
   * @param commet the optional section comment
   */
  public SchemaSection(final String path, final String commet) {
    super(path, commet);
  }

  /**
   * Creates a new schema section without a comment or child nodes.
   *
   * @param path the section path
   */
  public SchemaSection(final String path) {
    super(path, null);
  }

  /**
   * Creates a new schema section with the given child nodes.
   *
   * @param name the section path
   * @param comment the optional section comment
   * @param nodes the child nodes to add
   * @return a new {@link SchemaSection} instance
   */
  public static SchemaSection create(final String name, final String comment, final SchemaNode... nodes) {
    return new SchemaSection(name, comment, nodes);
  }

  /**
   * Creates a new schema section with the given child nodes.
   *
   * @param name the section path
   * @param comment the optional section comment
   * @param nodes the child nodes to add
   * @return a new {@link SchemaSection} instance
   */
  public static SchemaSection create(final String name, final String comment, final Collection<SchemaNode> nodes) {
    return new SchemaSection(name, comment, nodes);
  }

  /**
   * Creates a new schema section without child nodes.
   *
   * @param name the section path
   * @param comment the optional section comment
   * @return a new {@link SchemaSection} instance
   */
  public static SchemaSection create(final String name, final String comment) {
    return new SchemaSection(name, comment);
  }

  /**
   * Creates a new schema section without a comment or child nodes.
   *
   * @param name the section path
   * @return a new {@link SchemaSection} instance
   */
  public static SchemaSection create(final String name) {
    return new SchemaSection(name, null);
  }

  /**
   * Adds the given child nodes to this section.
   *
   * @param nodes the child nodes to add
   * @return this section for chaining
   */
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

  /**
   * Adds the given child nodes to this section.
   *
   * @param nodes the child nodes to add
   * @return this section for chaining
   */
  public SchemaSection addNodes(final Collection<SchemaNode> nodes) {
    if (nodes == null || nodes.isEmpty()) {
      return this;
    }
    nodes.removeIf(n -> n == null);
    this.nodes.addAll(nodes);
    return this;
  }

  /**
   * Removes child nodes whose paths match the given collection.
   *
   * @param paths the node paths to remove
   * @return this section for chaining
   */
  public SchemaSection removeNodes(final Collection<String> paths) {
    if (paths == null || paths.isEmpty()) {
      return this;
    }
    this.nodes.removeIf(n -> paths.contains(n.path));
    return this;
  }

  /**
   * Removes the given child nodes from this section.
   *
   * @param nodes the child nodes to remove
   * @return this section for chaining
   */
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

  /**
   * Replaces the child nodes in this section.
   *
   * @param nodes the new child nodes
   * @return this section for chaining
   */
  public SchemaSection setNodes(final Collection<SchemaNode> nodes) {
    this.nodes = nodes;
    return this;
  }

  /**
   * Checks whether this section contains any child nodes.
   *
   * @return {@code true} if child nodes are present; {@code false} otherwise
   */
  public boolean hasNodes() {
    return nodes != null && !nodes.isEmpty();
  }

  /**
   * Removes all child nodes from this section.
   *
   * @return this section for chaining
   */
  public SchemaSection clearNodes() {
    nodes.clear();
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SchemaSection setComment(final String comment) {
    super.setComment(comment);
    return this;
  }
}
