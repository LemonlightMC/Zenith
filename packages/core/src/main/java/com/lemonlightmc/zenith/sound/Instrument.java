package com.lemonlightmc.zenith.sound;

import com.lemonlightmc.zenith.interfaces.Builder;
import com.lemonlightmc.zenith.utils.MathUtils;

public class Instrument {

  public static final int DEFAULT_INSTRUMENT = NoteInstrument.HARP.getKey();

  protected final int key;
  protected final boolean isCustom;

  public Instrument(final int key, final boolean isCustom) {
    this.key = MathUtils.normalizeRangeOrThrow(key, Note.MINIMUM_NOTE, Note.MAXIMUM_NOTE, "Instrument Key");
    this.isCustom = isCustom;
  }

  public int getKey() {
    return key;
  }

  public boolean isCustom() {
    return isCustom;
  }

  @Override
  public int hashCode() {
    return 31 * (31 + key) + (isCustom ? 1231 : 1237);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    final Instrument other = (Instrument) obj;
    return key == other.key && isCustom == other.isCustom;
  }

  @Override
  public String toString() {
    return "Instrument [key=" + key + ", isCustom=" + isCustom + "]";
  }

  public static org.bukkit.Sound getInstrument(final byte instrument) {
    final org.bukkit.Sound sound = NoteSounds.getSoundByIndex(instrument);
    if (sound == null)
      throw new IllegalArgumentException("Instrument with index " + instrument + " is not available.");
    return sound;
  }

  public static org.bukkit.Sound getInstrument(final Note note) {
    final org.bukkit.Sound sound = NoteSounds.getSoundByIndex(note.getInstrument().getKey());
    if (sound == null)
      throw new IllegalArgumentException("Instrument with index " + note.getInstrument() + " is not available.");
    return sound;
  }

  @SuppressWarnings("deprecation")
  public static String getInstrumentName(final byte instrument) {
    final org.bukkit.Sound sound = NoteSounds.getSoundByIndex(instrument, NoteSounds.NOTE_PIANO);
    if (sound == null)
      throw new IllegalArgumentException("Instrument is not available in this server's version.");

    return sound.name();
  }

  public static boolean isCustomInstrument(final Instrument instrument) {
    return instrument.isCustom();
  }

  public static CustomInstrument getCustomInstrumentForNote(final Note note) {
    if (note.isCustomInstrument())
      return (CustomInstrument) note.getInstrument();
    else {
      final NoteSounds sound = NoteSounds.getByIndex(note.getInstrument().key);
      if (sound != null)
        return new CustomInstrument(sound.getInstrumentIndex(), sound.getResourcePackName(),
            sound.getResourcePackName());
    }
    return null;
  }

  public static String getCustomInstrumentFileName(final Note note) {
    if (note.isCustomInstrument()) {
      CustomInstrument instrument = ((CustomInstrument) note.getInstrument());
      return instrument == null ? null : instrument.getFileName();
    } else {
      final NoteSounds sound = NoteSounds.getByIndex(note.getInstrument().key);
      return sound == null ? null : sound.getResourcePackName();
    }
  }

  /**
   * Enumeration of Minecraft non-custom instruments
   */
  public static class NoteInstrument extends Instrument {
    /**
     * Block: Any than is not used by other instruments
     * Same as {@link #HARP}
     */
    public static final NoteInstrument PIANO = new NoteInstrument(0);

    /**
     * Block: Any than is not used by other instruments
     * Same as {@link #PIANO}
     */
    public static final NoteInstrument HARP = new NoteInstrument(0);

    /**
     * Block: Wood
     */
    public static final NoteInstrument BASS = new NoteInstrument(1);

    /**
     * Block: Stone
     */
    public static final NoteInstrument BASS_DRUM = new NoteInstrument(2);

    /**
     * Block: Sand
     */
    public static final NoteInstrument SNARE_DRUM = new NoteInstrument(3);

    /**
     * Block: Glass
     */
    public static final NoteInstrument CLICK = new NoteInstrument(4);

    /**
     * Block: Wool
     */
    public static final NoteInstrument GUITAR = new NoteInstrument(5);

    /**
     * Block: Clay
     */
    public static final NoteInstrument FLUTE = new NoteInstrument(6);

    /**
     * Block: Gold
     */
    public static final NoteInstrument BELL = new NoteInstrument(7);

    /**
     * Block: Packed Ice
     */
    public static final NoteInstrument CHIME = new NoteInstrument(8);

    /**
     * Block: Bone
     */
    public static final NoteInstrument XYLOPHONE = new NoteInstrument(9);

    /**
     * Block: Iron
     */
    public static final NoteInstrument IRON_XYLOPHONE = new NoteInstrument(10);

    /**
     * Block: Soul Sand
     */
    public static final NoteInstrument COW_BELL = new NoteInstrument(11);

    /**
     * Block: Pumpkin
     */
    public static final NoteInstrument DIDGERIDOO = new NoteInstrument(12);

    /**
     * Block: Emerald
     */
    public static final NoteInstrument BIT = new NoteInstrument(13);

    /**
     * Block: Hay
     */
    public static final NoteInstrument BANJO = new NoteInstrument(14);

    /**
     * Block: Glowstone
     */
    public static final NoteInstrument PLING = new NoteInstrument(15);

    public NoteInstrument(final int key) {
      super(key, false);
    }
  }

  public static class CustomInstrument extends Instrument {
    private final String name;
    private final String fileName;
    private final boolean shouldPressKey;

    public CustomInstrument(final int key, final String name, final String fileName) {
      super(key, true);
      this.name = name;
      this.fileName = fileName;
      this.shouldPressKey = false;
    }

    public CustomInstrument(final CustomInstrumentBuilder builder) {
      super(builder.key, true);
      name = builder.name;
      fileName = builder.fileName;
      shouldPressKey = builder.shouldPressKey;
    }

    public String getName() {
      return name;
    }

    public String getFileName() {
      return fileName;
    }

    public String getSoundFileName() {
      return fileName;
    }

    public org.bukkit.Sound getSound() {
      return null;
    }

    public boolean shouldPressKey() {
      return shouldPressKey;
    }

    public static CustomInstrumentBuilder builder() {
      return new CustomInstrumentBuilder();
    }

    public static CustomInstrumentBuilder builder(final CustomInstrument other) {
      return new CustomInstrumentBuilder(other);
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + ((name == null) ? 0 : name.hashCode());
      result = 31 * result + ((fileName == null) ? 0 : fileName.hashCode());
      result = 31 * result + (shouldPressKey ? 1231 : 1237);
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (!super.equals(obj) || getClass() != obj.getClass())
        return false;
      final CustomInstrument other = (CustomInstrument) obj;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      if (fileName == null) {
        if (other.fileName != null)
          return false;
      } else if (!fileName.equals(other.fileName))
        return false;
      if (shouldPressKey != other.shouldPressKey)
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "CustomInstrument [key=" + key + ", name=" + name + ", isCustom=" + isCustom + ", fileName=" + fileName
          + ", shouldPressKey=" + shouldPressKey + "]";
    }
  }

  public static class CustomInstrumentBuilder implements Builder<CustomInstrument> {

    private String name = "";
    private String fileName = "";
    private int key = 45;
    private boolean shouldPressKey = false;

    private CustomInstrumentBuilder() {
    }

    private CustomInstrumentBuilder(final CustomInstrument other) {
      this.name = other.getName();
      this.fileName = other.getFileName();
      this.key = other.getKey();
      this.shouldPressKey = other.shouldPressKey();
    }

    public CustomInstrumentBuilder setName(final String name) {
      if (name == null || name.isEmpty()) {
        throw new IllegalArgumentException("Instrument Name cant be empty");
      }
      this.name = name;
      return this;
    }

    public CustomInstrumentBuilder setFileName(final String fileName) {
      if (fileName == null || name.isEmpty()) {
        throw new IllegalArgumentException("Instrument File Name cant be empty");
      }

      this.fileName = fileName;
      return this;
    }

    public CustomInstrumentBuilder setKey(final int key) {
      this.key = MathUtils.normalizeRangeOrThrow(key, Note.MINIMUM_NOTE, Note.MAXIMUM_NOTE, "Instrument Key");
      return this;
    }

    public CustomInstrumentBuilder setShouldPressKey(final boolean shouldPressKey) {
      this.shouldPressKey = shouldPressKey;
      return this;
    }

    @Override
    public CustomInstrument build() {
      return new CustomInstrument(this);
    }
  }
}
