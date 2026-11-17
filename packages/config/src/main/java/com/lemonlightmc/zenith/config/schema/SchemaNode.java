package com.lemonlightmc.zenith.config.schema;

/**
 * Represents a named node in a configuration schema.
 */
public abstract class SchemaNode {
  /** The path identifying this schema node. */
  protected String path;
  /** The optional comment associated with this schema node. */
  protected String comment;

  /**
   * Creates a new schema node.
   *
   * @param path the node path
   * @param commet the optional node comment
   * @throws IllegalArgumentException if the path is {@code null}
   */
  public SchemaNode(final String path, final String commet) {
    if (path == null) {
      throw new IllegalArgumentException("SchemaNode Path cannot be null");
    }
    this.path = path;
    this.comment = commet != null && commet.isEmpty() ? null : commet;
  }

  /**
   * Creates a new schema node without a comment.
   *
   * @param path the node path
   */
  public SchemaNode(final String path) {
    this.path = path;
  }

  /**
   * Returns the path of this schema node.
   *
   * @return the node path
   */
  public String getPath() {
    return path;
  }

  /**
   * Returns the comment associated with this schema node.
   *
   * @return the node comment, or {@code null} if none is set
   */
  public String getComment() {
    return comment;
  }

  /**
   * Sets the comment associated with this schema node.
   *
   * @param comment the node comment
   * @return this schema node for chaining
   */
  public SchemaNode setComment(final String comment) {
    this.comment = comment;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return 31 * (31 + path.hashCode()) + ((comment == null) ? 0 : comment.hashCode());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final SchemaNode other = (SchemaNode) obj;
    if (comment == null && other.comment != null) {
      return false;
    }
    return path.equals(other.path) && comment.equals(other.comment);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "SchemaNode [path=" + path + ", comment=" + comment + "]";
  }

}
