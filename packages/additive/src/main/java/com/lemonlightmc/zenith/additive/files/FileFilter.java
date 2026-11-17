package com.lemonlightmc.zenith.additive.files;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A composable file filter that can be used as a {@link java.io.FileFilter} or {@link PathMatcher}.
 */
public abstract class FileFilter implements java.io.FileFilter, PathMatcher {

  /**
   * Tests whether the given file should be accepted.
   *
   * @param file the file to test
   * @return {@code true} if the file matches this filter
   */
  public boolean accept(final File file) {
    return matches(file.toPath());
  }

  /**
   * Tests whether the given path should be accepted.
   *
   * @param file the path to test
   * @return {@code true} if the path matches this filter
   */
  public boolean matches(final Path file) {
    return true;
  }

  /**
   * Creates a filter that matches paths against a glob pattern.
   *
   * @param input the glob pattern to match
   * @return a filter matching the given glob pattern
   */
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

  /**
   * Creates a filter that matches paths against a regular expression.
   *
   * @param input the regular expression to match
   * @return a filter matching the given regular expression
   */
  public static FileFilter regex(final String input) {
    final Pattern pattern = Pattern.compile(input, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return pattern.matcher(path.toAbsolutePath().toString()).matches();
      }
    };
  }

  /**
   * Creates a filter that accepts files with any of the given extensions.
   *
   * @param exts the allowed file extensions
   * @return a filter matching the given extensions
   */
  public static FileFilter extensions(final List<String> exts) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return exts.contains(FileUtils.getFileExtension(path));
      }
    };
  }

  /**
   * Creates a filter that accepts files with any of the given extensions.
   *
   * @param exts the allowed file extensions
   * @return a filter matching the given extensions
   */
  public static FileFilter extensions(final String... exts) {
    return extensions(List.of(exts));
  }

  /**
   * Creates a filter that accepts files whose names match any of the given values.
   *
   * @param names the allowed file names
   * @return a filter matching the given file names
   */
  public static FileFilter names(final List<String> names) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return names.contains(path.getFileName().toString());
      }
    };
  }

  /**
   * Creates a filter that accepts files whose names match any of the given values.
   *
   * @param names the allowed file names
   * @return a filter matching the given file names
   */
  public static FileFilter names(final String... names) {
    return names(List.of(names));
  }

  /**
   * Creates a filter that accepts directories.
   *
   * @return a directory filter
   */
  public static FileFilter isDirectory() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return path.toFile().isDirectory();
      }
    };
  }

  /**
   * Creates a filter that accepts regular files.
   *
   * @return a file filter
   */
  public static FileFilter isFile() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return path.toFile().isFile();
      }
    };
  }

  /**
   * Creates a filter that accepts hidden files.
   *
   * @return a hidden-file filter
   */
  public static FileFilter isHidden() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return path.toFile().isHidden();
      }
    };
  }

  /**
   * Creates a filter that accepts readable files.
   *
   * @return a readable-file filter
   */
  public static FileFilter isReadable() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return path.toFile().canRead();
      }
    };
  }

  /**
   * Creates a filter that accepts writable files.
   *
   * @return a writable-file filter
   */
  public static FileFilter isWritable() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return path.toFile().canWrite();
      }
    };
  }

  /**
   * Creates a filter from a path predicate.
   *
   * @param predicate the predicate used to test paths
   * @return a filter backed by the given predicate
   */
  public static FileFilter predicate(final Predicate<Path> predicate) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return predicate.test(path);
      }
    };
  }

  /**
   * Creates a filter from a {@link FilenameFilter}.
   *
   * @param filter the filename filter to adapt
   * @return a filter backed by the given filename filter
   */
  public static FileFilter filenameFilter(final FilenameFilter filter) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return filter.accept(path.getParent().toFile(), path.getFileName().toString());
      }
    };
  }

  /**
   * Creates a filter from a {@link java.io.FileFilter}.
   *
   * @param filter the file filter to adapt
   * @return a filter backed by the given file filter
   */
  public static FileFilter fileFilter(final java.io.FileFilter filter) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return filter.accept(path.toFile());
      }
    };
  }

  /**
   * Creates a filter from a {@link PathMatcher}.
   *
   * @param matcher the path matcher to adapt
   * @return a filter backed by the given path matcher
   */
  public static FileFilter pathMatcher(final PathMatcher matcher) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return matcher.matches(path);
      }
    };
  }

  /**
   * Creates a filter that accepts all paths.
   *
   * @return a filter that always returns {@code true}
   */
  public static FileFilter all() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return true;
      }
    };
  }

  /**
   * Creates a filter that rejects all paths.
   *
   * @return a filter that always returns {@code false}
   */
  public static FileFilter none() {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return false;
      }
    };
  }

  /**
   * Creates a filter that negates another filter.
   *
   * @param filter the filter to negate
   * @return a negated filter
   */
  public static FileFilter not(final FileFilter filter) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return !filter.matches(path);
      }
    };
  }

  /**
   * Creates a filter that requires all provided filters to match.
   *
   * @param filters the filters to combine
   * @return a combined filter that matches only when every filter matches
   */
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

  /**
   * Creates a filter that requires both provided filters to match.
   *
   * @param filter1 the first filter
   * @param filter2 the second filter
   * @return a combined filter that matches only when both filters match
   */
  public static FileFilter and(final FileFilter filter1, final FileFilter filter2) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return filter1.matches(path) && filter2.matches(path);
      }
    };
  }

  /**
   * Creates a filter that matches when any provided filter matches.
   *
   * @param filters the filters to combine
   * @return a combined filter that matches when at least one filter matches
   */
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

  /**
   * Creates a filter that matches when either provided filter matches.
   *
   * @param filter1 the first filter
   * @param filter2 the second filter
   * @return a combined filter that matches when either filter matches
   */
  public static FileFilter or(final FileFilter filter1, final FileFilter filter2) {
    return new FileFilter() {
      @Override
      public boolean matches(final Path path) {
        return filter1.matches(path) || filter2.matches(path);
      }
    };
  }
}