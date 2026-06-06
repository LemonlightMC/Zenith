package com.lemonlightmc.zenith.sound;

import org.bukkit.Note.Tone;

import com.google.common.base.Preconditions;
import com.lemonlightmc.zenith.utils.MathUtils;

public class Note extends Playable {
  public static final int MINIMUM_NOTE = 0;
  public static final int MAXIMUM_NOTE = 87;

  private static final double[] pitches = new double[2401];
  private static final double[] pitchArray = new double[25];
  static {
    for (int i = 0; i <= 24; i++) {
      // See https://minecraft.wiki/w/Note_Block#Notes
      pitchArray[i] = Math.pow(2, (i - 12) / 12f);
    }
    for (int i = 0; i < 2401; i++) {
      pitches[i] = (double) Math.pow(2, (i - 1200d) / 1200d);
    }
  }

  public static double getPitchTransposed(final Note note) {
    return getPitchTransposed(note.getNote(), note.getPitch());
  }

  public static double getPitchTransposed(final byte key, double pitch) {
    // Apply key to pitch
    pitch += key * 100;

    while (pitch < 3300)
      pitch += 1200;
    while (pitch > 5700)
      pitch -= 1200;

    pitch -= 3300;

    return pitches[(int) pitch];
  }

  private final byte note;
  private final Instrument instrument;

  public Note(final int note, final Instrument instrument) {
    Preconditions.checkArgument(note >= 0 && note <= 24, "The note value has to be between 0 and 24.");

    this.note = (byte) note;
    this.instrument = instrument;
  }

  @SuppressWarnings("deprecation")
  public Note(final int octave, Tone tone, boolean sharped) {
    if (sharped && !tone.isSharpable()) {
      tone = Tone.values()[tone.ordinal() + 1];
      sharped = false;
    }
    if (octave < 0 || octave > 2 || (octave == 2 && !(tone == Tone.F && sharped))) {
      throw new IllegalArgumentException("Tone and octave have to be between F#0 and F#2");
    }

    this.note = (byte) (octave * Tone.TONES_COUNT + tone.getId(sharped));
    this.instrument = Instrument.NoteInstrument.HARP;
  }

  public Note(final NoteBuilder builder) {
    instrument = builder.instrument;
    note = builder.note;
    setPitch(pitchArray[this.note]);
    setPanning(builder.panning);
    setVolume(builder.volume);
  }

  public static Note flat(final int octave, Tone tone) {
    Preconditions.checkArgument(octave != 2, "Octave cannot be 2 for flats");
    tone = tone == Tone.G ? Tone.F : Tone.values()[tone.ordinal() - 1];
    return new Note(octave, tone, tone.isSharpable());
  }

  public static Note sharp(final int octave, final Tone tone) {
    return new Note(octave, tone, true);
  }

  public static Note natural(final int octave, final Tone tone) {
    Preconditions.checkArgument(octave != 2, "Octave cannot be 2 for naturals");
    return new Note(octave, tone, false);
  }

  static NoteBuilder builder() {
    return new NoteBuilder();
  }

  static NoteBuilder builder(final Note note) {
    return new NoteBuilder(note);
  }

  public byte getNote() {
    return note;
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public boolean isCustomInstrument() {
    return instrument.isCustom();
  }

  public int getOctave() {
    return note / Tone.TONES_COUNT;
  }

  private byte getToneByte() {
    return (byte) (note % Tone.TONES_COUNT);
  }

  @SuppressWarnings("deprecation")
  public Tone getTone() {
    return Tone.getById(getToneByte());
  }

  @SuppressWarnings("deprecation")
  public boolean isSharped() {
    final byte note = getToneByte();
    return Tone.getById(note).isSharped(note);
  }

  public Note sharped() {
    Preconditions.checkArgument(note < 24, "This note cannot be sharped because it is the highest known note!");
    return new Note(note + 1, instrument);
  }

  public Note flattened() {
    Preconditions.checkArgument(note > 0, "This note cannot be flattened because it is the lowest known note!");
    return new Note(note - 1, instrument);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + note;
    result = 31 * result + ((instrument == null) ? 0 : instrument.hashCode());
    result = 31 * result + Double.hashCode(pitch);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj) || getClass() != obj.getClass())
      return false;
    final Note other = (Note) obj;
    if (instrument == null) {
      if (other.instrument != null)
        return false;
    } else if (!instrument.equals(other.instrument))
      return false;
    return note == other.note && Double.doubleToLongBits(pitch) == Double.doubleToLongBits(other.pitch);
  }

  public static class NoteBuilder extends PlayableBuilder {
    private Instrument instrument = Instrument.NoteInstrument.HARP;
    private byte note = 45;

    public NoteBuilder() {
    }

    public NoteBuilder(final Note note) {
      super(note);
      this.instrument = note.getInstrument();
      this.note = note.getNote();
    }

    public NoteBuilder instrument(final Instrument instrument) {
      this.instrument = instrument;
      return this;
    }

    public NoteBuilder note(byte note) {
      note = (byte) MathUtils.normalizeRangeOrThrow((int) note, MINIMUM_NOTE, MAXIMUM_NOTE, "note");
      this.note = note;
      return this;
    }

    @Override
    public Note build() {
      return new Note(this);
    }
  }
}
