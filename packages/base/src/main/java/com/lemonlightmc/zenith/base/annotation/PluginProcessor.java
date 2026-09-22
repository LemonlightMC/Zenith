package com.lemonlightmc.zenith.base.annotation;

import org.bukkit.plugin.PluginLoadOrder;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Processes the {@link Plugin} annotation and generates a plugin.yml file.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "com.lemonlightmc.zenith.base.annotation.PluginYmlCreator",
    "com.lemonlightmc.zenith.base.annotation.PluginDependency" })
public class PluginProcessor extends AbstractProcessor {

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
    final Set<? extends Element> annotatedElements = env.getElementsAnnotatedWith(PluginYmlCreator.class);
    if (annotatedElements.isEmpty()) {
      return false;
    }

    if (annotatedElements.size() > 1) {
      this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
          "More than one @PluginYmlCreator element found.");
      return false;
    }

    final Element element = annotatedElements.iterator().next();

    if (!(element instanceof TypeElement)) {
      this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
          "@Plugin element is not instance of TypeElement");
      return false;
    }

    final TypeElement type = ((TypeElement) element);
    final Map<String, Object> data = new LinkedHashMap<>();
    final PluginYmlCreator annotation = type.getAnnotation(PluginYmlCreator.class);

    data.put("name", annotation.name());

    final String version = annotation.version();
    if (!version.isEmpty()) {
      data.put("version", version);
    } else {
      data.put("version", new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date(System.currentTimeMillis())));
    }

    data.put("main", type.getQualifiedName().toString());

    final String description = annotation.description();
    if (!description.isEmpty()) {
      data.put("description", description);
    }

    final PluginLoadOrder order = annotation.load();
    if (order != PluginLoadOrder.POSTWORLD) {
      data.put("load", order.name());
    }

    final String apiVersion = annotation.apiVersion();
    if (!apiVersion.isEmpty()) {
      data.put("api-version", apiVersion);
    }

    final String[] authors = annotation.authors();
    if (authors.length == 1) {
      data.put("author", authors[0]);
    } else if (authors.length > 1) {
      data.put("authors", new ArrayList<>(Arrays.asList(authors)));
    }

    final String website = annotation.website();
    if (!website.isEmpty()) {
      data.put("website", website);
    }

    final PluginDependency[] depends = annotation.depends();
    final List<String> hard = new ArrayList<>();
    final List<String> soft = new ArrayList<>();

    for (final PluginDependency depend : depends) {
      if (depend.soft()) {
        soft.add(depend.value());
      } else {
        hard.add(depend.value());
      }
    }

    hard.addAll(Arrays.asList(annotation.hardDepends()));
    soft.addAll(Arrays.asList(annotation.softDepends()));

    if (!hard.isEmpty()) {
      data.put("depend", hard);
    }

    if (!soft.isEmpty()) {
      data.put("softdepend", soft);
    }

    final String[] loadBefore = annotation.loadBefore();
    if (loadBefore.length != 0) {
      data.put("loadbefore", new ArrayList<>(Arrays.asList(loadBefore)));
    }

    final String[] libraries = annotation.libraries();
    if (libraries.length != 0) {
      data.put("libraries", new ArrayList<>(Arrays.asList(libraries)));
    }

    try {
      final Yaml yaml = new Yaml();
      final FileObject resource = this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "",
          "plugin.yml");

      try (Writer writer = resource.openWriter(); BufferedWriter bw = new BufferedWriter(writer)) {
        yaml.dump(data, bw);
        bw.flush();
      }

      return true;
    } catch (final IOException e) {
      throw new RuntimeException("Cannot serialize plugin descriptor: " + e.getMessage(), e);
    }
  }

}