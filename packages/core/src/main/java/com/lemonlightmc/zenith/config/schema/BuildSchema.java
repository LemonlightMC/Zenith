package com.lemonlightmc.zenith.config.schema;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lemonlightmc.zenith.exceptions.ConfigParsingException;
import com.lemonlightmc.zenith.files.FileUtils;
import com.lemonlightmc.zenith.math.NumberConversions;
import com.lemonlightmc.zenith.utils.Ref;
import com.lemonlightmc.zenith.utils.StringUtils;

public record BuildSchema(SchemaNode[] nodes, int len, String header, String footer) {

  public static BuildSchema from(final Schema schema) {
    if (schema == null) {
      throw new IllegalArgumentException("Schema cannot be null");
    }
    return schema.build();
  }

  public static BuildSchema from(final List<SchemaNode> nodes, final String header, final String footer) {
    return new BuildSchema(nodes.toArray(new SchemaNode[nodes.size()]), nodes.size(),
        header.isEmpty() ? null : header,
        footer.isEmpty() ? null : footer);
  }

  public static BuildSchema from(final List<SchemaNode> nodes) {
    return new BuildSchema(nodes.toArray(new SchemaNode[nodes.size()]), nodes.size(), null, null);
  }

  public static BuildSchema from(final SchemaNode[] nodes, final String header, final String footer) {
    return new BuildSchema(nodes, nodes.length,
        header.isEmpty() ? null : header,
        footer.isEmpty() ? null : footer);
  }

  public static BuildSchema from(final SchemaNode[] nodes) {
    return new BuildSchema(nodes, nodes.length, null, null);
  }

  public static BuildSchema from() {
    return new BuildSchema(new SchemaNode[0], 0, null, null);
  }

  public boolean hasNodes() {
    return nodes != null || len != 0;
  }

  public boolean hasHeader() {
    return header != null || !header.isEmpty();
  }

  public boolean hasFooter() {
    return footer != null || !footer.isEmpty();
  }

  @Override
  public String toString() {
    return "BuildSchema [len=" + len + ",nodes=" + Arrays.toString(nodes) + "]";
  }

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

  public static BuildSchema load(final Path path) {
    if (path == null) {
      throw new IllegalArgumentException("Path cannot be null");
    }
    final String raw = FileUtils.readString(path);
    return loadFromString(raw);
  }

  static class BuildSchemaSection extends SchemaNode {
    protected final int size;

    public BuildSchemaSection(final String name, final String comment, final int size) {
      super(name, comment);
      this.size = size;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + size;
    }

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

    @Override
    public String toString() {
      return "BuildSchemaSection [size=" + size + "]";
    }
  }

  private static final char SEPERATOR_CHAR = ' ';
  private static final char NEWLINE_CHAR = '\n';
  private static final char SECTION_CHAR = 'S';
  private static final char PAIR_CHAR = 'P';

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
