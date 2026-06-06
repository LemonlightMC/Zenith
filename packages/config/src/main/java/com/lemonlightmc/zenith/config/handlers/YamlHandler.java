package com.lemonlightmc.zenith.config.handlers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.DumperOptions;
//import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.lemonlightmc.zenith.config.FileHandler;
import com.lemonlightmc.zenith.config.schema.SchemaPair;
import com.lemonlightmc.zenith.exceptions.ConfigParsingException;

public class YamlHandler extends FileHandler {

  private final Yaml yaml;

  public YamlHandler(final ConfigOptions options) {
    super(options);
    final LoaderOptions loaderOptions = new LoaderOptions();
    loaderOptions.setMaxAliasesForCollections(256);
    loaderOptions.setNestingDepthLimit(100);
    loaderOptions.setCodePointLimit(10 * 1024 * 1024);
    loaderOptions.setWrappedToRootException(true);
    loaderOptions.setProcessComments(true);

    final DumperOptions dumperOptions = new DumperOptions();
    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

    yaml = new Yaml(new YamlConstructor(loaderOptions), new YamlRepresenter(dumperOptions), dumperOptions,
        loaderOptions);
  }

  public static YamlHandler from(final ConfigOptions options) {
    return new YamlHandler(options);
  }

  @Override
  public String saveToString(final Map<String, SchemaPair<?>> data) {
    final Map<String, Object> nested = createNestedMap(data);
    return yaml.dump(nested);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> loadFromString(final String raw) {
    if (raw == null || raw.isBlank()) {
      return Map.of();
    }
    final Object loaded;
    try {
      loaded = yaml.load(raw);
    } catch (final Exception e) {
      throw new ConfigParsingException("Failed to parse YAML content", e);
    }
    if (loaded == null) {
      return Map.of();
    }
    if (!(loaded instanceof Map)) {
      throw new ConfigParsingException("Root YAML document must be a map");
    }
    return (Map<String, Object>) loaded;
  }

  private Map<String, Object> createNestedMap(final Map<String, SchemaPair<?>> data) {
    final Map<String, Object> root = new LinkedHashMap<>();
    if (data == null) {
      return root;
    }
    final String separator = String.valueOf(options().pathSeparator());
    final String separatorRegex = Pattern.quote(separator);
    for (final Map.Entry<String, SchemaPair<?>> entry : data.entrySet()) {
      final String key = entry.getKey();
      if (key == null || key.isEmpty()) {
        continue;
      }
      final Object value = entry.getValue() == null ? null : entry.getValue().getValue();
      final String[] segments = key.split(separatorRegex);
      Map<String, Object> current = root;
      for (int idx = 0; idx < segments.length - 1; idx++) {
        final String segment = segments[idx];
        final Object next = current.get(segment);
        if (!(next instanceof Map)) {
          final Map<String, Object> child = new LinkedHashMap<>();
          current.put(segment, child);
          current = child;
          continue;
        }
        @SuppressWarnings("unchecked")
        final Map<String, Object> child = (Map<String, Object>) next;
        current = child;
      }
      current.put(segments[segments.length - 1], value);
    }
    return root;
  }

  private static class YamlConstructor extends SafeConstructor {

    public YamlConstructor(LoaderOptions loaderOptions) {
      super(loaderOptions);
      this.yamlConstructors.put(Tag.MAP, new ConstructCustomObject());
    }

    private class ConstructCustomObject extends ConstructYamlMap {
      @Override
      public Object construct(Node node) {
        if (node.isTwoStepsConstruction()) {
          throw new YAMLException("Unexpected referential mapping structure. Node: " + node);
        }

        Map<?, ?> raw = (Map<?, ?>) super.construct(node);

        if (raw.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
          Map<String, Object> typed = new LinkedHashMap<String, Object>(raw.size());
          for (Map.Entry<?, ?> entry : raw.entrySet()) {
            typed.put(entry.getKey().toString(), entry.getValue());
          }

          try {
            return ConfigurationSerialization.deserializeObject(typed);
          } catch (IllegalArgumentException ex) {
            throw new YAMLException("Could not deserialize object", ex);
          }
        }

        return raw;
      }
    }
  }

  public class YamlRepresenter extends Representer {

    public YamlRepresenter(DumperOptions dumperOptions) {
      super(dumperOptions);
      this.multiRepresenters.put(ConfigurationSection.class, new RepresentConfigurationSection());
      this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());
    }

    private class RepresentConfigurationSection extends RepresentMap {
      @Override
      public Node representData(Object data) {
        return super.representData(((ConfigurationSection) data).getValues(false));
      }
    }

    private class RepresentConfigurationSerializable extends RepresentMap {
      @Override
      public Node representData(Object data) {
        ConfigurationSerializable serializable = (ConfigurationSerializable) data;
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY,
            ConfigurationSerialization.getAlias(serializable.getClass()));
        values.putAll(serializable.serialize());

        return super.representData(values);
      }
    }
  }

}
