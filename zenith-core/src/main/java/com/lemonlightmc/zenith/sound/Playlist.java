package com.lemonlightmc.zenith.sound;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

  private List<Song> songs;

  public Playlist(final List<Song> songs) {
    if (check(songs)) {
      this.songs = new ArrayList<>();
    } else {
      this.songs = songs;
    }
  }

  public Playlist(final Song song) {
    if (song == null) {
      throw new IllegalArgumentException("Song Song cant be null");
    }
    this.songs = List.of(song);
  }

  public void add(final List<Song> songs) {
    if (check(songs)) {
      return;
    }
    this.songs.addAll(songs);
  }

  public void insert(final int index, final List<Song> songs) {
    if (check(songs)) {
      return;
    }
    if (index > this.songs.size()) {
      throw new IllegalArgumentException("Index is higher than playlist size");
    }
    this.songs.addAll(index, songs);
  }

  public void remove(final List<Song> songs) {
    if (check(songs)) {
      return;
    }
    final ArrayList<Song> songsTemp = new ArrayList<>(this.songs);
    songsTemp.removeAll(songs);
    if (songsTemp.size() > 0) {
      this.songs = songsTemp;
    } else {
      throw new IllegalArgumentException("Cannot remove all songs from playlist");
    }
  }

  public Song getPlayable(final int songNumber) {
    return songs.get(songNumber);
  }

  public int getCount() {
    return songs.size();
  }

  public boolean hasNext(final int songNumber) {
    return songs.size() > (songNumber + 1);
  }

  public boolean exist(final int songNumber) {
    return songs.size() > songNumber;
  }

  public int getIndex(final Song song) {
    return songs.indexOf(song);
  }

  public boolean contains(final Song song) {
    return songs.contains(song);
  }

  public List<Song> getSongs() {
    return new ArrayList<>(songs);
  }

  private static boolean check(final List<Song> songs) {
    if (songs == null) {
      throw new IllegalArgumentException("Song List cant be null");
    }
    if (songs.contains(null)) {
      throw new IllegalArgumentException("Cannot add null to playlist");
    }
    return songs.size() == 0;
  }
}