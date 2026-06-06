package com.lemonlightmc.zenith.files;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

class FileDatas {

  // exists
  public static boolean exists(final Path path) {
    try {
      return Files.exists(path);
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean exists(final File file) {
    try {
      return Files.exists(file.toPath());
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean notExists(final Path path) {
    try {
      return Files.notExists(path);
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean notExists(final File file) {
    try {
      return Files.notExists(file.toPath());
    } catch (final Exception e) {
      return false;
    }
  }

  // is
  public static boolean isFile(final Path path) {
    try {
      return Files.isRegularFile(path);
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isFile(final File file) {
    try {
      return Files.isRegularFile(file.toPath());
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isDirectory(final Path path) {
    try {
      return Files.isDirectory(path);
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isDirectory(final File file) {
    try {
      return Files.isDirectory(file.toPath());
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isSymbolicLink(final Path path) {
    try {
      return Files.isSymbolicLink(path);
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isSymbolicLink(final File file) {
    try {
      return Files.isSymbolicLink(file.toPath());
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isExecutable(final Path path) {
    try {
      return Files.isExecutable(path);
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isExecutable(final File file) {
    try {
      return Files.isExecutable(file.toPath());
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isHidden(final Path path) {
    try {
      return Files.isHidden(path);
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isHidden(final File file) {
    try {
      return Files.isHidden(file.toPath());
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isRegularFile(final Path path) {
    try {
      return Files.isRegularFile(path);
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isRegularFile(final File file) {
    try {
      return Files.isRegularFile(file.toPath());
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isReadable(final Path path) {
    try {
      return Files.isReadable(path);
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isReadable(final File file) {
    try {
      return Files.isReadable(file.toPath());
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isWriteable(final Path path) {
    try {
      return Files.isWritable(path);
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isWriteable(final File file) {
    try {
      return Files.isWritable(file.toPath());
    } catch (final Exception e) {
      return false;
    }
  }

  // stats
  public static Optional<BasicFileAttributes> stats(final Path path) {
    try {
      return Optional.of(Files.readAttributes(path, BasicFileAttributes.class, new LinkOption[] {}));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static Optional<BasicFileAttributes> stats(final Path path, LinkOption... options) {
    try {
      return Optional
          .of(Files.readAttributes(path, BasicFileAttributes.class, options == null ? new LinkOption[] {} : options));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static Optional<BasicFileAttributes> stats(final File file) {
    try {
      return Optional.of(Files.readAttributes(file.toPath(), BasicFileAttributes.class, new LinkOption[] {}));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static Optional<BasicFileAttributes> stats(final File file, LinkOption... options) {
    try {
      return Optional.of(Files.readAttributes(file.toPath(), BasicFileAttributes.class,
          options == null ? new LinkOption[] {} : options));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  // list
  public static List<File> listFiles(final Path directory) {
    return directory == null ? List.of() : listFiles(directory.toFile());
  }

  public static List<File> listFiles(final Path directory, final Predicate<File> filter) {
    return directory == null ? List.of() : listFiles(directory.toFile(), filter);
  }

  public static List<File> listFiles(final Path directory, final FilenameFilter filter) {
    if (directory == null) {
      return List.of();
    }
    if (filter == null) {
      return listFiles(directory.toFile(), (Predicate<File>) null);
    }
    return listFiles(directory.toFile(), (f) -> filter.accept(f, f.getName()));
  }

  public static List<File> listFiles(final Path directory, final Collection<String> extensions) {
    if (extensions == null || extensions.isEmpty()) {
      return directory == null ? List.of() : listFiles(directory, (Predicate<File>) null);
    }
    return directory == null ? List.of()
        : listFiles(directory.toFile(), (f) -> extensions.contains(getExtension(f.getName())));
  }

  public static List<File> listFiles(final File directory) {
    return listFiles(directory, (Predicate<File>) null);
  }

  public static List<File> listFiles(final File directory, final FilenameFilter filter) {
    if (filter == null) {
      return listFiles(directory, (Predicate<File>) null);
    }
    return listFiles(directory, (f) -> filter.accept(f, f.getName()));
  }

  public static List<File> listFiles(final File directory, final Predicate<File> filter) {
    if (directory == null || !directory.isDirectory() || !directory.exists()) {
      return List.of();
    }
    final File[] listFiles = directory.listFiles();
    if (listFiles == null) {
      return List.of();
    }
    if (filter == null) {
      return List.of(listFiles);
    }
    List<File> filteredFiles = new ArrayList<>();
    for (File file : filteredFiles) {
      if (filter.test(file)) {
        filteredFiles.add(file);
      }
    }
    return filteredFiles;
  }

  public static List<File> listFiles(final File directory, final FilenameFilter filter, final boolean recursive) {
    if (filter == null) {
      return listFiles(directory, (Predicate<File>) null, recursive);
    }
    return listFiles(directory, (f) -> filter.accept(f, f.getName()), recursive);
  }

  public static List<File> listFiles(final File directory, final Collection<String> extensions,
      final boolean recursive) {
    if (extensions == null || extensions.isEmpty()) {
      return listFiles(directory, (Predicate<File>) null, recursive);
    }
    return listFiles(directory, (f) -> extensions.contains(getExtension(f.getName())), recursive);
  }

  public static List<File> listFiles(final File directory, final Predicate<File> filter, final boolean recursive) {
    if (directory == null || !directory.isDirectory() || !directory.exists()) {
      return List.of();
    }
    final File[] listFiles = directory.listFiles();
    if (listFiles == null) {
      return List.of();
    }
    final List<File> files = new ArrayList<>();
    for (final File file : listFiles) {
      if (filter != null && !filter.test(file)) {
        continue;
      }
      files.add(file);
      if (recursive && file.isDirectory()) {
        files.addAll(listFiles(directory, filter, recursive));
      }
    }
    return files;
  }

  private static String getExtension(final String str) {
    if (str == null) {
      return null;
    }
    final int extensionPos = str.lastIndexOf('.');
    final int index = Math.max(str.lastIndexOf('/'), str.lastIndexOf('\\')) > extensionPos ? -1 : extensionPos;
    if (index == -1) {
      return "";
    }
    return str.substring(index + 1);
  }
}
