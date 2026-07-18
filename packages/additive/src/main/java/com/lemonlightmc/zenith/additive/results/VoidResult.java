package com.lemonlightmc.zenith.additive.results;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.lemonlightmc.zenith.additive.ThrowingRunnable;

/**
 * A result object which either is in success state with no value, or in error
 * state containing a non-{@code null} error value.
 * <p>
 * A variable whose type is {@code VoidResult} should never itself be
 * {@code null}, it should always point to an {@code VoidResult} instance.
 *
 * @param <E> the type of the error value
 */
public sealed interface VoidResult<E> permits VoidResult.Success, VoidResult.Error {

  /**
   * Returns a {@code VoidResult} in success state.
   *
   * @param <E> the type of the error value
   * @return a {@code VoidResult} in success state
   */
  static <E> VoidResult<E> success() {
    return new Success<>();
  }

  /**
   * Returns a {@code VoidResult} in error state containing the given
   * non-{@code null} value as error value.
   *
   * @param value the error value, which must be non-{@code null}
   * @param <E>   the type of the error value
   * @return a {@code VoidResult} in error state containing the given error
   *         value
   * @throws NullPointerException if given error value is {@code null}
   */
  static <E> VoidResult<E> error(final E value) {
    return new Error<>(Objects.requireNonNull(value));
  }

  /**
   * Returns a {@code VoidResult} in success state.
   *
   * @param <E> the type of the error value
   * @return a {@code VoidResult} in success state
   */
  static <E> VoidResult<E> of() {
    return success();
  }

  /**
   * Handle the given {@code ThrowingRunnable}. If the {@code ThrowingRunnable}
   * executes successfully, the {@code VoidResult} will be in success state.
   * If the {@code ThrowingRunnable} throws an exception, the
   * {@code VoidResult} will be in error state containing the thrown exception.
   *
   * Note! A custom {@code ThrowingRunnable} is used here instead of
   * {@code Runnable} to allow handling of checked exceptions.
   *
   * @param runnable the {@code ThrowingRunnable} to handle
   * @return a {@code VoidResult} either in success state, or in error state
   *         containing the exception thrown by the {@code ThrowingRunnable}
   * @throws NullPointerException if the given runnable is {@code null}
   */
  static VoidResult<Exception> of(final ThrowingRunnable runnable) {
    Objects.requireNonNull(runnable);
    try {
      runnable.run();
      return VoidResult.success();
    } catch (final Exception e) {
      return VoidResult.error(e);
    }
  }

  /**
   * Handle the given {@code ThrowingRunnable}. If the {@code ThrowingRunnable}
   * executes successfully, the {@code VoidResult} will be in success state.
   * If the {@code ThrowingRunnable} throws an exception, the
   * {@code VoidResult} will be in error state containing the result after
   * mapping the exception with the given exception mapper function.
   *
   * Note! A custom {@code ThrowingRunnable} is used here instead of
   * {@code Runnable} to allow handling of checked exceptions.
   *
   * @param runnable the {@code ThrowingRunnable} to handle
   * @param <E>      type of the error value after mapping a thrown exception
   * @return a {@code VoidResult} either in success state, or in error state
   *         containing the result after mapping the exception thrown by the
   *         {@code ThrowingRunnable}
   * @throws NullPointerException if the given runnable is {@code null} or
   *                              the given exception mapper function is
   *                              {@code null} or returns
   *                              {@code null}
   */
  static <E> VoidResult<E> of(final ThrowingRunnable runnable,
      final Function<Exception, E> exceptionMapper) {
    Objects.requireNonNull(exceptionMapper);
    return of(runnable).mapError(exceptionMapper);
  }

  /**
   * Returns a {@code VoidResult} in success state or the first error
   * {@code VoidResult} if any of the given {@code Result}s is in error state.
   * 
   * @apiNote The returned list is unmodifiable and will not contain any
   *          {@code null}
   * @param results the {@code Result}s to collect success values from
   * @throws NullPointerException if the given {@code Iterable} is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <E> VoidResult<E> all(
      final Iterable<? extends VoidResult<? extends E>> results) {
    Objects.requireNonNull(results);

    for (final VoidResult<? extends E> result : results) {
      if (result == null) {
        continue;
      }
      if (result.isError()) {
        return (VoidResult<E>) result;
      }
    }
    return VoidResult.success();
  }

  /**
   * Returns a {@code VoidResult} in success state or the first error
   * {@code VoidResult} if any of the given {@code Result}s is in error state.
   * 
   * @apiNote The returned list is unmodifiable and will not contain any
   *          {@code null}
   * @param results the {@code Result}s to collect success values from
   * @throws NullPointerException if the given {@code Iterable} is {@code null}
   */
  @SuppressWarnings("unchecked")
  @SafeVarargs
  static <E> VoidResult<E> all(final VoidResult<? extends E>... results) {
    Objects.requireNonNull(results);

    for (final VoidResult<? extends E> result : results) {
      if (result == null) {
        continue;
      }
      if (result.isError()) {
        return (VoidResult<E>) result;
      }
    }
    return VoidResult.success();
  }

  /**
   * Returns the first successful {@code VoidResult}, or the last error when none
   * succeeds.
   * At least one result must be supplied.
   * 
   * @apiNote The returned list is unmodifiable and will not contain any
   *          {@code null}
   * @param results the {@code Result}s to collect success values from
   * @throws NullPointerException if the given {@code Iterable} is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <E> VoidResult<E> any(
      final Iterable<? extends VoidResult<? extends E>> results) {
    Objects.requireNonNull(results);

    VoidResult<E> lastError = null;
    for (final VoidResult<? extends E> result : results) {
      if (result == null) {
        continue;
      }
      if (result.isSuccess()) {
        return (VoidResult<E>) result;
      }
      lastError = (VoidResult<E>) result;
    }
    return lastError;
  }

  /**
   * Returns the first successful {@code VoidResult}, or the last error when none
   * succeeds.
   * At least one result must be supplied.
   * 
   * @apiNote The returned list is unmodifiable and will not contain any
   *          {@code null}
   * @param results the {@code Result}s to collect success values from
   * @throws NullPointerException if the given {@code Iterable} is {@code null}
   */
  @SuppressWarnings("unchecked")
  @SafeVarargs
  static <E> VoidResult<E> any(final VoidResult<? extends E>... results) {
    Objects.requireNonNull(results);

    VoidResult<E> lastError = null;
    for (final VoidResult<? extends E> result : results) {
      if (result == null) {
        continue;
      }
      if (result.isSuccess()) {
        return (VoidResult<E>) result;
      }
      lastError = (VoidResult<E>) result;
    }
    return lastError;
  }

  /**
   * If in error state, returns a {@code VoidResult} containing the result of
   * applying the given mapping function to the error value, otherwise returns
   * the unaltered {@code VoidResult} in success state.
   *
   * @param function the mapping function to apply to the error value, if
   *                 error state
   * @param <N>      the type of the value returned from the mapping function
   * @return a {@code VoidResult} containing the result of applying the
   *         mapping function to the error value of this {@code VoidResult}, if in
   *         error state, otherwise the unaltered {@code VoidResult} in success
   *         state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> VoidResult<N> mapError(Function<? super E, ? extends N> function);

  /**
   * If in success state, returns a {@code Result} containing the value
   * provided by the given supplier, otherwise returns a {@code Result}
   * containing the error value of this {@code VoidResult}.
   *
   * @param supplier the supplier to provide the value if success state, may
   *                 not be {@code null}
   * @param <N>      the type of the value provided by the supplier
   * @return a {@code Result} containing the value provided by the given
   *         supplier, if in success state, otherwise a {@code Result} containing
   *         the
   *         error value of this {@code VoidResult}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  <N> Result<N, E> replace(Supplier<? extends N> supplier);

  /**
   * If in success state, returns a {@code OptionalResult} containing the
   * optional value provided by given supplier, otherwise returns a
   * {@code OptionalResult} containing the error value of this
   * {@code VoidResult}.
   *
   * @param supplier the supplier to provide the optional value if success
   *                 state, may not be {@code null}
   * @param <N>      the type of the value which may be present in the
   *                 {@code Optional} provided by the supplier
   * @return a {@code OptionalResult} containing the optional value provided
   *         by the given supplier, if in success state, otherwise a
   *         {@code OptionalResult} containing the error value of this
   *         {@code VoidResult}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  <N> OptionalResult<N, E> replaceWithOptional(
      Supplier<Optional<? extends N>> supplier);

  /**
   * If in success state, returns a {@code BooleanResult} containing the
   * boolean value provided by the given supplier, otherwise returns a
   * {@code BooleanResult} containing the error value of this
   * {@code VoidResult}.
   *
   * @param supplier the supplier to provide the boolean value if success
   *                 state, may not be {@code null}
   * @return a {@code BooleanResult} containing the boolean value provided by
   *         the given supplier, if in success state, otherwise a
   *         {@code BooleanResult} containing the error value of this
   *         {@code VoidResult}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  BooleanResult<E> replaceWithBoolean(Supplier<Boolean> supplier);

  /**
   * If in success state, returns the {@code Result} provided by the given
   * supplier, otherwise returns a {@code Result} containing the error value
   * of this {@code VoidResult}.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code Result} provided by the supplier
   * @param supplier the supplier to provide the {@code Result}, if success
   *                 state
   * @return the {@code Result} provided by the supplier, if in success state,
   *         otherwise a {@code Result} containing the error value of this
   *         {@code VoidResult}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  <N> Result<N, E> flatReplace(
      Supplier<Result<? extends N, ? extends E>> supplier);

  /**
   * If in success state, returns the {@code OptionalResult} provided by the
   * given supplier, otherwise returns an {@code OptionalResult} containing
   * the error value of this {@code VoidResult}.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code OptionalResult} provided by the supplier
   * @param supplier the supplier to provide the {@code OptionalResult}, if
   *                 success state
   * @return the {@code OptionalResult} provided by the supplier, if in
   *         success state, otherwise an {@code OptionalResult} containing the
   *         error
   *         value of this {@code VoidResult}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  <N> OptionalResult<N, E> flatReplaceToOptionalResult(
      Supplier<OptionalResult<? extends N, ? extends E>> supplier);

  /**
   * If in success state, returns the {@code BooleanResult} provided by the
   * given supplier, otherwise returns a {@code BooleanResult} containing the
   * error value of this {@code VoidResult}.
   *
   * @param supplier the supplier to provide the {@code BooleanResult}, if
   *                 success state
   * @return the {@code BooleanResult} provided by the supplier, if in
   *         success state, otherwise a {@code BooleanResult} containing the error
   *         value of this {@code VoidResult}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  BooleanResult<E> flatReplaceToBooleanResult(
      Supplier<BooleanResult<? extends E>> supplier);

  /**
   * If in success state, returns the {@code VoidResult} provided by the given
   * supplier, otherwise returns the unaltered {@code VoidResult} in error
   * state.
   *
   * @param supplier the supplier to provide the {@code VoidResult}, if
   *                 success state
   * @return the {@code VoidResult} provided by the supplier, if in success
   *         state, otherwise the unaltered {@code VoidResult} in error state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  VoidResult<E> flatReplaceToVoidResult(Supplier<VoidResult<? extends E>> supplier);

  /**
   * If in error state, returns a new {@code VoidResult} in success state,
   * otherwise returns the unaltered {@code VoidResult} in success state.
   *
   * @return a {@code VoidResult} in success state
   */
  VoidResult<E> recover();

  /**
   * If in error state, returns the {@code VoidResult} from applying the given
   * mapping function to the error value, otherwise returns the unaltered
   * {@code VoidResult} in success state.
   *
   * @param function the mapping function to apply to the error value to
   *                 convert to a new {@code VoidResult}, if error state
   * @return the {@code VoidResult} returned from the mapping function, if in
   *         error state, otherwise the unaltered {@code VoidResult} in success
   *         state
   */
  VoidResult<E> flatRecover(
      Function<E, VoidResult<? extends E>> function);

  /**
   * If in error state, applies the error value to the given consumer,
   * otherwise does nothing.
   *
   * @param errorConsumer the consumer which accepts the error value
   * @return the original {@code VoidResult} unaltered
   * @throws NullPointerException if the given consumer is {@code null}
   */
  VoidResult<E> consumeError(Consumer<? super E> errorConsumer);

  /**
   * If in success state, runs the success-runnable. If in error state,
   * applies the error value to the given error-consumer.
   *
   * @param successRunnable the runnable to run if success state
   * @param errorConsumer   the consumer which accepts the error value
   * @return the original {@code VoidResult} unaltered
   * @throws NullPointerException if either the given runnable or consumer is
   *                              {@code null}
   */
  VoidResult<E> consumeEither(
      Runnable successRunnable,
      Consumer<? super E> errorConsumer);

  /**
   * If in success state, runs the given runnable, otherwise does nothing.
   *
   * @param runnable the runnable to run if success state
   * @return the original {@code VoidResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  VoidResult<E> runIfSuccess(Runnable runnable);

  /**
   * If in error state, runs the given runnable, otherwise does nothing.
   *
   * @param runnable the runnable to run if error state
   * @return the original {@code VoidResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  VoidResult<E> runIfError(Runnable runnable);

  /**
   * If in success state, runs the given success runnable. If in error state,
   * runs the given error runnable.
   *
   * @param successRunnable the runnable to run if success state
   * @param errorRunnable   the runnable to run if error state
   * @return the original {@code VoidResult} unaltered
   * @throws NullPointerException if one of the given runnables is {@code null}
   */
  VoidResult<E> runEither(Runnable successRunnable, Runnable errorRunnable);

  /**
   * Runs the given runnable, no matter the state.
   *
   * @param runnable the runnable to run
   * @return the original {@code VoidResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  VoidResult<E> runAlways(Runnable runnable);

  /**
   * If in success state, runs the given supplier. If the supplier returns a
   * {@code VoidResult} in success state, the original {@code VoidResult} is
   * returned unaltered. If the supplier returns a {@code VoidResult} in error
   * state, a {@code VoidResult} containing the error value is returned. If in
   * error state, the original {@code VoidResult} is returned unaltered.
   *
   * @param supplier the supplier to run
   * @return the original {@code VoidResult} unaltered if the given supplier
   *         returns success or the original {@code VoidResult} is in error state,
   *         otherwise a {@code VoidResult} containing the error value from the
   *         supplier result
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  VoidResult<E> flatRunIfSuccess(Supplier<? extends VoidResult<? extends E>> supplier);

  /**
   * Retrieve a value from this {@code VoidResult} by folding the states. If
   * in success state, return the value provided by the value-supplier. If in
   * error state, return the value of applying the error-function to the error
   * value.
   *
   * @param <N>           the type of retrieved value
   * @param valueSupplier supplier to provide the value, if success state, may
   *                      return {@code null}
   * @param errorFunction the mapping function to apply to the error value, if
   *                      error state, may return {@code null}
   * @return the folded value mapped from either the success value or error
   *         value, may be {@code null}
   * @throws NullPointerException if either the given supplier or function is
   *                              {@code null}
   */
  <N> N fold(Supplier<? extends N> valueSupplier,
      Function<? super E, ? extends N> errorFunction);

  /**
   * Returns whether this result is in the success state.
   */
  default boolean isSuccess() {
    return fold(() -> true, error -> false);
  }

  /**
   * Returns whether this result is in the error state.
   */
  default boolean isError() {
    return !isSuccess();
  }

  /**
   * Returns a sequential {@code Stream} containing the success value if this
   * result is in success state, otherwise returns an empty {@code Stream}.
   *
   * @return a sequential {@code Stream} containing the success value if this
   *         result is in success state, otherwise an empty {@code Stream}
   */
  default Stream<Void> stream() {
    return Stream.empty();
  }

  /**
   * Transforms this {@code VoidResult} to an {@code OptionalResult}. If in
   * success state, the {@code OptionalResult} will be in empty success state.
   * If in error state, the {@code OptionalResult} will be in error state
   * containing the error value from this {@code VoidResult}.
   * <p>
   * The returned {@code OptionalResult} will never have a value.
   *
   * @param <N> the type of the success value in the returned
   *            {@code OptionalResult}, inferred from the variable
   * @return an {@code OptionalResult} in empty success state or in error
   *         state containing the error value from this {@code VoidResult}
   */
  <N> OptionalResult<N, E> toOptionalResult();

  record Success<ERR>() implements VoidResult<ERR> {

    @Override
    public <N> VoidResult<N> mapError(final Function<? super ERR, ? extends N> function) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @Override
    public <N> Result<N, ERR> replace(final Supplier<? extends N> supplier) {
      return Result.success(supplier.get());
    }

    @Override
    public <N> OptionalResult<N, ERR> replaceWithOptional(final Supplier<Optional<? extends N>> supplier) {
      return OptionalResult.success(supplier.get());
    }

    @Override
    public BooleanResult<ERR> replaceWithBoolean(final Supplier<Boolean> supplier) {
      return BooleanResult.success(supplier.get());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> Result<N, ERR> flatReplace(final Supplier<Result<? extends N, ? extends ERR>> supplier) {
      return (Result<N, ERR>) supplier.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> flatReplaceToOptionalResult(
        final Supplier<OptionalResult<? extends N, ? extends ERR>> supplier) {
      return (OptionalResult<N, ERR>) supplier.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public BooleanResult<ERR> flatReplaceToBooleanResult(final Supplier<BooleanResult<? extends ERR>> supplier) {
      return (BooleanResult<ERR>) supplier.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public VoidResult<ERR> flatReplaceToVoidResult(final Supplier<VoidResult<? extends ERR>> supplier) {
      return (VoidResult<ERR>) supplier.get();
    }

    @Override
    public VoidResult<ERR> recover() {
      return VoidResult.success();
    }

    @Override
    public VoidResult<ERR> flatRecover(final Function<ERR, VoidResult<? extends ERR>> function) {
      return safeCast();
    }

    @Override
    public VoidResult<ERR> consumeError(final Consumer<? super ERR> errorConsumer) {
      return safeCast();
    }

    @Override
    public VoidResult<ERR> consumeEither(final Runnable successRunnable, final Consumer<? super ERR> errorConsumer) {
      successRunnable.run();
      return safeCast();
    }

    @Override
    public VoidResult<ERR> runIfSuccess(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public VoidResult<ERR> runIfError(final Runnable runnable) {
      return safeCast();
    }

    @Override
    public VoidResult<ERR> runEither(final Runnable successRunnable, final Runnable errorRunnable) {
      successRunnable.run();
      return safeCast();
    }

    @Override
    public VoidResult<ERR> runAlways(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @SuppressWarnings("unchecked")
    @Override
    public VoidResult<ERR> flatRunIfSuccess(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      return (VoidResult<ERR>) Objects.requireNonNull(supplier.get());
    }

    @Override
    public <N> N fold(final Supplier<? extends N> valueSupplier,
        final Function<? super ERR, ? extends N> errorFunction) {
      return valueSupplier.get();
    }

    @Override
    public <N> OptionalResult<N, ERR> toOptionalResult() {
      return OptionalResult.success(Optional.empty());
    }

    @SuppressWarnings("unchecked")
    private <E1> Success<E1> safeCast() {
      return (Success<E1>) this;
    }
  }

  record Error<ERR>(ERR error) implements VoidResult<ERR> {

    public Error(final ERR error) {
      this.error = Objects.requireNonNull(error);
    }

    @Override
    public <N> VoidResult<N> mapError(final Function<? super ERR, ? extends N> function) {
      Objects.requireNonNull(function);
      return VoidResult.error(function.apply(error));
    }

    @Override
    public <N> Result<N, ERR> replace(final Supplier<? extends N> supplier) {
      return Result.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> replaceWithOptional(final Supplier<Optional<? extends N>> supplier) {
      return OptionalResult.error(error);
    }

    @Override
    public BooleanResult<ERR> replaceWithBoolean(final Supplier<Boolean> supplier) {
      return BooleanResult.error(error);
    }

    @Override
    public <N> Result<N, ERR> flatReplace(final Supplier<Result<? extends N, ? extends ERR>> supplier) {
      return Result.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> flatReplaceToOptionalResult(
        final Supplier<OptionalResult<? extends N, ? extends ERR>> supplier) {
      return OptionalResult.error(error);
    }

    @Override
    public BooleanResult<ERR> flatReplaceToBooleanResult(final Supplier<BooleanResult<? extends ERR>> supplier) {
      return BooleanResult.error(error);
    }

    @Override
    public VoidResult<ERR> flatReplaceToVoidResult(final Supplier<VoidResult<? extends ERR>> supplier) {
      return VoidResult.error(error);
    }

    @Override
    public VoidResult<ERR> recover() {
      return VoidResult.success();
    }

    @SuppressWarnings("unchecked")
    @Override
    public VoidResult<ERR> flatRecover(final Function<ERR, VoidResult<? extends ERR>> function) {
      return (VoidResult<ERR>) function.apply(error);
    }

    @Override
    public VoidResult<ERR> consumeError(final Consumer<? super ERR> errorConsumer) {
      errorConsumer.accept(error);
      return safeCast();
    }

    @Override
    public VoidResult<ERR> consumeEither(final Runnable successRunnable, final Consumer<? super ERR> errorConsumer) {
      errorConsumer.accept(error);
      return safeCast();
    }

    @Override
    public VoidResult<ERR> runIfSuccess(final Runnable runnable) {
      return safeCast();
    }

    @Override
    public VoidResult<ERR> runIfError(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public VoidResult<ERR> runEither(final Runnable successRunnable, final Runnable errorRunnable) {
      errorRunnable.run();
      return safeCast();
    }

    @Override
    public VoidResult<ERR> runAlways(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public VoidResult<ERR> flatRunIfSuccess(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      Objects.requireNonNull(supplier);
      return safeCast();
    }

    @Override
    public <N> N fold(final Supplier<? extends N> valueSupplier,
        final Function<? super ERR, ? extends N> errorFunction) {
      return errorFunction.apply(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> toOptionalResult() {
      return OptionalResult.error(error);
    }

    private Error<ERR> safeCast() {
      return (Error<ERR>) this;
    }

  }
}
