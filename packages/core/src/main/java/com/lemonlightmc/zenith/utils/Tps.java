package com.lemonlightmc.zenith.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Tps {

  private static final Lazy<Supplier<double[]>> SUPPLIER = Lazy.from(() -> {
    try {
      final Method spigotMethod = Bukkit.getServer().getClass().getMethod("spigot");
      final Method getTPSMethod = Class.forName("org.bukkit.Server$Spigot").getMethod("getTPS");
      final Object spigot = spigotMethod.invoke(Bukkit.getServer());
      return () -> {
        try {
          return (double[]) getTPSMethod.invoke(spigot);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      };
    } catch (final Exception e) {
      // ignore
    }

    try {
      final Method getTPSMethod = Bukkit.class.getMethod("getTPS");
      return () -> {
        try {
          return (double[]) getTPSMethod.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      };
    } catch (final Exception e) {
      // ignore
    }
    return null;
  });

  public static boolean isSupported() {
    return SUPPLIER.isPresent();
  }

  public static Optional<Tps> read() {
    if (SUPPLIER.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(new Tps(SUPPLIER.get().get()));
  }

  private final double avg1;
  private final double avg5;
  private final double avg15;
  private final double[] asArray;

  public Tps(final double avg1, final double avg5, final double avg15) {
    this.avg1 = avg1;
    this.avg5 = avg5;
    this.avg15 = avg15;
    this.asArray = new double[] { avg1, avg5, avg15 };
  }

  public Tps(final double[] values) {
    this.avg1 = values[0];
    this.avg5 = values[1];
    this.avg15 = values[2];
    this.asArray = values;
  }

  public double avg1() {
    return this.avg1;
  }

  public double avg5() {
    return this.avg5;
  }

  public double avg15() {
    return this.avg15;
  }

  public double[] asArray() {
    return this.asArray;
  }

  public String toFormattedString() {
    return String.join(", ", format(this.avg1), format(this.avg5), format(this.avg15));
  }

  public static String format(final double tps) {
    final StringBuilder sb = new StringBuilder();
    if (tps > 18.0) {
      sb.append(ChatColor.GREEN);
    } else if (tps > 16.0) {
      sb.append(ChatColor.YELLOW);
    } else {
      sb.append(ChatColor.RED);
    }

    sb.append(Math.min(Math.round(tps * 100.0) / 100.0, 20.0));

    if (tps > 20.0) {
      sb.append('*');
    }

    return sb.toString();
  }

  @Override
  public int hashCode() {
    int result = 31 + Double.hashCode(avg1);
    result = 31 * result + Double.hashCode(avg5);
    result = 31 * result + Double.hashCode(avg15);
    return 31 * result + Arrays.hashCode(asArray);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Tps other = (Tps) obj;
    return Double.doubleToLongBits(avg1) == Double.doubleToLongBits(other.avg1)
        && Double.doubleToLongBits(avg5) == Double.doubleToLongBits(other.avg5)
        && Double.doubleToLongBits(avg15) == Double.doubleToLongBits(other.avg15)
        && Arrays.equals(asArray, other.asArray);
  }

  @Override
  public String toString() {
    return "Tps [" + Arrays.toString(asArray) + "]";
  }

}
