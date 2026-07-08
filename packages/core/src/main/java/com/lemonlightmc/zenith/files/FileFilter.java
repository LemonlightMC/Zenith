package com.lemonlightmc.zenith.files;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class FileFilter implements java.io.FileFilter, PathMatcher {

  public boolean accept(final File file) {
    return matches(file.toPath());
  }

  public boolean matches(final Path file) {
    return true;
  }

  public static FileFilter glob(final String input) {
    final Pattern pattern = Pattern.compile(Globs.toRegexPattern(input, true),
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return pattern.matcher(path.toAbsolutePath().toString()).matches();
      }
    };
  }

  public static FileFilter regex(final String input) {
    final Pattern pattern = Pattern.compile(input, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return pattern.matcher(path.toAbsolutePath().toString()).matches();
      }
    };
  }

  public static FileFilter extensions(final List<String> exts) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return exts.contains(FileUtils.getFileExtension(path));
      }
    };
  }

  public static FileFilter extensions(final String... exts) {
    return extensions(List.of(exts));
  }

  public static FileFilter names(final List<String> names) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return names.contains(path.getFileName().toString());
      }
    };
  }

  public static FileFilter names(final String... names) {
    return names(List.of(names));
  }

  public static FileFilter isDirectory() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return path.toFile().isDirectory();
      }
    };
  }

  public static FileFilter isFile() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return path.toFile().isFile();
      }
    };
  }

  public static FileFilter isHidden() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return path.toFile().isHidden();
      }
    };
  }

  public static FileFilter isReadable() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return path.toFile().canRead();
      }
    };
  }

  public static FileFilter isWritable() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return path.toFile().canWrite();
      }
    };
  }

  public static FileFilter predicate(final Predicate<Path> predicate) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return predicate.test(path);
      }
    };
  }

  public static FileFilter filenameFilter(final FilenameFilter filter) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return filter.accept(path.getParent().toFile(), path.getFileName().toString());
      }
    };
  }

  public static FileFilter fileFilter(final java.io.FileFilter filter) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return filter.accept(path.toFile());
      }
    };
  }

  public static FileFilter pathMatcher(final PathMatcher matcher) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return matcher.matches(path);
      }
    };
  }

  public static FileFilter all() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return true;
      }
    };
  }

  public static FileFilter none() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return false;
      }
    };
  }

  public static FileFilter not(final FileFilter filter) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return !filter.matches(path);
      }
    };
  }

  public static FileFilter and(final FileFilter... filters) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        for (final FileFilter filter : filters) {
          if (!filter.matches(path)) {
            return false;
          }
        }
        return true;
      }
    };
  }

  public static FileFilter and(final FileFilter filter1, final FileFilter filter2) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return filter1.matches(path) && filter2.matches(path);
      }
    };
  }

  public static FileFilter or(final FileFilter... filters) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        for (final FileFilter filter : filters) {
          if (filter.matches(path)) {
            return true;
          }
        }
        return false;
      }
    };
  }

  public static FileFilter or(final FileFilter filter1, final FileFilter filter2) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return filter1.matches(path) || filter2.matches(path);
      }
    };
  }
}