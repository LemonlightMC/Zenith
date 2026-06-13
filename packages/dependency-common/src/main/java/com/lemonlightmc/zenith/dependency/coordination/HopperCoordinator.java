package com.lemonlightmc.zenith.dependency.coordination;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;

import com.lemonlightmc.zenith.files.FileUtils;

/**
 * Coordinates multiple DependencyAPI instances (from different plugins) via filesystem
 * locks.
 * <p>
 * This ensures that only one plugin at a time can:
 * <ul>
 * <li>Read/write registry.json</li>
 * <li>Read/write hopper.lock</li>
 * <li>Download files</li>
 * </ul>
 */
public final class HopperCoordinator implements Closeable {

  private static final String LOCK_FILE = ".coordination.lock";
  private static final String REGISTRY_FILE = "registry.json";
  private static final String LOCKFILE_FILE = "hopper.lock";

  private final Path coordinationDir;
  private final RandomAccessFile lockRaf;
  private final FileChannel lockChannel;
  private final FileLock lock;

  private HopperCoordinator(final Path coordinationDir, final RandomAccessFile lockRaf,
      final FileChannel lockChannel, final FileLock lock) {
    this.coordinationDir = coordinationDir;
    this.lockRaf = lockRaf;
    this.lockChannel = lockChannel;
    this.lock = lock;
  }

  /**
   * Acquire the coordination lock. Blocks until the lock is available.
   *
   * @param coordinationDir the .hopper directory
   * @return the coordinator (must be closed when done)
   * @throws IOException
   */
  public static HopperCoordinator acquire(final Path coordinationDir) throws IOException {
    FileUtils.mkdirs(coordinationDir);

    final RandomAccessFile raf = new RandomAccessFile(coordinationDir.resolve(LOCK_FILE).toFile(), "rw");
    final FileChannel channel = raf.getChannel();

    // Blocking lock - waits until lock is available
    final FileLock lock = channel.lock();
    return new HopperCoordinator(coordinationDir, raf, channel, lock);
  }

  /**
   * Load the registry (creates empty one if doesn't exist).
   */
  public LibraryRegistry loadRegistry() throws IOException {
    final Path registryPath = coordinationDir.resolve(REGISTRY_FILE);
    if (FileUtils.exists(registryPath)) {
      return LibraryRegistry.fromJson(FileUtils.readString(registryPath));
    }
    return new LibraryRegistry();
  }

  /**
   * Save the registry.
   */
  public void saveRegistry(final LibraryRegistry registry) throws IOException {
    FileUtils.write(coordinationDir.resolve(REGISTRY_FILE), registry.toJson());
  }

  /**
   * Load the lockfile (creates empty one if doesn't exist).
   */
  public Lockfile loadLockfile() throws IOException {
    final Path lockfilePath = coordinationDir.resolve(LOCKFILE_FILE);
    if (FileUtils.exists(lockfilePath)) {
      return Lockfile.fromJson(FileUtils.readString(lockfilePath));
    }
    return new Lockfile();
  }

  /**
   * Save the lockfile.
   */
  public void saveLockfile(final Lockfile lockfile) throws IOException {
    FileUtils.write(coordinationDir.resolve(LOCKFILE_FILE), lockfile.toJson());
  }

  @Override
  public void close() throws IOException {
    try {
      if (lock != null && lock.isValid()) {
        lock.release();
      }
    } finally {
      try {
        if (lockChannel != null && lockChannel.isOpen()) {
          lockChannel.close();
        }
      } finally {
        if (lockRaf != null) {
          lockRaf.close();
        }
      }
    }
  }
}
