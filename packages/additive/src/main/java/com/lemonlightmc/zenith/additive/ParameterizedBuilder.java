
package com.lemonlightmc.zenith.additive;

public interface ParameterizedBuilder<I, O> {
  public O build(I param);
}
