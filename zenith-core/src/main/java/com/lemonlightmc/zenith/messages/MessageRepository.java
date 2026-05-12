package com.lemonlightmc.zenith.messages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Supplier;

import com.lemonlightmc.zenith.files.FileUtils;
import com.lemonlightmc.zenith.files.ResourceUtils;
import com.lemonlightmc.zenith.interfaces.Cloneable;
import com.lemonlightmc.zenith.utils.StringUtils;
import com.lemonlightmc.zenith.version.Version;

public abstract class MessageRepository<T extends MessageRepository<T>> implements Cloneable<T> {
  protected String name;
  protected final Locale locale;
  protected String description;
  protected float progress;
  protected Version version;
  protected List<String> contributors;

  protected Map<String, String> messages;
  protected long last_updated;

  public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
  private static final Version DEFAULT_VERSION = Version.FIRST_VERSION;
  private static final List<String> DEFAULT_CONTRIBUTORS = List.of();
  private static final String DEFAULT_DESC = "No description";

  public MessageRepository(final Locale locale, final String name, final Version version,
      final float progress, final String desc,
      final List<String> contributors) {
    if (locale == null) {
      throw new IllegalArgumentException("Locale cannot be null for Message Repository!");
    }
    this.locale = locale;
    this.name = name == null ? locale.toString() : name;
    this.version = version == null ? DEFAULT_VERSION : version;
    this.contributors = contributors == null || !contributors.isEmpty() ? DEFAULT_CONTRIBUTORS : contributors;
    this.description = desc == null || !desc.isEmpty() ? DEFAULT_DESC : desc;
    setProgress(progress);
  }

  public MessageRepository(final String locale, final String name, final Version version,
      final float progress, final String desc,
      final List<String> contributors) {
    this(StringUtils.parseLocale(locale), name, version, progress, desc, contributors);
  }

  public MessageRepository(final Locale locale, final String name, final Version version,
      final float progress, final String desc) {
    this(locale, name, version, progress, desc, DEFAULT_CONTRIBUTORS);
  }

  public MessageRepository(final String locale, final String name, final Version version,
      final float progress, final String desc) {
    this(StringUtils.parseLocale(locale), name, version, progress, desc, DEFAULT_CONTRIBUTORS);
  }

  public MessageRepository(final Locale locale, final String name, final float progress,
      final String desc) {
    this(locale, name, DEFAULT_VERSION, progress, desc, DEFAULT_CONTRIBUTORS);
  }

  public MessageRepository(final String locale, final String name, final float progress,
      final String desc) {
    this(StringUtils.parseLocale(locale), name, DEFAULT_VERSION, progress, desc, DEFAULT_CONTRIBUTORS);
  }

  public MessageRepository(final Locale locale, final String name, final float progress) {
    this(locale, name, DEFAULT_VERSION, progress, DEFAULT_DESC, DEFAULT_CONTRIBUTORS);
  }

  public MessageRepository(final String locale, final String name, final float progress) {
    this(StringUtils.parseLocale(locale), name, DEFAULT_VERSION, progress, DEFAULT_DESC, DEFAULT_CONTRIBUTORS);
  }

  public MessageRepository(final Locale locale, final String name) {
    this(locale, name, DEFAULT_VERSION, 0f, DEFAULT_DESC, DEFAULT_CONTRIBUTORS);
  }

  public MessageRepository(final String locale) {
    this(StringUtils.parseLocale(locale), null, DEFAULT_VERSION, 0f, DEFAULT_DESC, DEFAULT_CONTRIBUTORS);
  }

  public MessageRepository(final MessageRepository<?> repo) {
    if (repo == null) {
      throw new IllegalArgumentException("Message Repositorysitory cannot be null when cloning!");
    }
    this.name = repo.name;
    this.locale = repo.locale;
    this.version = repo.version;
    this.progress = repo.progress;
    this.description = repo.description;
    this.contributors = repo.contributors;
    this.messages = repo.messages;
  }

  public static StaticMessageRepository from(final Locale locale, final Properties props) {
    return new StaticMessageRepository(locale, props);
  }

  public static StaticMessageRepository from(final Properties props) {
    return new StaticMessageRepository(null, props);
  }

  public static StaticMessageRepository from(final Locale locale, final ResourceBundle bundle) {
    return new StaticMessageRepository(locale, bundle);
  }

  public static StaticMessageRepository from(final ResourceBundle bundle) {
    return new StaticMessageRepository(null, bundle);
  }

  public static FileMessageRepository from(final Locale locale, final Path path) {
    return new FileMessageRepository(locale, path);
  }

  public static FileMessageRepository from(final Path path) {
    return new FileMessageRepository(null, path);
  }

  public static FileMessageRepository from(final Locale locale, final File file) {
    return new FileMessageRepository(locale, file);
  }

  public static FileMessageRepository from(final File file) {
    return new FileMessageRepository(null, file);
  }

  public static FunctionMessageRepository from(final Locale locale, final Function<String, String> function) {
    return new FunctionMessageRepository(locale, function);
  }

  public static SupplierMessageRepository from(final Locale locale, final Supplier<Map<String, String>> supplier) {
    return new SupplierMessageRepository(locale, supplier);
  }

  public static SupplierMessageRepository from(final Supplier<Map<String, String>> supplier) {
    return new SupplierMessageRepository(null, supplier);
  }

  public static TranslatorMessageRepository from(final Locale locale, final ITranslator translator) {
    return new TranslatorMessageRepository(locale, translator);
  }

  public static TranslatorMessageRepository from(final ITranslator translator) {
    return new TranslatorMessageRepository(null, translator);
  }

  public static URLMessageRepository from(final Locale locale, final URL url) {
    return new URLMessageRepository(locale, url);
  }

  public static URLMessageRepository from(final URL url) {
    return new URLMessageRepository(null, url);
  }

  public static URLMessageRepository from(final Locale locale, final URI uri) {
    return new URLMessageRepository(locale, uri);
  }

  public static URLMessageRepository from(final URI uri) {
    return new URLMessageRepository(null, uri);
  }

  @SuppressWarnings("unchecked")
  protected T getInstance() {
    return (T) this;
  }

  public Locale getLocale() {
    return locale;
  }

  public T setName(final String name) {
    if (name != null && !name.isEmpty()) {
      this.name = name;
    }
    return getInstance();
  }

  public String getName() {
    return this.name;
  }

  public T setVersion(final String version) {
    this.version = new Version(version);
    return getInstance();
  }

  public T setVersion(final Version version) {
    if (version != null) {
      this.version = version;
    }
    return getInstance();
  }

  public Version getVersion() {
    return version;
  }

  public T setProgress(final float progress) {
    this.progress = Math.max(Math.min(Math.round(progress), 0), 100);
    return getInstance();
  }

  public float getProgress() {
    return progress;
  }

  public T setDescription(final String description) {
    this.description = description;
    return getInstance();
  }

  public String getDescription() {
    return description;
  }

  public T setContributors(final List<String> contributors) {
    this.contributors = contributors;
    return getInstance();
  }

  public List<String> getContributors() {
    return this.contributors;
  }

  public String getMessage(final String key) {
    if (key == null || key.length() == 0) {
      return null;
    }
    return messages.get(key);
  }

  public T clear() {
    messages.clear();
    return getInstance();
  }

  public boolean isEmpty() {
    return messages == null || messages.isEmpty();
  }

  public boolean isLoaded() {
    return messages != null && !messages.isEmpty();
  }

  public long getLastUpdated() {
    return last_updated;
  }

  public T load() {
    return getInstance();
  }

  public T save() {
    return getInstance();
  }

  public boolean hasChanged() {
    return false;
  }

  @Override
  public T clone() {
    return getInstance().clone();
  }

  @Override
  public int hashCode() {
    int result = 31 + locale.hashCode();
    result = 31 * result + ((name == null) ? 0 : name.hashCode());
    result = 31 * result + ((description == null) ? 0 : description.hashCode());
    result = 31 * result + Float.floatToIntBits(progress);
    result = 31 * result + ((contributors == null) ? 0 : contributors.hashCode());
    return 31 * result + version.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final MessageRepository<?> other = (MessageRepository<?>) obj;
    if (name == null && other.name != null || version == null && other.version != null
        || description == null && other.description != null || contributors == null && other.contributors != null) {
      return false;
    }
    return Float.floatToIntBits(progress) != Float.floatToIntBits(other.progress)
        && locale.equals(other.locale)
        && version.equals(other.version)
        && name.equals(other.name);
  }

  @Override
  public String toString() {
    return "MessageRepository [name=" + name + ", locale=" + locale + ", description=" + description
        + ", progress=" + progress + ", version=" + version + ", contributors=" + contributors + "]";
  }

  protected Properties createProperties() {
    final Properties props = new Properties();
    props.setProperty("meta.name", name);
    props.setProperty("meta.locale", locale.toLanguageTag());
    props.setProperty("meta.version", version.toString());
    props.setProperty("meta.progress", Float.toString(progress));
    props.setProperty("meta.description", description);
    props.setProperty("meta.contributors", String.join(",", contributors));
    return props;
  }

  protected void load(final Properties props) {
    loadMeta(props.getProperty("meta.name"), props.getProperty("meta.locale"),
        props.getProperty("meta.version"), props.getProperty("meta.description"), props.getProperty("meta.progress"),
        props.getProperty("meta.contributors"));
    if (messages == null) {
      messages = new HashMap<>();
    } else {
      messages.clear();
    }
    for (final String key : props.stringPropertyNames()) {
      if (key.startsWith("meta.")) {
        continue;
      }
      messages.put(key, props.getProperty(key));
    }
  }

  protected void loadMeta(final String name, final String locale, final String version,
      final String description, final String progress,
      final String contributors) {
    final Locale parsedLocale = StringUtils.parseLocale(locale);
    if (parsedLocale == null || !this.locale.equals(parsedLocale)) {
      throw new IllegalArgumentException(
          "Locale mismatch when loading messages: expected " + this.locale + " but got " + parsedLocale);
    }
    if (name != null) {
      this.name = name;
    }
    if (version != null) {
      this.version = new Version(version);
    }
    if (description != null) {
      this.description = description;
    }
    if (progress != null) {
      this.progress = Float.parseFloat(progress);
    }
    if (contributors != null) {
      this.contributors = List.of(contributors.split(","));
    }
  }

  public static class StaticMessageRepository extends MessageRepository<StaticMessageRepository> {

    public StaticMessageRepository(final Locale locale, final Map<String, String> messages) {
      super(locale, null);
      this.messages = messages;
    }

    public StaticMessageRepository(final Locale locale, final Properties props) {
      super(locale == null ? props.getProperty("meta.locale") : locale.toString());
      loadMeta(props.getProperty("meta.name"), props.getProperty("meta.locale"),
          props.getProperty("meta.version"), props.getProperty("meta.description"), props.getProperty("meta.progress"),
          props.getProperty("meta.contributors"));
      if (messages == null) {
        messages = new HashMap<>();
      } else {
        messages.clear();
      }
      for (final String key : props.stringPropertyNames()) {
        messages.put(key, props.getProperty(key));
      }
    }

    public StaticMessageRepository(final Locale locale, final ResourceBundle bundle) {
      super(locale == null ? bundle.getString("meta.locale") : locale.toString());
      loadMeta(bundle.getString("meta.name"), bundle.getString("meta.locale"),
          bundle.getString("meta.version"), bundle.getString("meta.description"), bundle.getString("meta.progress"),
          bundle.getString("meta.contributors"));
      if (messages == null) {
        messages = new HashMap<>();
      } else {
        messages.clear();
      }
      for (final String key : bundle.keySet()) {
        if (key.startsWith("meta.")) {
          continue;
        }
        messages.put(key, ResourceUtils.getResourceBundleString(bundle, key, null));
      }
    }

    public StaticMessageRepository(final StaticMessageRepository repo) {
      super(repo);
    }

    @Override
    protected StaticMessageRepository getInstance() {
      return this;
    }
  }

  public static class FileMessageRepository extends MessageRepository<FileMessageRepository> {
    public static final String[] resourceFolders = { "translations", "translation", "lang", "languages", "language",
        "messages" };
    private final Path path;
    private final File file;
    private boolean hasDefault;

    public FileMessageRepository(final String file) {
      this(Path.of(file));
    }

    public FileMessageRepository(final Locale locale, final Path path) {
      super(locale == null ? getLocale(path) : locale, path.getFileName().toString());
      this.file = path.toFile();
      this.path = path;
      this.hasDefault = ResourceUtils.hasResource(path.toString());
    }

    public FileMessageRepository(final Path path) {
      super(getLocale(path), path.getFileName().toString());
      this.file = path.toFile();
      this.path = path;
      this.hasDefault = ResourceUtils.hasResource(path.toString());
    }

    public FileMessageRepository(final Locale locale, final File file) {
      super(locale == null ? getLocale(file) : locale, file.getName());
      this.file = file;
      this.path = file.toPath();
      this.hasDefault = ResourceUtils.hasResource(path.toString());
    }

    public FileMessageRepository(final File file) {
      super(getLocale(file), file.getName());
      this.file = file;
      this.path = file.toPath();
      this.hasDefault = ResourceUtils.hasResource(path.toString());
    }

    public FileMessageRepository(final FileMessageRepository repo) {
      super(repo);
      this.file = repo.file;
      this.path = repo.path;
      this.hasDefault = repo.hasDefault;
    }

    @Override
    protected FileMessageRepository getInstance() {
      return this;
    }

    public File getFile() {
      return file;
    }

    public Path getPath() {
      return path;
    }

    private static Locale getLocale(final File file) {
      return file == null ? null : StringUtils.parseLocale(file.getName().replace("lang_", ""));
    }

    private static Locale getLocale(final Path path) {
      return path == null ? null : StringUtils.parseLocale(path.getFileName().toString().replace("lang_", ""));
    }

    @Override
    public boolean hasChanged() {
      final BasicFileAttributes attr = FileUtils.stats(path).get();
      return attr == null || attr.lastModifiedTime().toMillis() != last_updated;
    }

    @Override
    public int hashCode() {
      final int result = 31 * super.hashCode() + path.hashCode();
      return 31 * result + file.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || !super.equals(obj) || getClass() != obj.getClass()) {
        return false;
      }
      final FileMessageRepository other = (FileMessageRepository) obj;
      return path.equals(other.path) && file.equals(other.file);
    }

    @Override
    public FileMessageRepository load() {
      if (FileUtils.notExists(path)) {
        hasDefault = ResourceUtils.hasResource(path.toString());
        if (!hasDefault) {
          Logger.warn("Message file does not exist and no default is available: " + file);
          return this;
        } else {
          createDefault();
        }
      }

      final Properties props = new Properties();
      try (BufferedReader in = FileUtils.createReader(file)) {
        props.load(in);
        load(props);
        FileUtils.stats(path).ifPresent(a -> last_updated = a.lastModifiedTime().toMillis());
      } catch (final Exception e) {
        Logger.warn("Failed to load messages from file: " + file);
        e.printStackTrace();
      }
      return this;
    }

    @Override
    public FileMessageRepository save() {
      FileUtils.mkdirs(file).throwIfFailed();
      try (BufferedWriter writer = FileUtils.createWriter(file)) {
        createProperties().store(writer, null);
        last_updated = System.currentTimeMillis();
      } catch (final Exception e) {
        Logger.warn("Failed to save messages to file: " + file);
        e.printStackTrace();
      }
      return this;
    }

    public FileMessageRepository save(final ResourceBundle bundle) {
      FileUtils.mkdirs(file).throwIfFailed();
      try (BufferedWriter writer = FileUtils.createWriter(file)) {
        last_updated = System.currentTimeMillis();
      } catch (final Exception e) {
        Logger.warn("Failed to save messages to file: " + file);
        e.printStackTrace();
      }
      return this;
    }

    public FileMessageRepository createDefault() {
      final File resource = ResourceUtils.getResourceFile(path.toString());
      if (resource == null) {
        Logger.warn("No default message resource found for: " + file);
        return this;
      }
      try {
        ResourceUtils.saveResource(file, resource);
      } catch (final Exception e) {
      }
      return this;
    }
  }

  public static class SupplierMessageRepository extends MessageRepository<SupplierMessageRepository> {
    private final Supplier<Map<String, String>> supplier;
    private final boolean isDynamic;

    public SupplierMessageRepository(final Locale locale, final Supplier<Map<String, String>> supplier,
        final boolean isDynamic) {
      super(locale, null);
      if (supplier == null) {
        throw new IllegalArgumentException("Supplier cannot be null for SupplierMessageRepository!");
      }
      this.supplier = supplier;
      this.isDynamic = isDynamic;
    }

    public SupplierMessageRepository(final Locale locale, final Supplier<Map<String, String>> supplier) {
      this(locale, supplier, false);
    }

    public SupplierMessageRepository(final SupplierMessageRepository repo) {
      super(repo);
      this.supplier = repo.supplier;
      this.isDynamic = repo.isDynamic;
    }

    @Override
    protected SupplierMessageRepository getInstance() {
      return this;
    }

    public Supplier<Map<String, String>> getSupplier() {
      return supplier;
    }

    @Override
    public boolean hasChanged() {
      return isDynamic;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + supplier.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || !super.equals(obj) || getClass() != obj.getClass()) {
        return false;
      }
      return supplier.equals(((SupplierMessageRepository) obj).supplier);
    }

    @Override
    public SupplierMessageRepository load() {
      this.messages = supplier.get();
      loadMeta(messages.get("meta.name"), messages.get("meta.locale"),
          messages.get("meta.version"),
          messages.get("meta.description"), messages.get("meta.progress"), messages.get("meta.contributors"));
      last_updated = System.currentTimeMillis();
      return this;
    }
  }

  public static class FunctionMessageRepository extends MessageRepository<FunctionMessageRepository> {
    private final Function<String, String> function;

    public FunctionMessageRepository(final Locale locale, final Function<String, String> function) {
      super(locale, null);
      this.function = function;
    }

    public FunctionMessageRepository(final FunctionMessageRepository repo) {
      super(repo);
      this.function = repo.function;
    }

    @Override
    public String getMessage(final String key) {
      if (key == null || key.isEmpty()) {
        return null;
      }
      return function.apply(key);
    }

    @Override
    public FunctionMessageRepository load() {
      loadMeta(function.apply("meta.name"), locale.toString(),
          function.apply("meta.version"),
          function.apply("meta.description"), function.apply("meta.progress"), function.apply("meta.contributors"));
      return this;
    }

    @Override
    protected FunctionMessageRepository getInstance() {
      return this;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + function.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || !super.equals(obj) || getClass() != obj.getClass()) {
        return false;
      }
      return function.equals(((FunctionMessageRepository) obj).function);
    }
  }

  public static class TranslatorMessageRepository extends MessageRepository<TranslatorMessageRepository> {
    private final ITranslator translator;

    public TranslatorMessageRepository(final Locale locale, final ITranslator translator) {
      super(locale == null ? translator.providesLocale() : locale, translator.name().getKey());
      this.translator = translator;
    }

    public TranslatorMessageRepository(final ITranslator translator) {
      super(translator.providesLocale(), translator.name().getKey());
      this.translator = translator;
    }

    public TranslatorMessageRepository(final TranslatorMessageRepository repo) {
      super(repo);
      this.translator = repo.translator;
    }

    @Override
    public String getMessage(final String key) {
      if (key == null || key.isEmpty()) {
        return null;
      }
      return translator.translate(key, locale);
    }

    @Override
    protected TranslatorMessageRepository getInstance() {
      return this;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + translator.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || !super.equals(obj) || getClass() != obj.getClass()) {
        return false;
      }
      return translator.equals(((TranslatorMessageRepository) obj).translator);
    }
  }

  public static class URLMessageRepository extends MessageRepository<URLMessageRepository> {
    private final URL url;

    public URLMessageRepository(final Locale locale, final URI uri) {
      super(locale, null);
      if (uri == null) {
        throw new IllegalArgumentException("Invalid URI: " + uri);
      }
      try {
        this.url = uri.toURL();
      } catch (final Exception e) {
        throw new IllegalArgumentException("Invalid URL: " + uri, e);
      }
    }

    public URLMessageRepository(final Locale locale, final String url) {
      super(locale, null);
      if (url == null || url.isEmpty()) {
        throw new IllegalArgumentException("Invalid URL: " + url);
      }
      try {
        this.url = new URI(url).toURL();
      } catch (final Exception e) {
        throw new IllegalArgumentException("Invalid URL: " + url, e);
      }
    }

    public URLMessageRepository(final Locale locale, final URL url) {
      super(locale, null);
      if (url == null) {
        throw new IllegalArgumentException("Invalid URL: " + url);
      }
      this.url = url;
    }

    public URLMessageRepository(final URLMessageRepository repo) {
      super(repo);
      this.url = repo.url;
    }

    @Override
    protected URLMessageRepository getInstance() {
      return this;
    }

    public URL getUrl() {
      return url;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + url.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || !super.equals(obj) || getClass() != obj.getClass()) {
        return false;
      }
      return url.equals(((URLMessageRepository) obj).url);
    }

    @Override
    public URLMessageRepository load() {
      HttpURLConnection conn = null;
      try {
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        final int code = conn.getResponseCode();
        if (code < 200 || code >= 300) {
          Logger.warn("Failed to load messages from URL (HTTP " + code + "): " + url);
          return this;
        }

        final Properties props = new Properties();
        try (final BufferedReader reader = FileUtils.createReader(conn.getInputStream())) {
          props.load(reader);
          load(props);
          last_updated = System.currentTimeMillis();
          return this;
        }
      } catch (final Exception e) {
        Logger.warn("Failed to load messages from URL: " + url);
        e.printStackTrace();
        return this;
      } finally {
        if (conn != null) {
          conn.disconnect();
          conn = null;
        }
      }
    }

    public URLMessageRepository save(final Properties props) {
      HttpURLConnection conn = null;
      try {
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setDoOutput(true);
        // Try PUT first; many endpoints accept PUT for replacing resources.
        try {
          conn.setRequestMethod("PUT");
        } catch (final ProtocolException ex) {
          // fallback to POST if PUT not allowed by handler
          conn.setRequestMethod("POST");
        }
        conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

        try (final BufferedWriter writer = FileUtils.createWriter(conn.getOutputStream())) {
          createProperties().store(writer, null);
        }

        final int code = conn.getResponseCode();
        if (code < 200 || code >= 300) {
          Logger.warn("Failed to save messages to URL (HTTP " + code + "): " + url);
          return this;
        }
      } catch (final Exception e) {
        Logger.warn("Failed to save messages to URL: " + url);
        e.printStackTrace();
      } finally {
        if (conn != null) {
          conn.disconnect();
          conn = null;
        }
      }
      return this;
    }
  }
}
