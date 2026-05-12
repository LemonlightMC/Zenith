package com.lemonlightmc.zenith.math;

import java.util.function.BinaryOperator;

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

  public static MathOperation fromString(final String input) {
    for (final MathOperation mathOp : MathOperation.values()) {
      if (mathOp.symbol.equals(input)) {
        return mathOp;
      }
    }
    throw new IllegalArgumentException(input + " is not a valid MathOperation");
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
}
