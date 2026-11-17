package com.lemonlightmc.zenith.math;

import java.util.Locale;
import java.util.function.BinaryOperator;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Represents a binary math operation that can be parsed from user input and applied to numeric values.
 */
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

  /**
   * Returns the symbolic representation of this operation.
   *
   * @return the operation symbol
   */
  @Override
  public String toString() {
    return symbol;
  }

  /**
   * Resolves a math operation from its symbol or enum name.
   *
   * @param input operation symbol or name to parse
   * @return the matching operation, or {@code null} if the input is empty or unknown
   */
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

  /**
   * Gets the symbolic representation of this operation.
   *
   * @return the operation symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Gets the binary function backing this operation.
   *
   * @return the operation function
   */
  public BinaryOperator<Double> getFunc() {
    return func;
  }

  /**
   * Applies this operation to two integer values.
   *
   * @param val1 first operand
   * @param val2 second operand
   * @return the operation result as an integer
   */
  public int apply(final int val1, final int val2) {
    return (int) apply((double) val1, (double) val2);
  }

  /**
   * Applies this operation to two long values.
   *
   * @param val1 first operand
   * @param val2 second operand
   * @return the operation result as a long
   */
  public long apply(final long val1, final long val2) {
    return (long) apply((double) val1, (double) val2);
  }

  /**
   * Applies this operation to two float values.
   *
   * @param val1 first operand
   * @param val2 second operand
   * @return the operation result as a float
   */
  public float apply(final float val1, final float val2) {
    return (float) apply((double) val1, (double) val2);
  }

  /**
   * Applies this operation to two double values.
   *
   * @param val1 first operand
   * @param val2 second operand
   * @return the operation result
   */
  public double apply(final double val1, final double val2) {
    return func.apply(val1, val2);
  }

  /**
   * Serializes and deserializes {@link MathOperation} values for Configurate.
   */
  public static final class MathOperationSerializer implements TypeSerializer<MathOperation> {

    /**
     * Shared serializer instance.
     */
    public static final MathOperationSerializer INSTANCE = new MathOperationSerializer();

    private MathOperationSerializer() {
    }

    /**
     * Deserializes a math operation from a configuration node.
     *
     * @param type target type
     * @param node configuration node to read from
     * @return the deserialized math operation, or {@code null} if the node is empty
     * @throws SerializationException if the node contains an unknown operation value
     */
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

    /**
     * Serializes a math operation to a configuration node.
     *
     * @param type source type
     * @param obj math operation to serialize
     * @param node configuration node to write to
     * @throws SerializationException if serialization fails
     */
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
