package com.lemonlightmc.zenith.math;

import java.util.Locale;
import java.util.function.BinaryOperator;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public enum MathOperation {
  ADD("+=", (val1, val2) -> val1 + val2),
  SUBTRACT("-=", (val1, val2) -> val1 - val2),
  MULTIPLY("*=", (val1, val2) -> val1 * val2),
  DIVIDE("/=", (val1, val2) -> val1 / val2),
  MOD("%=", (val1, val2) -> val1 % val2),
  @SuppressWarnings("null")
  MIN("<", Math::min),
  @SuppressWarnings("null")
  MAX(">", Math::max),
  SWAP("><", (val1, val2) -> val2),
  ASSIGN("=", (val1, val2) -> val2);

  private final String symbol;
  private final BinaryOperator<Double> func;

  MathOperation(final String symbol, final BinaryOperator<Double> func) {
    this.symbol = symbol;
    this.func = func;
  }

  @Override
  public String toString() {
    return symbol;
  }

  public static MathOperation fromString(String input) {
    if (input == null || input.isEmpty()) {
      return null;
    }
    input = input.trim();
    final String inputName = input.toUpperCase(Locale.ROOT).replace('-', '_');
    for (final MathOperation mathOp : MathOperation.values()) {
      if (mathOp.symbol.equals(input) || mathOp.name().equals(inputName)) {
        return mathOp;
      }
    }
    return null;
  }

  public String getSymbol() {
    return symbol;
  }

  public BinaryOperator<Double> getFunc() {
    return func;
  }

  public int apply(final int val1, final int val2) {
    return (int) apply((double) val1, (double) val2);
  }

  public long apply(final long val1, final long val2) {
    return (long) apply((double) val1, (double) val2);
  }

  public float apply(final float val1, final float val2) {
    return (float) apply((double) val1, (double) val2);
  }

  public double apply(final double val1, final double val2) {
    return func.apply(val1, val2);
  }

  public static final class MathOperationSerializer implements TypeSerializer<MathOperation> {

    public static final MathOperationSerializer INSTANCE = new MathOperationSerializer();

    private MathOperationSerializer() {
    }

    @Override
    public MathOperation deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
      if (node == null || node.virtual() || node.isNull()) {
        return null;
      }

      final String raw = node.getString();
      if (raw == null || raw.isEmpty()) {
        return null;
      }

      MathOperation operation = MathOperation.fromString(raw);
      if (operation == null) {
        throw new SerializationException(node, MathOperation.class, "Unknown math operation: " + raw);
      }
      return operation;
    }

    @Override
    public void serialize(final Type type, final MathOperation obj, final ConfigurationNode node)
        throws SerializationException {
      if (obj == null) {
        node.set(null);
        return;
      }
      node.set(obj.getSymbol());
    }
  }
}
