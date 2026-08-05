
package com.lemonlightmc.zenith.additive.interfaces;

public interface ParameterizedBuilder<I, O> {
  public O build(I param);
}
