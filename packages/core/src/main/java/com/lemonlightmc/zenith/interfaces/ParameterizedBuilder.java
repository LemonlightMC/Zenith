
package com.lemonlightmc.zenith.interfaces;

public interface ParameterizedBuilder<I, O> {
  public O build(I param);
}
