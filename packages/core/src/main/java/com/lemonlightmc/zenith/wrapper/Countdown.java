package com.lemonlightmc.zenith.wrapper;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.lemonlightmc.zenith.additive.Builder;
import com.lemonlightmc.zenith.additive.time.IPolyTimeUnit;
import com.lemonlightmc.zenith.scheduler.GlobalScheduler;
import com.lemonlightmc.zenith.scheduler.ScheduledTask;

public class Countdown {

  public static final long DEFAULT_TIME = 100l;

  public CountdownBuilder builder() {
    return new CountdownBuilder();
  }

  private final Consumer<CountdownInfo> count;
  private final Consumer<CountdownInfo> done;
  private final Consumer<CountdownInfo> start;
  private final Supplier<Long> duration;

  private long startTime = -1L;
  private ScheduledTask task;
  private long durationTime;

  public Countdown(final Consumer<CountdownInfo> done) {
    this(done, (i) -> {
    }, (i) -> {
    }, () -> DEFAULT_TIME);
  }

  public Countdown(final Consumer<CountdownInfo> done, final Consumer<CountdownInfo> count) {
    this(done, count, (i) -> {
    }, () -> DEFAULT_TIME);
  }

  public Countdown(final Consumer<CountdownInfo> done, final Consumer<CountdownInfo> count,
      final Consumer<CountdownInfo> start) {
    this(done, count, start, () -> DEFAULT_TIME);
  }

  public Countdown(final Consumer<CountdownInfo> done, final Consumer<CountdownInfo> count,
      final Consumer<CountdownInfo> start, final Supplier<Long> duration) {
    this.count = count;
    this.done = done;
    this.start = start;
    this.duration = duration;
  }

  public Countdown(final Consumer<CountdownInfo> done, final Consumer<CountdownInfo> count,
      final Consumer<CountdownInfo> start, final long duration) {
    this.count = count;
    this.done = done;
    this.start = start;
    this.duration = () -> duration;
    this.durationTime = duration;
  }

  public Consumer<CountdownInfo> getDoneAction() {
    return done;
  }

  public Consumer<CountdownInfo> getCountAction() {
    return count;
  }

  public Consumer<CountdownInfo> getStartAction() {
    return start;
  }

  public Supplier<Long> getDurationSupplier() {
    return duration;
  }

  public Long getTotalTime() {
    return task == null ? null : durationTime;
  }

  public Long getStartTime() {
    return task == null ? null : startTime;
  }

  public Long getEndTime() {
    return task == null ? null : startTime + durationTime;
  }

  public Long getTimeLeft() {
    return task == null ? null : startTime + durationTime - System.currentTimeMillis();
  }

  public boolean hasStarted() {
    return task != null;
  }

  public boolean hasEnded() {
    return task != null && getTimeLeft() > 0;
  }

  public void start() {
    this.durationTime = duration.get();
    this.startTime = System.currentTimeMillis();
    start.accept(CountdownInfo.from(this));
    task = GlobalScheduler.runEveryAsync(() -> {
      final CountdownInfo info = CountdownInfo.from(this);
      count.accept(info);
      if (hasEnded()) {
        done.accept(info);
      }
    }, 20, 0);
  }

  public void stop() {
    task.cancel();
    done.accept(CountdownInfo.from(this));
    task = null;
    startTime = -1l;
  }

  @Override
  public int hashCode() {
    int result = 31 + ((count == null) ? 0 : count.hashCode());
    result = 31 * result + ((done == null) ? 0 : done.hashCode());
    result = 31 * result + ((start == null) ? 0 : start.hashCode());
    result = 31 * result + ((duration == null) ? 0 : duration.hashCode());
    result = 31 * result + (int) (startTime ^ (startTime >>> 32));
    result = 31 * result + ((task == null) ? 0 : task.hashCode());
    result = 31 * result + (int) (durationTime ^ (durationTime >>> 32));
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Countdown other = (Countdown) obj;
    if (done == null && other.done != null || count == null && other.count != null
        || start == null && other.start != null || task == null && other.task != null
        || duration == null && other.duration != null) {
      return false;
    }
    return durationTime == other.durationTime && startTime == other.startTime && done.equals(other.done)
        && count.equals(other.count) && start.equals(other.start) && duration.equals(other.duration)
        && task.equals(other.task);
  }

  @Override
  public String toString() {
    return "Countdown [startTime=" + startTime + ", durationTime=" + durationTime + "]";
  }

  public static class CountdownBuilder implements Builder<Countdown> {
    private Consumer<CountdownInfo> count;
    private Consumer<CountdownInfo> done;
    private Consumer<CountdownInfo> start;
    private Supplier<Long> duration = () -> DEFAULT_TIME;

    public CountdownBuilder() {
    }

    public CountdownBuilder count(final Consumer<CountdownInfo> count) {
      this.count = count;
      return this;
    }

    public CountdownBuilder done(final Consumer<CountdownInfo> done) {
      this.done = done;
      return this;
    }

    public CountdownBuilder start(final Consumer<CountdownInfo> start) {
      this.start = start;
      return this;
    }

    public CountdownBuilder duration(final long ticks) {
      this.duration = () -> ticks;
      return this;
    }

    public CountdownBuilder duration(final long ticks, final IPolyTimeUnit unit) {
      this.duration = () -> unit.toMillis(ticks);
      return this;
    }

    public CountdownBuilder duration(final Supplier<Long> duration) {
      this.duration = duration;
      return this;
    }

    @Override
    public Countdown build() {
      if (done == null || count == null) {
        throw new IllegalArgumentException("No Action for the Countdown has be given!");
      }
      return new Countdown(done, count, start, duration);
    }

    @Override
    public int hashCode() {
      int result = 1;
      result = 31 * result + ((count == null) ? 0 : count.hashCode());
      result = 31 * result + ((done == null) ? 0 : done.hashCode());
      result = 31 * result + ((start == null) ? 0 : start.hashCode());
      result = 31 * result + ((duration == null) ? 0 : duration.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      final CountdownBuilder other = (CountdownBuilder) obj;
      if (done == null && other.done != null || count == null && other.count != null
          || start == null && other.start != null) {
        return false;
      }
      return done.equals(other.done)
          && count.equals(other.count) && start.equals(other.start) && duration.equals(other.duration);
    }

    @Override
    public String toString() {
      return "CountdownBuilder [count=" + count + ", done=" + done + ", start=" + start + ", duration=" + duration
          + "]";
    }
  }

  public static record CountdownInfo(long startTime, long endTime, long duration) {
    public long getTimeLeft() {
      return startTime + duration - System.currentTimeMillis();
    }

    public long getTimePassed() {
      return System.currentTimeMillis() - startTime;
    }

    public int getCounts() {
      return (int) Math.floor(getTimePassed());
    }

    public int getRemainingCounts() {
      return (int) Math.ceil(getTimeLeft());
    }

    public static CountdownInfo from(final Countdown c) {
      return new CountdownInfo(c.startTime, c.getEndTime(), c.durationTime);
    }
  }
}
