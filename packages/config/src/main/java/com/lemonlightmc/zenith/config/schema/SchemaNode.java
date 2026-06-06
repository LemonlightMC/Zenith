package com.lemonlightmc.zenith.config.schema;

public abstract class SchemaNode {
  protected String path;
  protected String comment;

  public SchemaNode(final String path, final String commet) {
    if (path == null) {
      throw new IllegalArgumentException("SchemaNode Path cannot be null");
    }
    this.path = path;
    this.comment = commet != null && commet.isEmpty() ? null : commet;
  }

  public SchemaNode(final String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public String getComment() {
    return comment;
  }

  public SchemaNode setComment(final String comment) {
    this.comment = comment;
    return this;
  }

  @Override
  public int hashCode() {
    return 31 * (31 + path.hashCode()) + ((comment == null) ? 0 : comment.hashCode());
  }

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

  @Override
  public String toString() {
    return "SchemaNode [path=" + path + ", comment=" + comment + "]";
  }

}
