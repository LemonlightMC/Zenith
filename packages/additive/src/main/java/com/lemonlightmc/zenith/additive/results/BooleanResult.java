package com.lemonlightmc.zenith.additive.results;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A result object which either is in success state containing a
 * non-{@code null} boolean value, or in error state containing a
 * non-{@code null} error value.
 * <p>
 * A variable whose type is {@code BooleanResult} should never itself be
 * {@code null}, it should always point to an {@code BooleanResult} instance.
 *
 * @param <E> the type of the error value
 */
public sealed interface BooleanResult<E> {

  /**
   * Returns a {@code BooleanResult} in success state containing the given
   * non-{@code null} boolean value as success value.
   *
   * @param value the boolean success value, which must be non-{@code null}
   * @param <E>   the type of the error value
   * @return a {@code BooleanResult} in success state containing the given
   *         boolean success value
   * @throws NullPointerException if given success value is {@code null}
   */
  static <E> BooleanResult<E> success(final boolean value) {
    return new Success<>(value);
  }

  /**
   * Returns a {@code BooleanResult} in success state containing {@code true}
   * as the boolean success value.
   *
   * @param <E> the type of the error value
   * @return a {@code BooleanResult} in success state containing {@code true}
   *         as the boolean success value.
   */
  static <E> BooleanResult<E> successTrue() {
    return BooleanResult.success(true);
  }

  /**
   * Returns a {@code BooleanResult} in success state containing {@code false}
   * as the boolean success value.
   *
   * @param <E> the type of the error value
   * @return a {@code BooleanResult} in success state containing {@code false}
   *         as the boolean success value.
   */
  static <E> BooleanResult<E> successFalse() {
    return BooleanResult.success(false);
  }

  /**
   * Returns a {@code BooleanResult} in error state containing the given
   * non-{@code null} value as error value.
   *
   * @param value the error value, which must be non-{@code null}
   * @param <E>   the type of the error value
   * @return a {@code BooleanResult} in error state containing the given error
   *         value
   * @throws NullPointerException if given error value is {@code null}
   */
  static <E> BooleanResult<E> error(final E value) {
    return new Error<>(Objects.requireNonNull(value));
  }

  /** Returns a boolean result in success state. */
  static <E> BooleanResult<E> of(final boolean value) {
    return success(value);
  }

  /** Executes the callable and captures a thrown exception as an error. */
  static BooleanResult<Exception> of(final Callable<Boolean> callable) {
    return handle(callable);
  }

  /** Executes the callable and maps a thrown exception to an error value. */
  static <E> BooleanResult<E> of(final Callable<Boolean> callable,
      final Function<Exception, E> exceptionMapper) {
    return handle(callable, exceptionMapper);
  }

  /**
   * If in success state, returns a {@code Result} containing the result of
   * applying the given mapping function to the boolean success value,
   * otherwise returns a {@code Result} containing the error value of this
   * {@code BooleanResult}.
   *
   * @param function the mapping function to apply to the boolean success
   *                 value, if success state
   * @param <N>      the type of the value returned from the mapping function
   * @return a {@code Result} containing the result of applying the mapping
   *         function to the boolean success value of this {@code BooleanResult},
   *         if
   *         in success state, otherwise a {@code Result} containing the error
   *         value
   *         of this {@code BooleanResult}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> Result<N, E> map(Function<Boolean, ? extends N> function);

  /** Maps a successful result to a supplied value. */
  default <N> Result<N, E> map(final Supplier<? extends N> supplier) {
    Objects.requireNonNull(supplier);
    return map(ignored -> supplier.get());
  }

  /**
   * If in success state, returns a {@code OptionalResult} containing the
   * result of applying the given mapping function to the boolean success
   * value, otherwise returns a {@code OptionalResult} containing the error
   * value of this {@code BooleanResult}.
   *
   * @param function the mapping function to apply to the boolean success
   *                 value, if success state
   * @param <N>      the type of the success value which may be present in the
   *                 {@code Optional} returned from the mapping function
   * @return a {@code OptionalResult} containing the result of applying the
   *         mapping function to the boolean success value of this
   *         {@code BooleanResult}, if in success state, otherwise a
   *         {@code OptionalResult} containing the error value of this
   *         {@code BooleanResult}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> OptionalResult<N, E> mapToOptional(
      Function<Boolean, ? extends Optional<? extends N>> function);

  /**
   * If in success state, returns a {@code BooleanResult} containing the
   * result of applying the given mapping function to the success value,
   * otherwise returns the unaltered {@code BooleanResult} in error state.
   *
   * @param function the mapping function to apply to the boolean success
   *                 value, if success state
   * @return a {@code BooleanResult} containing the result of applying the
   *         mapping function to the boolean success value of this
   *         {@code BooleanResult}, if in success state, otherwise the unaltered
   *         {@code BooleanResult} in error state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  BooleanResult<E> mapToBoolean(
      Function<Boolean, Boolean> function);

  /** Replaces the success value without reading it. */
  default <N> Result<N, E> replace(final Supplier<? extends N> supplier) {
    Objects.requireNonNull(supplier);
    return map(ignored -> supplier.get());
  }

  /**
   * If in error state, returns a {@code BooleanResult} containing the result
   * of applying the given mapping function to the error value, otherwise
   * returns the unaltered {@code BooleanResult} in success state.
   *
   * @param function the mapping function to apply to the error value, if
   *                 error state
   * @param <N>      the type of the value returned from the mapping function
   * @return a {@code BooleanResult} containing the result of applying the
   *         mapping function to the error value of this {@code BooleanResult}, if
   *         in
   *         error state, otherwise the unaltered {@code BooleanResult} in success
   *         state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> BooleanResult<N> mapError(Function<? super E, ? extends N> function);

  /**
   * If in success state, returns the {@code Result} from applying the given
   * mapping function to the boolean success value, otherwise returns a
   * {@code Result} containing the error value of this {@code BooleanResult}.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code Result} returned by the mapping function
   * @param function the mapping function to apply to the boolean success
   *                 value, if success state
   * @return the {@code Result} returned from the mapping function, if in
   *         success state, otherwise a {@code Result} containing the error
   *         value of this {@code BooleanResult}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> Result<N, E> flatMap(
      Function<Boolean, Result<? extends N, ? extends E>> function);

  /** Flat-maps a successful result with an additional argument. */
  default <N, U> Result<N, E> flatMap(
      final BiFunction<Boolean, ? super U, Result<? extends N, ? extends E>> function,
      final U argument) {
    Objects.requireNonNull(function);
    return flatMap(value -> function.apply(value, argument));
  }

  /** Flat-maps a successful result to a supplied result. */
  default <N> Result<N, E> flatMap(
      final Supplier<? extends Result<? extends N, ? extends E>> supplier) {
    Objects.requireNonNull(supplier);
    return flatMap(ignored -> supplier.get());
  }

  /** Replaces this result with a supplied result when in success state. */
  default <N> Result<N, E> flatReplace(
      final Supplier<Result<? extends N, ? extends E>> supplier) {
    Objects.requireNonNull(supplier);
    return flatMap(ignored -> supplier.get());
  }

  /**
   * If in success state, returns the {@code OptionalResult} from applying
   * the given mapping function to the boolean success value, otherwise
   * returns a {@code OptionalResult} containing the error value of this
   * {@code BooleanResult}.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code OptionalResult} returned by the mapping function
   * @param function the mapping function to apply to the boolean success
   *                 value, if success state
   * @return the {@code OptionalResult} returned from the mapping function, if
   *         in success state, otherwise a {@code OptionalResult} containing the
   *         error
   *         value of this {@code BooleanResult}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> OptionalResult<N, E> flatMapToOptionalResult(
      Function<Boolean, OptionalResult<? extends N, ? extends E>> function);

  /**
   * If in success state, returns the {@code BooleanResult} from applying the
   * given mapping function to the boolean success value, otherwise returns
   * the unaltered {@code BooleanResult} in error state.
   *
   * @param function the mapping function to apply to the boolean success
   *                 value, if success state
   * @return the {@code BooleanResult} returned from the mapping function, if
   *         in success state, otherwise the unaltered {@code BooleanResult} in
   *         error
   *         state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  BooleanResult<E> flatMapToBooleanResult(
      Function<Boolean, BooleanResult<? extends E>> function);

  /**
   * If in success state, returns the {@code VoidResult} from applying the
   * given mapping function to the boolean success value, otherwise returns a
   * {@code VoidResult} containing the error value of this
   * {@code BooleanResult}.
   *
   * @param function the mapping function to apply to the boolean success
   *                 value, if success state
   * @return the {@code VoidResult} returned from the mapping function, if in
   *         success state, otherwise a {@code VoidResult} containing the error
   *         value
   *         of this {@code BooleanResult}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  VoidResult<E> flatMapToVoidResult(
      Function<Boolean, VoidResult<? extends E>> function);

  /**
   * If in error state, returns a {@code BooleanResult} with the success
   * value from applying the given mapping function to the error value,
   * otherwise returns the unaltered {@code BooleanResult} in success state.
   *
   * @param function the mapping function to apply to the error value to
   *                 convert to a new success value, if error state
   * @return A {@code BooleanResult} containing the value from the mapping
   *         function, if in error state, otherwise the unaltered
   *         {@code BooleanResult} in success state
   */
  BooleanResult<E> recover(
      Function<E, Boolean> function);

  /**
   * If in error state, returns the {@code BooleanResult} from applying the
   * given mapping function to the error value, otherwise returns the
   * unaltered {@code BooleanResult} in success state.
   *
   * @param function the mapping function to apply to the error value to
   *                 convert to a new {@code BooleanResult}, if error state
   *                 {@code BooleanResult} returned by the mapping function
   * @return the {@code BooleanResult} returned from the mapping function, if
   *         in error state, otherwise the unaltered {@code BooleanResult} in
   *         success
   *         state
   */
  BooleanResult<E> flatRecover(
      Function<E, BooleanResult<? extends E>> function);

  /**
   * If in success state, applies the boolean success value to the given
   * consumer, otherwise does nothing.
   *
   * @param consumer the consumer which accepts the boolean success value
   * @return the original {@code BooleanResult} unaltered
   * @throws NullPointerException if the given consumer is {@code null}
   */
  BooleanResult<E> consume(Consumer<Boolean> consumer);

  /**
   * If in error state, applies the error value to the given consumer,
   * otherwise does nothing.
   *
   * @param errorConsumer the consumer which accepts the error value
   * @return the original {@code BooleanResult} unaltered
   * @throws NullPointerException if the given consumer is {@code null}
   */
  BooleanResult<E> consumeError(Consumer<? super E> errorConsumer);

  /**
   * If in success state, applies the boolean success value to the given value
   * consumer. If in error state, applies the error value to the given error
   * consumer.
   *
   * @param valueConsumer the consumer which accepts the boolean success value
   * @param errorConsumer the consumer which accepts the error value
   * @return the original {@code BooleanResult} unaltered
   * @throws NullPointerException if one of the given consumers is {@code null}
   */
  BooleanResult<E> consumeEither(
      Consumer<Boolean> valueConsumer,
      Consumer<? super E> errorConsumer);

  /**
   * If in success state with a success value of {@code true}, runs the given
   * true-runnable. If in success state with a success value of {@code false},
   * runs the given false-runnable. If in error state, applies the error value
   * to the given error consumer.
   *
   * @param trueRunnable  the runnable to run if a success value of
   *                      {@code true}
   * @param falseRunnable the runnable to run if a success value of
   *                      {@code false}
   * @param errorConsumer the consumer which accepts the error value
   * @return the original {@code BooleanResult} unaltered
   * @throws NullPointerException if one of the given runnables or consumer is
   *                              {@code null}
   */
  BooleanResult<E> consumeEither(
      Runnable trueRunnable,
      Runnable falseRunnable,
      Consumer<? super E> errorConsumer);

  /**
   * If in success state, applies the boolean success value to the given
   * function. If the function returns a {@code VoidResult} in success state,
   * the original {@code BooleanResult} is returned unaltered. If the function
   * returns a {@code VoidResult} in error state, a {@code BooleanResult}
   * containing the error value is returned. If in error state, the original
   * {@code BooleanResult} is returned unaltered.
   *
   * @param function the function which accepts the boolean success value
   * @return the original {@code BooleanResult} unaltered if the given
   *         function returns success or the original {@code BooleanResult} is in
   *         error state, otherwise a {@code BooleanResult} containing the error
   *         value
   *         from the function result
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  BooleanResult<E> flatConsume(Function<Boolean, ? extends VoidResult<? extends E>> function);

  /**
   * If in success state, runs the given runnable, otherwise does nothing.
   *
   * @param runnable the runnable to run if success state
   * @return the original {@code BooleanResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  BooleanResult<E> runIfSuccess(Runnable runnable);

  /**
   * If in success state with a success value of {@code true}, runs the given
   * runnable, otherwise does nothing.
   *
   * @param runnable the runnable to run if a success value of {@code true}
   * @return the original {@code BooleanResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  BooleanResult<E> runIfTrue(Runnable runnable);

  /**
   * If in success state with a success value of {@code false}, runs the given
   * runnable, otherwise does nothing.
   *
   * @param runnable the runnable to run if a success value of {@code false}
   * @return the original {@code BooleanResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  BooleanResult<E> runIfFalse(Runnable runnable);

  /**
   * If in error state, runs the given runnable, otherwise does nothing.
   *
   * @param runnable the runnable to run if error state
   * @return the original {@code BooleanResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  BooleanResult<E> runIfError(Runnable runnable);

  /**
   * If in success state, runs the given success runnable. If in error state,
   * runs the given error runnable.
   *
   * @param successRunnable the runnable to run if success state
   * @param errorRunnable   the runnable to run if error state
   * @return the original {@code BooleanResult} unaltered
   * @throws NullPointerException if one of the given runnables is {@code null}
   */
  BooleanResult<E> runEither(Runnable successRunnable, Runnable errorRunnable);

  /**
   * If in success state with a success value of {@code true}, runs the given
   * true-runnable. If in success state with a success value of {@code false},
   * runs the given false-runnable. If in error state, runs the given
   * error-runnable.
   *
   * @param trueRunnable  the runnable to run if a success value of
   *                      {@code true}
   * @param falseRunnable the runnable to run if a success value of
   *                      {@code false}
   * @param errorRunnable the runnable to run if error state
   * @return the original {@code BooleanResult} unaltered
   * @throws NullPointerException if one of the given runnables is {@code null}
   */
  BooleanResult<E> runEither(Runnable trueRunnable,
      Runnable falseRunnable,
      Runnable errorRunnable);

  /**
   * Runs the given runnable, no matter the state.
   *
   * @param runnable the runnable to run
   * @return the original {@code BooleanResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  BooleanResult<E> runAlways(Runnable runnable);

  /**
   * If in success state, runs the given supplier. If the supplier returns a
   * {@code VoidResult} in success state, the original {@code BooleanResult}
   * is returned unaltered. If the supplier returns a {@code VoidResult} in
   * error state, a {@code BooleanResult} containing the error value is
   * returned. If in error state, the original {@code BooleanResult} is
   * returned unaltered.
   *
   * @param supplier the supplier to run
   * @return the original {@code BooleanResult} unaltered if the given
   *         supplier returns success or the original {@code BooleanResult} is in
   *         error state, otherwise a {@code BooleanResult} containing the error
   *         value
   *         from the supplier result
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  BooleanResult<E> flatRunIfSuccess(Supplier<? extends VoidResult<? extends E>> supplier);

  /**
   * If in success state, verifies the boolean success value of this
   * {@code BooleanResult} by testing it with the given predicate. If the
   * predicate evaluates to false, a new {@code BooleanResult} is returned
   * containing the error value provided by the given error supplier. If the
   * predicate evaluates to true, or the {@code BooleanResult} already was in
   * error state, the original {@code BooleanResult} is returned unaltered.
   *
   * @param predicate     the predicate used to filter the boolean success value,
   *                      if success state
   * @param errorSupplier supplier providing the error if predicate evaluates
   *                      to false
   * @return the original {@code BooleanResult} unaltered, unless the predicate
   *         evaluates to false, then a new {@code BooleanResult} in error state
   *         is returned
   *         containing the supplied error value
   * @throws NullPointerException if the given predicate is {@code null} or
   *                              returns {@code null}, or the given error
   *                              supplier is {@code null} or
   *                              returns {@code null}
   */
  BooleanResult<E> filter(Predicate<Boolean> predicate,
      Supplier<? extends E> errorSupplier);

  /**
   * If in success state, verifies the success value of this
   * {@code BooleanResult} by mapping it to a {@code VoidResult}. If the
   * returned {@code VoidResult} is in error state, a new
   * {@code BooleanResult} is returned containing the error value of the
   * {@code VoidResult}. If the {@code VoidResult} is in success state, or the
   * {@code BooleanResult} already was in error state, the original
   * {@code BooleanResult} is returned unaltered.
   *
   * @param function the function applied to the success value, if success
   *                 state
   * @return the original {@code BooleanResult} unaltered, unless the
   *         {@code VoidResult} returned by the mapping function is in error
   *         state,
   *         then a new {@code BooleanResult} in error state is returned
   *         containing
   *         the error value from the {@code VoidResult}
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  BooleanResult<E> filter(Function<Boolean, ? extends VoidResult<? extends E>> function);

  /**
   * Retrieve a value from this {@code BooleanResult} by folding the states.
   * If in success state, return the value of applying the value function to
   * the boolean success value. If in error state, return the value of
   * applying the error function to the error value.
   *
   * @param <N>           the type of the retrieved value
   * @param valueFunction the mapping function to apply to the boolean success
   *                      value, if success state, may return {@code null}
   * @param errorFunction the mapping function to apply to the error value, if
   *                      error state, may return {@code null}
   * @return the folded value mapped from either the success value or error
   *         value, may be {@code null}
   * @throws NullPointerException if one of the given functions is
   *                              {@code null}
   */
  <N> N fold(Function<Boolean, ? extends N> valueFunction,
      Function<? super E, ? extends N> errorFunction);

  /**
   * Retrieve a value from this {@code BooleanResult} by folding the states.
   * If in success state with a success value of {@code true}, return the
   * value provided by the true-supplier. If in success state with a success
   * value of {@code false}, return the value provided by the false-supplier.
   * If in error state, return the value of applying the error function to
   * the error value.
   *
   * @param <N>           the type of the retrieved value
   * @param trueSupplier  the supplier to provide the value if a success value
   *                      of {@code true}, may return {@code null}
   * @param falseSupplier the supplier to provide the value if a success value
   *                      of {@code false}, may return {@code null}
   * @param errorFunction the mapping function to apply to the error value, if
   *                      error state, may return {@code null}
   * @return the folded value mapped from either the success value or error
   *         value, may be {@code null}
   * @throws NullPointerException if one of the given functions or the
   *                              supplier is {@code null}
   */
  <N> N fold(Supplier<? extends N> trueSupplier,
      Supplier<? extends N> falseSupplier,
      Function<? super E, ? extends N> errorFunction);

  /**
   * If in success state, returns the boolean success value, otherwise returns
   * {@code other}.
   *
   * @param other the value to be returned, if not in success state, may be
   *              {@code null}
   * @return the boolean success value, if success state, otherwise
   *         {@code other}
   */
  Boolean orElse(Boolean other);

  /**
   * If in success state, returns the boolean success value, otherwise returns
   * {@code true}.
   *
   * @return the boolean success value, if success state, otherwise
   *         {@code true}
   */
  boolean orElseTrue();

  /**
   * If in success state, returns the boolean success value, otherwise returns
   * {@code false}.
   *
   * @return the boolean success value, if success state, otherwise
   *         {@code false}
   */
  boolean orElseFalse();

  /**
   * If in success state, returns the boolean success value, otherwise returns
   * the value returned from the given function.
   *
   * @param function the mapping function to apply to the error value, if not
   *                 in success state, it may return {@code null}
   * @return the boolean success value, if success state, otherwise the result
   *         returned from the given function
   * @throws NullPointerException if the given function is {@code null}
   */
  Boolean orElseGet(Function<? super E, Boolean> function);

  /** Returns the success value or a supplied fallback. */
  default Boolean orElse(final Supplier<Boolean> supplier) {
    Objects.requireNonNull(supplier);
    return orElseGet(ignored -> supplier.get());
  }

  /** Returns the success value or maps the error to a fallback. */
  default Boolean orElse(final Function<? super E, Boolean> function) {
    return orElseGet(function);
  }

  /** Returns the success value, or {@code null} when in error state. */
  default Boolean get() {
    return orElse((Boolean) null);
  }

  /**
   * If in success state, returns the boolean success value, otherwise throws
   * the exception returned by the given function.
   *
   * @param <X>      type of the exception to be thrown
   * @param function the mapping function producing an exception by applying
   *                 the error value, if not in success state
   * @return the boolean success value, if success state
   * @throws X                    if in error state
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  <X extends Throwable> Boolean orElseThrow(
      Function<? super E, ? extends X> function) throws X;

  /** Returns the success value or throws the mapped exception. */
  default <X extends Throwable> Boolean getOrThrow(
      final Function<? super E, ? extends X> function) throws X {
    return orElseThrow(function);
  }

  /** Returns the success value or throws an IllegalStateException. */
  default Boolean getOrThrow(final String message) {
    Objects.requireNonNull(message);
    return orElseThrow(error -> new IllegalStateException(message + ": " + error));
  }

  /** Returns whether this result is in success state. */
  default boolean isSuccess() {
    return fold(value -> true, error -> false);
  }

  /** Returns whether this result is in error state. */
  default boolean isError() {
    return !isSuccess();
  }

  /** Returns a stream containing the success value, if present. */
  default Stream<Boolean> stream() {
    return fold(Stream::of, error -> Stream.empty());
  }

  /**
   * Transforms this {@code BooleanResult} to an {@code OptionalResult}. If in
   * success state, the {@code OptionalResult} will be in success state
   * containing the boolean success value from this {@code BooleanResult}. If
   * in error state, the {@code OptionalResult} will be in error state
   * containing the error value from this {@code BooleanResult}.
   * <p>
   * The returned {@code OptionalResult} will never be empty.
   *
   * @return an {@code OptionalResult} in success state containing the boolean
   *         success value from this {@code BooleanResult} or in error state
   *         containing the error value from this {@code BooleanResult}
   */
  OptionalResult<Boolean, E> toOptionalResult();

  /**
   * Transforms this {@code BooleanResult} to a {@code VoidResult}. If in
   * success state, the {@code VoidResult} will be in success state. If in
   * error state, the {@code VoidResult} will be in error state containing the
   * error value from this {@code BooleanResult}.
   *
   * @return a {@code VoidResult} either in success state or in error state
   *         containing the error value from this {@code BooleanResult}
   */
  VoidResult<E> toVoidResult();

  /**
   * Handle the given {@code Callable}. If the {@code Callable} executes
   * successfully, the {@code BooleanResult} will be in success state
   * containing the returned value. If the {@code Callable} throws an
   * exception, the {@code BooleanResult} will be in error state containing
   * the thrown exception.
   *
   * @param callable the {@code Callable} to handle
   * @return a {@code BooleanResult} either in success state containing the
   *         value from the {@code Callable}, or in error state containing the
   *         exception thrown by the {@code Callable}
   * @throws NullPointerException if the given callable is {@code null} or
   *                              returns {@code null}
   */
  static BooleanResult<Exception> handle(final Callable<Boolean> callable) {
    Objects.requireNonNull(callable);
    final Boolean value;
    try {
      value = callable.call();
    } catch (final Exception e) {
      return BooleanResult.error(e);
    }
    return BooleanResult.success(value);
  }

  /**
   * Handle the given {@code Callable}. If the {@code Callable} executes
   * successfully, the {@code BooleanResult} will be in success state
   * containing the returned value. If the {@code Callable} throws an
   * exception, the {@code BooleanResult} will be in error state containing
   * the result after mapping the exception with the given exception mapper
   * function.
   *
   * @param callable the {@code Callable} to handle
   * @param <E>      type of the error value after mapping a thrown exception
   * @return a {@code BooleanResult} either in success state containing the
   *         value from the {@code Callable}, or in error state containing the
   *         result
   *         after mapping the exception thrown by the {@code Callable}
   * @throws NullPointerException if the given callable is {@code null} or
   *                              returns {@code null}, or if the given exception
   *                              mapper function is
   *                              {@code null} or returns {@code null}
   */
  static <E> BooleanResult<E> handle(final Callable<Boolean> callable,
      final Function<Exception, E> exceptionMapper) {
    Objects.requireNonNull(exceptionMapper);
    return handle(callable).mapError(exceptionMapper);
  }

  record Success<ERR>(boolean value) implements BooleanResult<ERR> {

    @Override
    public <N> Result<N, ERR> map(final Function<Boolean, ? extends N> function) {
      return Result.success(function.apply(value));
    }

    @Override
    public <N> OptionalResult<N, ERR> mapToOptional(final Function<Boolean, ? extends Optional<? extends N>> function) {
      return OptionalResult.success(function.apply(value));
    }

    @Override
    public BooleanResult<ERR> mapToBoolean(final Function<Boolean, Boolean> function) {
      return BooleanResult.success(function.apply(value));
    }

    @Override
    public <N> BooleanResult<N> mapError(final Function<? super ERR, ? extends N> function) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> Result<N, ERR> flatMap(final Function<Boolean, Result<? extends N, ? extends ERR>> function) {
      return (Result<N, ERR>) function.apply(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> flatMapToOptionalResult(
        final Function<Boolean, OptionalResult<? extends N, ? extends ERR>> function) {
      return (OptionalResult<N, ERR>) function.apply(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BooleanResult<ERR> flatMapToBooleanResult(final Function<Boolean, BooleanResult<? extends ERR>> function) {
      return (BooleanResult<ERR>) function.apply(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public VoidResult<ERR> flatMapToVoidResult(final Function<Boolean, VoidResult<? extends ERR>> function) {
      return (VoidResult<ERR>) function.apply(value);
    }

    @Override
    public BooleanResult<ERR> recover(final Function<ERR, Boolean> function) {
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> flatRecover(final Function<ERR, BooleanResult<? extends ERR>> function) {
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> consume(final Consumer<Boolean> consumer) {
      consumer.accept(value);
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> consumeError(final Consumer<? super ERR> errorConsumer) {
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> consumeEither(final Consumer<Boolean> valueConsumer,
        final Consumer<? super ERR> errorConsumer) {
      valueConsumer.accept(value);
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> consumeEither(final Runnable trueRunnable, final Runnable falseRunnable,
        final Consumer<? super ERR> errorConsumer) {
      Objects.requireNonNull(trueRunnable);
      Objects.requireNonNull(falseRunnable);
      Objects.requireNonNull(errorConsumer);

      if (value) {
        trueRunnable.run();
      } else {
        falseRunnable.run();
      }
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> flatConsume(final Function<Boolean, ? extends VoidResult<? extends ERR>> function) {
      Objects.requireNonNull(function);
      final VoidResult<? extends ERR> apply = function.apply(value);
      return apply.fold(() -> this, BooleanResult::error);
    }

    @Override
    public BooleanResult<ERR> runIfSuccess(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runIfTrue(final Runnable runnable) {
      if (value) {
        runnable.run();
      }
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runIfFalse(final Runnable runnable) {
      if (!value) {
        runnable.run();
      }
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runIfError(final Runnable runnable) {
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runEither(final Runnable successRunnable, final Runnable errorRunnable) {
      successRunnable.run();
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runEither(final Runnable trueRunnable, final Runnable falseRunnable,
        final Runnable errorRunnable) {
      if (value) {
        trueRunnable.run();
      } else {
        falseRunnable.run();
      }
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runAlways(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> flatRunIfSuccess(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      final VoidResult<? extends ERR> result = supplier.get();
      return result.fold(() -> this, BooleanResult::error);
    }

    @Override
    public BooleanResult<ERR> filter(final Predicate<Boolean> predicate, final Supplier<? extends ERR> errorSupplier) {
      if (predicate.test(value)) {
        return safeCast();
      } else {
        return BooleanResult.error(errorSupplier.get());
      }
    }

    @Override
    public BooleanResult<ERR> filter(final Function<Boolean, ? extends VoidResult<? extends ERR>> function) {
      final VoidResult<? extends ERR> apply = function.apply(value);
      return apply.fold(() -> this, BooleanResult::error);
    }

    @Override
    public <N> N fold(final Function<Boolean, ? extends N> valueFunction,
        final Function<? super ERR, ? extends N> errorFunction) {
      return valueFunction.apply(value);
    }

    @Override
    public <N> N fold(final Supplier<? extends N> trueSupplier, final Supplier<? extends N> falseSupplier,
        final Function<? super ERR, ? extends N> errorFunction) {
      Objects.requireNonNull(falseSupplier);
      Objects.requireNonNull(trueSupplier);
      Objects.requireNonNull(errorFunction);
      if (value) {
        return trueSupplier.get();
      } else {
        return falseSupplier.get();
      }
    }

    @Override
    public Boolean orElse(final Boolean other) {
      return value;
    }

    @Override
    public boolean orElseTrue() {
      return value;
    }

    @Override
    public boolean orElseFalse() {
      return value;
    }

    @Override
    public Boolean orElseGet(final Function<? super ERR, Boolean> function) {
      return value;
    }

    @Override
    public <X extends Throwable> Boolean orElseThrow(final Function<? super ERR, ? extends X> function) throws X {
      return value;
    }

    @Override
    public OptionalResult<Boolean, ERR> toOptionalResult() {
      return OptionalResult.success(value);
    }

    @Override
    public VoidResult<ERR> toVoidResult() {
      return VoidResult.success();
    }

    @SuppressWarnings("unchecked")
    private <E1> Success<E1> safeCast() {
      return (Success<E1>) this;
    }
  }

  record Error<ERR>(ERR error) implements BooleanResult<ERR> {

    @Override
    public <N> Result<N, ERR> map(final Function<Boolean, ? extends N> function) {
      return Result.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> mapToOptional(final Function<Boolean, ? extends Optional<? extends N>> function) {
      return OptionalResult.error(error);
    }

    @Override
    public BooleanResult<ERR> mapToBoolean(final Function<Boolean, Boolean> function) {
      return safeCast();
    }

    @Override
    public <N> BooleanResult<N> mapError(final Function<? super ERR, ? extends N> function) {
      Objects.requireNonNull(function);
      return BooleanResult.error(function.apply(error));
    }

    @Override
    public <N> Result<N, ERR> flatMap(final Function<Boolean, Result<? extends N, ? extends ERR>> function) {
      return Result.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> flatMapToOptionalResult(
        final Function<Boolean, OptionalResult<? extends N, ? extends ERR>> function) {
      return OptionalResult.error(error);
    }

    @Override
    public BooleanResult<ERR> flatMapToBooleanResult(final Function<Boolean, BooleanResult<? extends ERR>> function) {
      return BooleanResult.error(error);
    }

    @Override
    public VoidResult<ERR> flatMapToVoidResult(final Function<Boolean, VoidResult<? extends ERR>> function) {
      return VoidResult.error(error);
    }

    @Override
    public BooleanResult<ERR> recover(final Function<ERR, Boolean> function) {
      return BooleanResult.success(function.apply(error));
    }

    @SuppressWarnings("unchecked")
    @Override
    public BooleanResult<ERR> flatRecover(final Function<ERR, BooleanResult<? extends ERR>> function) {
      return (BooleanResult<ERR>) function.apply(error);
    }

    @Override
    public BooleanResult<ERR> consume(final Consumer<Boolean> consumer) {
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> consumeError(final Consumer<? super ERR> errorConsumer) {
      errorConsumer.accept(error);
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> consumeEither(final Consumer<Boolean> valueConsumer,
        final Consumer<? super ERR> errorConsumer) {
      errorConsumer.accept(error);
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> consumeEither(final Runnable trueRunnable, final Runnable falseRunnable,
        final Consumer<? super ERR> errorConsumer) {
      errorConsumer.accept(error);
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> flatConsume(final Function<Boolean, ? extends VoidResult<? extends ERR>> function) {
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runIfSuccess(final Runnable runnable) {
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runIfTrue(final Runnable runnable) {
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runIfFalse(final Runnable runnable) {
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runIfError(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runEither(final Runnable successRunnable, final Runnable errorRunnable) {
      errorRunnable.run();
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runEither(final Runnable trueRunnable, final Runnable falseRunnable,
        final Runnable errorRunnable) {
      Objects.requireNonNull(trueRunnable);
      Objects.requireNonNull(falseRunnable);
      errorRunnable.run();
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> runAlways(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> flatRunIfSuccess(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> filter(final Predicate<Boolean> predicate, final Supplier<? extends ERR> errorSupplier) {
      return safeCast();
    }

    @Override
    public BooleanResult<ERR> filter(final Function<Boolean, ? extends VoidResult<? extends ERR>> function) {
      return safeCast();
    }

    @Override
    public <N> N fold(final Function<Boolean, ? extends N> valueFunction,
        final Function<? super ERR, ? extends N> errorFunction) {
      return errorFunction.apply(error);
    }

    @Override
    public <N> N fold(final Supplier<? extends N> trueSupplier, final Supplier<? extends N> falseSupplier,
        final Function<? super ERR, ? extends N> errorFunction) {
      Objects.requireNonNull(trueSupplier);
      Objects.requireNonNull(falseSupplier);
      return errorFunction.apply(error);
    }

    @Override
    public Boolean orElse(final Boolean other) {
      return other;
    }

    @Override
    public boolean orElseTrue() {
      return true;
    }

    @Override
    public boolean orElseFalse() {
      return false;
    }

    @Override
    public Boolean orElseGet(final Function<? super ERR, Boolean> function) {
      return function.apply(error);
    }

    @Override
    public <X extends Throwable> Boolean orElseThrow(final Function<? super ERR, ? extends X> function) throws X {
      throw function.apply(error);
    }

    @Override
    public OptionalResult<Boolean, ERR> toOptionalResult() {
      return OptionalResult.error(error);
    }

    @Override
    public VoidResult<ERR> toVoidResult() {
      return VoidResult.error(error);
    }

    @SuppressWarnings("unchecked")
    private <E1> Error<E1> safeCast() {
      return (Error<E1>) this;
    }
  }

}
