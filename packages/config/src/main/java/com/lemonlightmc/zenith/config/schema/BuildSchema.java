package com.lemonlightmc.zenith.config.schema;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lemonlightmc.zenith.additive.Ref;
import com.lemonlightmc.zenith.additive.StringUtils;
import com.lemonlightmc.zenith.additive.files.FileUtils;
import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.exceptions.ConfigParsingException;

/**
 * Represents a flattened, immutable schema ready for parsing and serialization.
 *
 * @param nodes the flattened schema nodes
 * @param len the number of nodes in the schema
 * @param header the optional schema header comment
 * @param footer the optional schema footer comment
 */
public record BuildSchema(SchemaNode[] nodes, int len, String header, String footer) {

  /**
   * Builds a {@link BuildSchema} from the given schema definition.
   *
   * @param schema the schema definition to build from
   * @return the built schema
   * @throws IllegalArgumentException if the schema is {@code null}
   */
  public static BuildSchema from(final Schema schema) {
    if (schema == null) {
      throw new IllegalArgumentException("Schema cannot be null");
    }
    return schema.build();
  }

  /**
   * Creates a built schema from the given nodes, header, and footer.
   *
   * @param nodes the schema nodes
   * @param header the optional schema header comment
   * @param footer the optional schema footer comment
   * @return a new {@link BuildSchema} instance
   */
  public static BuildSchema from(final List<SchemaNode> nodes, final String header, final String footer) {
    return new BuildSchema(nodes.toArray(new SchemaNode[nodes.size()]), nodes.size(),
        header.isEmpty() ? null : header,
        footer.isEmpty() ? null : footer);
  }

  /**
   * Creates a built schema from the given nodes.
   *
   * @param nodes the schema nodes
   * @return a new {@link BuildSchema} instance
   */
  public static BuildSchema from(final List<SchemaNode> nodes) {
    return new BuildSchema(nodes.toArray(new SchemaNode[nodes.size()]), nodes.size(), null, null);
  }

  /**
   * Creates a built schema from the given nodes, header, and footer.
   *
   * @param nodes the schema nodes
   * @param header the optional schema header comment
   * @param footer the optional schema footer comment
   * @return a new {@link BuildSchema} instance
   */
  public static BuildSchema from(final SchemaNode[] nodes, final String header, final String footer) {
    return new BuildSchema(nodes, nodes.length,
        header.isEmpty() ? null : header,
        footer.isEmpty() ? null : footer);
  }

  /**
   * Creates a built schema from the given nodes.
   *
   * @param nodes the schema nodes
   * @return a new {@link BuildSchema} instance
   */
  public static BuildSchema from(final SchemaNode[] nodes) {
    return new BuildSchema(nodes, nodes.length, null, null);
  }

  /**
   * Creates an empty built schema.
   *
   * @return a new empty {@link BuildSchema} instance
   */
  public static BuildSchema from() {
    return new BuildSchema(new SchemaNode[0], 0, null, null);
  }

  /**
   * Checks whether this schema contains any nodes.
   *
   * @return {@code true} if nodes are present; {@code false} otherwise
   */
  public boolean hasNodes() {
    return nodes != null || len != 0;
  }

  /**
   * Checks whether this schema has a header comment.
   *
   * @return {@code true} if a header is present; {@code false} otherwise
   */
  public boolean hasHeader() {
    return header != null || !header.isEmpty();
  }

  /**
   * Checks whether this schema has a footer comment.
   *
   * @return {@code true} if a footer is present; {@code false} otherwise
   */
  public boolean hasFooter() {
    return footer != null || !footer.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "BuildSchema [len=" + len + ",nodes=" + Arrays.toString(nodes) + "]";
  }

  /**
   * Parses raw configuration data into schema-backed key-value pairs.
   *
   * @param data the raw configuration data to parse
   * @return a map of parsed schema pairs keyed by path
   * @throws ConfigParsingException if the raw data does not match the expected schema structure
   */
  public Map<String, SchemaPair<?>> parse(final Map<String, Object> data) {
    if (data == null || data.isEmpty()) {
      return Map.of();
    }
    final Map<String, SchemaPair<?>> schemaPairs = new HashMap<>();
    final Ref<Integer> idxRef = new Ref<>(0);
    for (; idxRef.value < len; idxRef.value++) {
      parseNode(idxRef, schemaPairs, nodes[idxRef.value], data);
    }
    return schemaPairs;
  }

  @SuppressWarnings("unchecked")
  private void parseNode(final Ref<Integer> idxRef, final Map<String, SchemaPair<?>> schemaPairs, final SchemaNode node,
      final Map<String, Object> data) {
    final Object obj = data.get(node.path);
    if (node instanceof final SchemaPair<?> pair) {
      pair.fillValue(obj);
      schemaPairs.put(pair.path, pair);
    } else if (node instanceof final BuildSchemaSection section) {
      if (!(obj instanceof final Map<?, ?> map)) {
        throw new ConfigParsingException("Invalid Config! Object at '" + section.path + "' is not a Config Section!");
      }
      final Map<String, Object> objMap = (Map<String, Object>) map;
      for (; idxRef.value < section.size; idxRef.value++) {
        parseNode(idxRef, schemaPairs, nodes[idxRef.value], objMap);
      }
    }
  }

  /**
   * Saves this schema definition to the given path.
   *
   * @param path the destination path
   * @throws IllegalArgumentException if the path is {@code null} or the schema cannot be saved
   */
  public void save(final Path path) {
    if (path == null) {
      throw new IllegalArgumentException("Path cannot be null");
    }
    try {
      FileUtils.mkdirs(path.getParent());
      FileUtils.write(path, saveToString());
    } catch (final Exception e) {
      throw new IllegalArgumentException("Failed to save Schema to " + path, e);
    }
  }

  /**
   * Loads a built schema definition from the given path.
   *
   * @param path the source path
   * @return the loaded {@link BuildSchema}
   * @throws IllegalArgumentException if the path is {@code null}
   */
  public static BuildSchema load(final Path path) {
    if (path == null) {
      throw new IllegalArgumentException("Path cannot be null");
    }
    final String raw = FileUtils.readString(path);
    return loadFromString(raw);
  }

  /**
   * Represents a flattened schema section marker used during build and serialization.
   */
  static class BuildSchemaSection extends SchemaNode {
    /** The number of nodes contained in this section. */
    protected final int size;

    /**
     * Creates a new built schema section marker.
     *
     * @param name the section path
     * @param comment the optional section comment
     * @param size the number of nodes contained in the section
     */
    public BuildSchemaSection(final String name, final String comment, final int size) {
      super(name, comment);
      this.size = size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      return 31 * super.hashCode() + size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || !super.equals(obj) || getClass() != obj.getClass()) {
        return false;
      }
      return size == ((BuildSchemaSection) obj).size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return "BuildSchemaSection [size=" + size + "]";
    }
  }

  private static final char SEPERATOR_CHAR = ' ';
  private static final char NEWLINE_CHAR = '\n';
  private static final char SECTION_CHAR = 'S';
  private static final char PAIR_CHAR = 'P';

  /**
   * Serializes this schema definition to a string representation.
   *
   * @return the serialized schema content
   */
  public String saveToString() {
    final StringBuilder builder = new StringBuilder();
    if (header != null) {
      builder.append('#').append(StringUtils.escape(header)).append('\n');
    }
    for (final SchemaNode node : nodes) {
      writeNode(node, builder);
    }
    if (footer != null) {
      builder.append('#').append(StringUtils.escape(footer)).append('\n');
    }
    return builder.toString();
  }

  /**
   * Loads a built schema definition from its serialized string representation.
   *
   * @param <T> the schema pair value type used when reconstructing pairs
   * @param raw the serialized schema content
   * @return the loaded {@link BuildSchema}
   */
  @SuppressWarnings("unchecked")
  public static <T> BuildSchema loadFromString(final String raw) {
    if (raw == null || raw.isBlank()) {
      return BuildSchema.from();
    }

    final ArrayList<SchemaNode> nodes = new ArrayList<>();
    final String[] lines = StringUtils.fastSplit(raw, NEWLINE_CHAR);
    String header = null;
    if (lines[0].charAt(0) == '#') {
      header = StringUtils.unescape(lines[0].substring(1));
    }
    String footer = null;
    if (lines[lines.length - 1].charAt(0) == '#') {
      footer = StringUtils.unescape(lines[lines.length - 1].substring(1));
    }

    for (final String rawLine : lines) {
      if (rawLine == null || rawLine.isEmpty() || raw.charAt(0) == '#') {
        continue;
      }

      final String[] parts = StringUtils.fastSplit(raw, SEPERATOR_CHAR);
      if (parts.length < 2) {
        continue;
      }
      final String prefix = parts[0];
      final String path = parts[1];
      if (prefix == null || prefix.isEmpty() || path == null || path.isEmpty()) {
        continue;
      }
      if (prefix == "S" && parts.length > 4) {
        final int sectionSize = NumberConversions.parseInt(parts[2]);
        final String comment = StringUtils.unescape(parts[3]);
        nodes.add(new BuildSchemaSection(path, comment, sectionSize));
      } else if (prefix == "P" && parts.length > 5) {
        final SchemaType<?> type = SchemaType.valueOf(parts[2]);
        final Object def = parseDefault(type, StringUtils.unescape(parts[3]));
        final String comment = StringUtils.unescape(parts[4]);
        nodes.add(SchemaPair.create(path, (SchemaType<T>) type, (T) def, comment));
      }
    }
    return BuildSchema.from(nodes, header, footer);
  }

  private static void writeNode(final SchemaNode node, final StringBuilder builder) {
    if (node instanceof final BuildSchemaSection childSection) {
      builder.append(SECTION_CHAR)
          .append(SEPERATOR_CHAR)
          .append(childSection.path)
          .append(SEPERATOR_CHAR)
          .append(childSection.size)
          .append(SEPERATOR_CHAR)
          .append(StringUtils.escape(childSection.getComment()))
          .append(NEWLINE_CHAR);
    } else if (node instanceof final SchemaPair<?> pair) {
      builder.append(PAIR_CHAR)
          .append(SEPERATOR_CHAR)
          .append(pair.path)
          .append(SEPERATOR_CHAR)
          .append(pair.getType().getName())
          .append(SEPERATOR_CHAR)
          .append(pair.getDefault() == null ? "" : StringUtils.escape(pair.getDefault().toString()))
          .append(SEPERATOR_CHAR)
          .append(StringUtils.escape(pair.getComment()))
          .append(NEWLINE_CHAR);
    }
  }

  private static Object parseDefault(final SchemaType<?> type, final String text) {
    if (text == null || text.isEmpty()) {
      return null;
    }
    if (type == SchemaType.STRING) {
      return text;
    }
    final Object parsed = type.parse(text);
    return parsed;
  }
}
