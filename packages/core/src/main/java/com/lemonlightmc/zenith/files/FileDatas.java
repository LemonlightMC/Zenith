package com.lemonlightmc.zenith.files;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class FileDatas {

  public static String getFileExtension(final Path path) {
    return path == null ? "" : getFileExtension(path.getFileName().toString());
  }

  public static String getFileExtension(final File file) {
    return file == null ? "" : getFileExtension(file.getName());
  }

  public static String getFileExtension(final String str) {
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
    } catch (final Exception e) {
      return Optional.empty();
    }
  }

  public static Optional<BasicFileAttributes> stats(final Path path, final LinkOption... options) {
    try {
      return Optional
          .of(Files.readAttributes(path, BasicFileAttributes.class, options == null ? new LinkOption[] {} : options));
    } catch (final Exception e) {
      return Optional.empty();
    }
  }

  public static Optional<BasicFileAttributes> stats(final File file) {
    try {
      return Optional.of(Files.readAttributes(file.toPath(), BasicFileAttributes.class, new LinkOption[] {}));
    } catch (final Exception e) {
      return Optional.empty();
    }
  }

  public static Optional<BasicFileAttributes> stats(final File file, final LinkOption... options) {
    try {
      return Optional.of(Files.readAttributes(file.toPath(), BasicFileAttributes.class,
          options == null ? new LinkOption[] {} : options));
    } catch (final Exception e) {
      return Optional.empty();
    }
  }

  // list
  public static List<File> listFiles(final Path directory) {
    return directory == null ? List.of() : listFiles(directory.toFile());
  }

  public static List<File> listFiles(final File directory) {
    if (directory == null || !directory.isDirectory() || !directory.exists()) {
      return List.of();
    }
    final File[] listFiles = directory.listFiles();
    return listFiles == null ? List.of() : List.of(listFiles);
  }

  public static List<File> listFiles(final Path directory, final FileFilter filter) {
    return directory == null ? List.of() : listFiles(directory.toFile());
  }

  public static List<File> listFiles(final File directory, final FileFilter filter) {
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
    final List<File> filteredFiles = new ArrayList<>();
    for (final File file : filteredFiles) {
      if (filter.accept(file)) {
        filteredFiles.add(file);
      }
    }
    return filteredFiles;
  }

  public static List<File> listFiles(final Path directory, final boolean recursive) {
    return directory == null ? List.of() : listFiles(directory.toFile());
  }

  public static List<File> listFiles(final File directory, final boolean recursive) {
    return listFiles(directory, null, recursive);
  }

  public static List<File> listFiles(final Path directory, final FileFilter filter, final boolean recursive) {
    return directory == null ? List.of() : listFiles(directory.toFile());
  }

  public static List<File> listFiles(final File directory, final FileFilter filter, final boolean recursive) {
    if (directory == null || !directory.isDirectory() || !directory.exists()) {
      return List.of();
    }
    final File[] listFiles = directory.listFiles();
    if (listFiles == null) {
      return List.of();
    }
    final List<File> files = new ArrayList<>();
    for (final File file : listFiles) {
      if (filter != null && !filter.accept(file)) {
        continue;
      }
      files.add(file);
      if (recursive && file.isDirectory()) {
        files.addAll(listFiles(directory, filter, recursive));
      }
    }
    return files;
  }
}
