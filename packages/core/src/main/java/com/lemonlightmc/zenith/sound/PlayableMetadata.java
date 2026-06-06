package com.lemonlightmc.zenith.sound;

import java.io.File;

public class PlayableMetadata {

  private String title = "";
  private String author = "";
  private String description = "";
  private boolean isLoop = false;
  private File sourceFile = null;

  public PlayableMetadata() {
  }

  public PlayableMetadata(final PlayableMetadata metadata) {
    title = metadata.title;
    author = metadata.author;
    description = metadata.description;
    isLoop = metadata.isLoop;
    sourceFile = metadata.sourceFile;
  }

  public PlayableMetadata setTitle(final String title) {
    this.title = title;
    return this;
  }

  public PlayableMetadata setAuthor(final String author) {
    this.author = author;
    return this;
  }

  public PlayableMetadata setDescription(final String description) {
    this.description = description;
    return this;
  }

  public PlayableMetadata setLoop(final boolean isLoop) {
    this.isLoop = isLoop;
    return this;
  }

  public PlayableMetadata setSourceFile(final File sourceFile) {
    this.sourceFile = sourceFile;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public String getDescription() {
    return description;
  }

  public boolean isLoop() {
    return isLoop;
  }

  public File getSourceFile() {
    return sourceFile;
  }
}