package com.lemonlightmc.zenith.additive.results;

import java.util.ArrayList;
import java.util.List;
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
 * non-{@code null} success value, or in error state containing a
 * non-{@code null} error value.
 * <p>
 * A variable whose type is {@code Result} should never itself be {@code null},
 * it should always point to an {@code Result} instance.
 *
 * @param <T> the type of the success value
 * @param <E> the type of the error value
 */
public sealed interface Result<T, E> {

  /**
   * Returns a {@code Result} in success state containing the given
   * non-{@code null} value as success value.
   *
   * @param value the success value, which must be non-{@code null}
   * @param <T>   the type of the success value
   * @param <E>   the type of the error value
   * @return a {@code Result} in success state containing the given success
   *         value
   * @throws NullPointerException if given success value is {@code null}
   */
  static <T, E> Result<T, E> success(final T value) {
    return new Success<>(Objects.requireNonNull(value));
  }

  /**
   * Returns a {@code Result} in error state containing the given
   * non-{@code null} value as error value.
   *
   * @param value the error value, which must be non-{@code null}
   * @param <T>   the type of the success value
   * @param <E>   the type of the error value
   * @return a {@code Result} in error state containing the given error value
   * @throws NullPointerException if given error value is {@code null}
   */
  static <T, E> Result<T, E> error(final E value) {
    return new Error<>(Objects.requireNonNull(value));
  }

  /**
   * Returns a {@code Result} in success state containing the given
   * non-{@code null} value as success value.
   *
   * @param value the success value, which must be non-{@code null}
   * @param <T>   the type of the success value
   * @param <E>   the type of the error value
   * @return a {@code Result} in success state containing the given success
   *         value
   * @throws NullPointerException if given success value is {@code null}
   */
  static <T, E> Result<T, E> of(final T value) {
    return success(value);
  }

  /**
   * Returns a {@code VoidResult} in success state.
   *
   * @param <E> the type of the error value
   * @return a {@code VoidResult} in success state
   */
  static <E> VoidResult<E> of() {
    return VoidResult.success();
  }

  /**
   * Handle the given {@code Callable}. If the {@code Callable} executes
   * successfully, the {@code Result} will be in success state containing the
   * returned value. If the {@code Callable} throws an exception, the
   * {@code Result} will be in error state containing the thrown exception.
   *
   * @param callable the {@code Callable} to handle
   * @param <T>      type of the return value of the {@code Callable}
   * @return a {@code Result} either in success state containing the value
   *         from the {@code Callable}, or in error state containing the exception
   *         thrown by the {@code Callable}
   * @throws NullPointerException if the given callable is {@code null} or
   *                              returns {@code null}
   */

  static <T> Result<T, Exception> of(final Callable<T> callable) {
    Objects.requireNonNull(callable);
    try {
      return Result.success(callable.call());
    } catch (final Exception e) {
      return Result.error(e);
    }
  }

  /**
   * Handle the given {@code Callable}. If the {@code Callable} executes
   * successfully, the {@code Result} will be in success state containing the
   * returned value. If the {@code Callable} throws an exception, the
   * {@code Result} will be in error state containing the result after mapping
   * the exception with the given exception mapper function.
   *
   * @param callable the {@code Callable} to handle
   * @param <T>      type of the return value of the {@code Callable}
   * @param <E>      type of the error value after mapping a thrown exception
   * @return a {@code Result} either in success state containing the value
   *         from the {@code Callable}, or in error state containing the result
   *         after
   *         mapping the exception thrown by the {@code Callable}
   * @throws NullPointerException if the given callable is {@code null} or
   *                              returns {@code null}, or if the given exception
   *                              mapper function is
   *                              {@code null} or returns {@code null}
   */
  static <T, E> Result<T, E> of(final Callable<T> callable,
      final Function<Exception, E> exceptionMapper) {
    Objects.requireNonNull(callable);
    Objects.requireNonNull(exceptionMapper);
    try {
      return Result.success(callable.call());
    } catch (final Exception e) {
      return Result.error(exceptionMapper.apply(e));
    }
  }

  /**
   * Returns a {@code Result} in success state containing a list of all
   * success values from the given {@code Result}s, or the first error
   * {@code Result} if any of the given {@code Result}s is in error state.
   * 
   * @apiNote The returned list is unmodifiable and will not contain any
   *          {@code null}
   * @param results the {@code Result}s to collect success values from
   * @throws NullPointerException if the given {@code Iterable} is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T, E> Result<List<T>, E> all(
      final Iterable<? extends Result<? extends T, ? extends E>> results) {
    Objects.requireNonNull(results);

    final List<T> values = new ArrayList<>();
    for (final Result<? extends T, ? extends E> result : results) {
      if (result == null) {
        continue;
      }
      if (result.isError()) {
        return (Result<List<T>, E>) result;
      }
      values.add((T) ((Success<?, ?>) result).value());
    }
    return success(List.copyOf(values));
  }

  /**
   * Returns a {@code Result} in success state containing a list of all
   * success values from the given {@code Result}s, or the first error
   * {@code Result} if any of the given {@code Result}s is in error state.
   * 
   * @apiNote The returned list is unmodifiable and will not contain any
   *          {@code null}
   * @param results the {@code Result}s to collect success values from
   * @throws NullPointerException if the given {@code Iterable} is {@code null}
   */
  @SuppressWarnings("unchecked")
  @SafeVarargs
  static <T, E> Result<List<T>, E> all(final Result<? extends T, ? extends E>... results) {
    Objects.requireNonNull(results);

    final List<T> values = new ArrayList<>();
    for (final Result<? extends T, ? extends E> result : results) {
      if (result == null) {
        continue;
      }
      if (result.isError()) {
        return (Result<List<T>, E>) result;
      }
      values.add((T) ((Success<?, ?>) result).value());
    }
    return success(List.copyOf(values));
  }

  /**
   * Returns the first successful {@code Result}, or the last error when none
   * succeeds.
   * At least one result must be supplied.
   * 
   * @apiNote The returned list is unmodifiable and will not contain any
   *          {@code null}
   * @param results the {@code Result}s to collect success values from
   * @throws NullPointerException if the given {@code Iterable} is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T, E> Result<T, E> any(
      final Iterable<? extends Result<? extends T, ? extends E>> results) {
    Objects.requireNonNull(results);

    Result<T, E> lastError = null;
    for (final Result<? extends T, ? extends E> result : results) {
      if (result == null) {
        continue;
      }
      if (result.isSuccess()) {
        return (Result<T, E>) result;
      }
      lastError = (Result<T, E>) result;
    }
    return lastError;
  }

  /**
   * Returns the first successful {@code Result}, or the last error when none
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
  static <T, E> Result<T, E> any(final Result<? extends T, ? extends E>... results) {
    Objects.requireNonNull(results);

    Result<T, E> lastError = null;
    for (final Result<? extends T, ? extends E> result : results) {
      if (result == null) {
        continue;
      }
      if (result.isSuccess()) {
        return (Result<T, E>) result;
      }
      lastError = (Result<T, E>) result;
    }
    return lastError;
  }

  /**
   * If in success state, returns a {@code Result} containing the result of
   * applying the given mapping function to the success value, otherwise
   * returns the unaltered {@code Result} in error state.
   *
   * @param function the mapping function to apply to the success value, if
   *                 success state
   * @param <N>      the type of the value returned from the mapping function
   * @return a {@code Result} containing the result of applying the mapping
   *         function to the success value of this {@code Result}, if in success
   *         state, otherwise the unaltered {@code Result} in error state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> Result<N, E> map(Function<? super T, ? extends N> function);

  /**
   * If in success state, returns a {@code Result} containing the result of
   * the given supplier, otherwise returns the unaltered {@code Result}
   * in error state.
   *
   * @param supplier the supplier of the success value, if
   *                 success state
   * @param <N>      the type of the value returned from the supplier
   * @return a {@code Result} containing the result of applying the mapping
   *         function to the success value of this {@code Result}, if in success
   *         state, otherwise the unaltered {@code Result} in error state
   * @throws NullPointerException if the given supplier is
   *                              {@code null} or returns {@code null}
   */
  <N> Result<N, E> map(final Supplier<? extends N> supplier);

  /**
   * If in success state, returns a {@code OptionalResult} containing the
   * result of applying the given mapping function to the success value,
   * otherwise returns a {@code OptionalResult} containing the error value of
   * this {@code Result}.
   *
   * @param function the mapping function to apply to the success value, if
   *                 success state
   * @param <N>      the type of the success value which may be present in the
   *                 {@code Optional} returned from the mapping function
   * @return a {@code OptionalResult} containing the result of applying the
   *         mapping function to the success value of this {@code Result}, if in
   *         success state, otherwise a {@code OptionalResult} containing the
   *         error
   *         value of this {@code Result}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> OptionalResult<N, E> mapToOptional(Function<? super T, Optional<N>> function);

  /**
   * If in success state, returns a {@code BooleanResult} containing the
   * result of applying the given mapping function to the success value,
   * otherwise returns a {@code BooleanResult} containing the error value of
   * this {@code Result}.
   *
   * @param function the mapping function to apply to the success value, if
   *                 success state
   * @return a {@code BooleanResult} containing the result of applying the
   *         mapping function to the success value of this {@code Result}, if in
   *         success state, otherwise a {@code BooleanResult} containing the error
   *         value of this {@code Result}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  BooleanResult<E> mapToBoolean(Function<? super T, Boolean> function);

  /**
   * If in error state, returns a {@code Result} containing the result of
   * applying the given mapping function to the error value, otherwise
   * returns the unaltered {@code Result} in success state.
   *
   * @param function the mapping function to apply to the error value, if
   *                 error state
   * @param <N>      the type of the value returned from the mapping function
   * @return a {@code Result} containing the result of applying the mapping
   *         function to the error value of this {@code Result}, if in error
   *         state, otherwise the unaltered {@code Result} in success state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> Result<T, N> mapError(Function<? super E, ? extends N> function);

  /**
   * If in success state, returns the {@code Result} from applying the given
   * mapping function to the success value, otherwise returns the unaltered
   * {@code Result} in error state.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code Result} returned by the mapping function
   * @param function the mapping function to apply to the success value, if
   *                 success state
   * @return the {@code Result} returned from the mapping function, if in
   *         success state, otherwise the unaltered {@code Result} in error state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> Result<N, E> flatMap(
      Function<? super T, ? extends Result<? extends N, ? extends E>> function);

  /**
   * If in success state, returns the {@code Result} from applying the given
   * mapping function to the success value and the given argument, otherwise
   * returns the unaltered {@code Result} in error state.
   * 
   * @param <N>      the type of success value which may be present in the
   *                 {@code Result} returned by the mapping function
   * @param <U>      the type of the argument to be passed to the mapping function
   * @param function the mapping function to apply to the success value and the
   *                 given argument, if success state
   * @return the {@code Result} returned from the mapping function, if in
   *         success state, otherwise the unaltered {@code Result} in error state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N, U> Result<N, E> flatMap(
      final BiFunction<? super T, ? super U, ? extends Result<? extends N, ? extends E>> function,
      final U argument);

  /**
   * If in success state, returns the {@code Result} from the given supplier,
   * otherwise returns the unaltered {@code Result} in error state.
   *
   * @param supplier the supplier to provide the {@code Result}, if success
   *                 state
   * @param <N>      the type of success value which may be present in the
   *                 {@code Result} provided by the supplier
   * @return the {@code Result} provided by the supplier, if in success state,
   *         otherwise the unaltered {@code Result} in error state
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  <N> Result<N, E> flatMap(final Supplier<? extends Result<? extends N, ? extends E>> supplier);

  /**
   * If in success state, returns the {@code OptionalResult} from applying
   * the given mapping function to the success value, otherwise returns a
   * {@code OptionalResult} containing the error value of this {@code Result}.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code OptionalResult} returned by the mapping function
   * @param function the mapping function to apply to the success value, if
   *                 success state
   * @return the {@code OptionalResult} returned from the mapping function, if
   *         in success state, otherwise a {@code OptionalResult} containing the
   *         error
   *         value of this {@code Result}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> OptionalResult<N, E> flatMapToOptionalResult(
      Function<? super T, OptionalResult<N, E>> function);

  /**
   * If in success state, returns the {@code BooleanResult} from applying
   * the given mapping function to the success value, otherwise returns a
   * {@code BooleanResult} containing the error value of this {@code Result}.
   *
   * @param function the mapping function to apply to the success value, if
   *                 success state
   * @return the {@code BooleanResult} returned from the mapping function, if
   *         in success state, otherwise a {@code BooleanResult} containing the
   *         error
   *         value of this {@code Result}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  BooleanResult<E> flatMapToBooleanResult(
      Function<? super T, BooleanResult<E>> function);

  /**
   * If in success state, returns the {@code VoidResult} from applying the
   * given mapping function to the success value, otherwise returns a
   * {@code VoidResult} containing the error value of this {@code Result}.
   *
   * @param function the mapping function to apply to the success value, if
   *                 success state
   * @return the {@code VoidResult} returned from the mapping function, if in
   *         success state, otherwise a {@code VoidResult} containing the error
   *         value
   *         of this {@code Result}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  VoidResult<E> flatMapToVoidResult(
      Function<? super T, VoidResult<E>> function);

  /**
   * If in success state, returns a {@code Result} containing the value
   * provided by the given supplier, otherwise returns a {@code Result}
   * containing the error value of this {@code Result}.
   *
   * @param supplier the supplier to provide the value if success state, may
   *                 not be {@code null}
   * @param <N>      the type of the value provided by the supplier
   * @return a {@code Result} containing the value provided by the given
   *         supplier, if in success state, otherwise a {@code Result} containing
   *         the
   *         error value of this {@code Result}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  <N> Result<N, E> replace(Supplier<? extends N> supplier);

  /**
   * If in success state, returns a {@code OptionalResult} containing the
   * optional value provided by given supplier, otherwise returns a
   * {@code OptionalResult} containing the error value of this
   * {@code Result}.
   *
   * @param supplier the supplier to provide the optional value if success
   *                 state, may not be {@code null}
   * @param <N>      the type of the value which may be present in the
   *                 {@code Optional} provided by the supplier
   * @return a {@code OptionalResult} containing the optional value provided
   *         by the given supplier, if in success state, otherwise a
   *         {@code OptionalResult} containing the error value of this
   *         {@code Result}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  <N> OptionalResult<N, E> replaceWithOptional(
      Supplier<Optional<? extends N>> supplier);

  /**
   * If in success state, returns a {@code BooleanResult} containing the
   * boolean value provided by the given supplier, otherwise returns a
   * {@code BooleanResult} containing the error value of this
   * {@code Result}.
   *
   * @param supplier the supplier to provide the boolean value if success
   *                 state, may not be {@code null}
   * @return a {@code BooleanResult} containing the boolean value provided by
   *         the given supplier, if in success state, otherwise a
   *         {@code BooleanResult} containing the error value of this
   *         {@code Result}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  BooleanResult<E> replaceWithBoolean(Supplier<Boolean> supplier);

  /**
   * If in success state, returns the {@code Result} provided by the given
   * supplier, otherwise returns a {@code Result} containing the error value
   * of this {@code Result}.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code Result} provided by the supplier
   * @param supplier the supplier to provide the {@code Result}, if success
   *                 state
   * @return the {@code Result} provided by the supplier, if in success state,
   *         otherwise a {@code Result} containing the error value of this
   *         {@code Result}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  <N> Result<N, E> flatReplace(
      Supplier<Result<? extends N, ? extends E>> supplier);

  /**
   * If in success state, returns the {@code OptionalResult} provided by the
   * given supplier, otherwise returns an {@code OptionalResult} containing
   * the error value of this {@code Result}.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code OptionalResult} provided by the supplier
   * @param supplier the supplier to provide the {@code OptionalResult}, if
   *                 success state
   * @return the {@code OptionalResult} provided by the supplier, if in
   *         success state, otherwise an {@code OptionalResult} containing the
   *         error
   *         value of this {@code Result}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  <N> OptionalResult<N, E> flatReplaceToOptionalResult(
      Supplier<OptionalResult<? extends N, ? extends E>> supplier);

  /**
   * If in success state, returns the {@code BooleanResult} provided by the
   * given supplier, otherwise returns a {@code BooleanResult} containing the
   * error value of this {@code Result}.
   *
   * @param supplier the supplier to provide the {@code BooleanResult}, if
   *                 success state
   * @return the {@code BooleanResult} provided by the supplier, if in
   *         success state, otherwise a {@code BooleanResult} containing the error
   *         value of this {@code Result}
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
   * If in error state, returns a {@code Result} with the success value from
   * applying the given mapping function to the error value, otherwise returns
   * the unaltered {@code Result} in success state.
   *
   * @param function the mapping function to apply to the error value to
   *                 convert to a new success value, if error state
   * @return A {@code Result} containing the value from the mapping function,
   *         if in error state, otherwise the unaltered {@code Result} in success
   *         state
   */
  Result<T, E> recover(Function<E, T> function);

  /**
   * If in error state, returns the {@code Result} from applying the given
   * mapping function to the error value, otherwise returns the unaltered
   * {@code Result} in success state.
   *
   * @param function the mapping function to apply to the error value to
   *                 convert to a new {@code Result}, if error state
   * @param <N>      the type of success value which may be present in the
   *                 {@code Result} returned by the mapping function
   * @return the {@code Result} returned from the mapping function, if in
   *         error state, otherwise the unaltered {@code Result} in success state
   */
  <N> Result<N, E> flatRecover(
      Function<E, Result<? extends N, ? extends E>> function);

  /**
   * If in success state, applies the success value to the given consumer,
   * otherwise does nothing.
   *
   * @param consumer the consumer which accepts the success value
   * @return the original {@code Result} unaltered
   * @throws NullPointerException if the given consumer is {@code null}
   */
  Result<T, E> consume(Consumer<? super T> consumer);

  /**
   * If in error state, applies the error value to the given consumer,
   * otherwise does nothing.
   *
   * @param errorConsumer the consumer which accepts the error value
   * @return the original {@code Result} unaltered
   * @throws NullPointerException if the given consumer is {@code null}
   */
  Result<T, E> consumeError(Consumer<? super E> errorConsumer);

  /**
   * If in success state, applies the success value to the given value
   * consumer. If in error state, applies the error value to the given error
   * consumer.
   *
   * @param valueConsumer the consumer which accepts the success value
   * @param errorConsumer the consumer which accepts the error value
   * @return the original {@code Result} unaltered
   * @throws NullPointerException if one of the given consumers is
   *                              {@code null}
   */
  Result<T, E> consumeEither(Consumer<? super T> valueConsumer,
      Consumer<? super E> errorConsumer);

  /**
   * If in success state, applies the success value to the given function. If
   * the function returns a {@code VoidResult} in success state, the original
   * {@code Result} is returned unaltered. If the function returns a
   * {@code VoidResult} in error state, a {@code Result} containing the error
   * value is returned. If in error state, the original {@code Result} is
   * returned unaltered.
   *
   * @param function the function which accepts the success value
   * @return the original {@code Result} unaltered if the given function
   *         returns success or the original {@code Result} is in error state,
   *         otherwise a {@code Result} containing the error value from the
   *         function
   *         result
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  Result<T, E> flatConsume(Function<? super T, ? extends VoidResult<? extends E>> function);

  /**
   * If in success state, runs the given runnable, otherwise does nothing.
   *
   * @param runnable the runnable to run if success state
   * @return the original {@code Result} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  Result<T, E> runIfSuccess(Runnable runnable);

  /**
   * If in error state, runs the given runnable, otherwise does nothing.
   *
   * @param runnable the runnable to run if error state
   * @return the original {@code Result} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  Result<T, E> runIfError(Runnable runnable);

  /**
   * If in success state, runs the given success runnable. If in error state,
   * runs the given error runnable.
   *
   * @param successRunnable the runnable to run if success state
   * @param errorRunnable   the runnable to run if error state
   * @return the original {@code Result} unaltered
   * @throws NullPointerException if one of the given runnables is {@code null}
   */
  Result<T, E> runEither(Runnable successRunnable, Runnable errorRunnable);

  /**
   * Runs the given runnable, no matter the state.
   *
   * @param runnable the runnable to run
   * @return the original {@code Result} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  Result<T, E> runAlways(Runnable runnable);

  /**
   * If in success state, runs the given supplier. If the supplier returns a
   * {@code VoidResult} in success state, the original {@code Result} is
   * returned unaltered. If the supplier returns a {@code VoidResult} in error
   * state, a {@code Result} containing the error value is returned. If in
   * error state, the original {@code Result} is returned unaltered.
   *
   * @param supplier the supplier to run
   * @return the original {@code Result} unaltered if the given supplier
   *         returns success or the original {@code Result} is in error state,
   *         otherwise a {@code Result} containing the error value from the
   *         supplier
   *         result
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  Result<T, E> flatRunIfSuccess(Supplier<? extends VoidResult<? extends E>> supplier);

  /**
   * If in success state, verifies the success value of this {@code Result} by
   * testing it with the given predicate. If the predicate evaluates to false,
   * a new {@code Result} is returned containing the error value provided by
   * the given error supplier. If the predicate evaluates to true, or the
   * {@code Result} already was in error state, the original {@code Result} is
   * returned unaltered.
   *
   * @param predicate     the predicate used to filter the success value, if
   *                      success state
   * @param errorSupplier supplier providing the error if predicate evaluates
   *                      to false
   * @return the original {@code Result} unaltered, unless the predicate
   *         evaluates to false, then a new {@code Result} in error state is
   *         returned
   *         containing the supplied error value
   * @throws NullPointerException if the given predicate is {@code null} or
   *                              returns {@code null}, or the given error
   *                              supplier is {@code null} or
   *                              returns {@code null}
   */
  Result<T, E> filter(Predicate<? super T> predicate,
      Supplier<? extends E> errorSupplier);

  /**
   * If in success state, verifies the success value of this {@code Result} by
   * mapping it to a {@code Result}. If the returned {@code Result} is
   * in error state, a new {@code Result} is returned containing the error
   * value of the {@code Result}. If the {@code Result} is in success
   * state, or the {@code Result} already was in error state, the original
   * {@code Result} is returned unaltered.
   *
   * @param function the function applied to the success value, if success
   *                 state
   * @return the original {@code Result} unaltered, unless the
   *         {@code Result} returned by the mapping function is in error
   *         state,
   *         then a new {@code Result} in error state is returned containing the
   *         error
   *         value from the {@code Result}
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  Result<T, E> filter(Function<? super T, ? extends VoidResult<? extends E>> function);

  /**
   * Retrieve a value from this {@code Result} by folding the states. If in
   * success state, return the value of applying the value function to the
   * success value. If in error state, return the value of applying the error
   * function to the error value.
   *
   * @param <N>           the type of retrieved value
   * @param valueFunction the mapping function to apply to the success value,
   *                      if success state, may return {@code null}
   * @param errorFunction the mapping function to apply to the error value, if
   *                      error state, may return {@code null}
   * @return the folded value mapped from either the success value or error
   *         value, may be {@code null}
   * @throws NullPointerException if one of the given functions is
   *                              {@code null}
   */
  <N> N fold(Function<? super T, ? extends N> valueFunction,
      Function<? super E, ? extends N> errorFunction);

  /**
   * If in success state, returns the success value, otherwise returns
   * {@code other}.
   *
   * @param other the value to be returned, if not in success state, may be
   *              {@code null}
   * @return the success value, if success state, otherwise {@code other}
   */
  T orElse(T other);

  /**
   * If in success state, returns the success value, otherwise returns the
   * value returned from the given supplier.
   *
   * @param function the mapping function to apply to the error value, if not
   *                 in success state, it may return {@code null}
   * @return the success value, if success state, otherwise the result
   *         returned from the given function
   * @throws NullPointerException if the given function is {@code null}
   */
  T orElse(final Supplier<? extends T> supplier);

  /**
   * If in success state, returns the success value, otherwise returns the
   * value returned from the given function.
   *
   * @param function the mapping function to apply to the error value, if not
   *                 in success state, it may return {@code null}
   * @return the success value, if success state, otherwise the result
   *         returned from the given function
   * @throws NullPointerException if the given function is {@code null}
   */
  T orElse(Function<? super E, ? extends T> function);

  /**
   * If in success state, returns the success value, otherwise returns
   * {@code null}.
   */
  T get();

  /**
   * If in success state, returns the success value, otherwise throws the
   * exception returned by the given function.
   *
   * @param <X>      type of the exception to be thrown
   * @param function the mapping function producing an exception by applying
   *                 the error value, if not in success state
   * @return the success value, if success state
   * @throws X                    if in error state
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  <X extends Throwable> T getOrThrow(
      Function<? super E, ? extends X> function) throws X;

  /**
   * If in success state, returns the success value, otherwise throws an
   * IllegalStateException with the given message and the error value.
   *
   * @param function the mapping function producing an exception by applying
   *                 the error value, if not in success state
   * @return the success value, if success state
   * @throws IllegalStateException if in error state
   * @throws NullPointerException  if the given string is {@code null}
   */
  T getOrThrow(final String message) throws IllegalStateException;

  /**
   * Returns whether this result is in the success state.
   */
  boolean isSuccess();

  /**
   * Returns whether this result is in the error state.
   */
  boolean isError();

  /**
   * Returns a sequential {@code Stream} containing the success value if this
   * result is in success state, otherwise returns an empty {@code Stream}.
   *
   * @return a sequential {@code Stream} containing the success value if this
   *         result is in success state, otherwise an empty {@code Stream}
   */
  Stream<T> stream();

  /**
   * Transforms this {@code Result} to an {@code OptionalResult}. If in
   * success state, the {@code OptionalResult} will be in success state
   * containing the success value from this {@code Result}. If in error state,
   * the {@code OptionalResult} will be in error state containing the error
   * value from this {@code Result}.
   * <p>
   * The returned {@code OptionalResult} will never be empty.
   *
   * @return an {@code OptionalResult} in success state containing the success
   *         value from this {@code Result} or in error state containing the error
   *         value from this {@code Result}
   */
  OptionalResult<T, E> toOptionalResult();

  /**
   * Transforms this {@code Result} to a {@code VoidResult}. If in success
   * state, the {@code VoidResult} will be in success state. If in error
   * state, the {@code VoidResult} will be in error state containing the
   * error value from this {@code Result}.
   *
   * @return a {@code VoidResult} either in success state or in error state
   *         containing the error value from this {@code Result}
   */
  VoidResult<E> toVoidResult();

  record Success<S, ERR>(S value) implements Result<S, ERR> {
    public Success(final S value) {
      this.value = Objects.requireNonNull(value);
    }

    @Override
    public <N> Result<N, ERR> map(final Function<? super S, ? extends N> function) {
      return new Success<>(function.apply(value));
    }

    @Override
    public <N> Result<N, ERR> map(final Supplier<? extends N> supplier) {
      return Result.success(Objects.requireNonNull(supplier).get());
    }

    @Override
    public <N> OptionalResult<N, ERR> mapToOptional(final Function<? super S, Optional<N>> function) {
      return OptionalResult.success(function.apply(value));
    }

    @Override
    public BooleanResult<ERR> mapToBoolean(final Function<? super S, Boolean> function) {
      return BooleanResult.success(function.apply(value));
    }

    @Override
    public <N> Result<S, N> mapError(final Function<? super ERR, ? extends N> function) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> Result<N, ERR> flatMap(
        final Function<? super S, ? extends Result<? extends N, ? extends ERR>> function) {
      return (Result<N, ERR>) Objects.requireNonNull(function.apply(value));
    }

    @Override
    public <N, U> Result<N, ERR> flatMap(
        final BiFunction<? super S, ? super U, ? extends Result<? extends N, ? extends ERR>> function,
        final U argument) {
      return flatMap(current -> Objects.requireNonNull(function).apply(current, argument));
    }

    @Override
    public <N> Result<N, ERR> flatMap(
        final Supplier<? extends Result<? extends N, ? extends ERR>> supplier) {
      return flatMap(ignored -> Objects.requireNonNull(supplier).get());
    }

    @Override
    public <N> OptionalResult<N, ERR> flatMapToOptionalResult(
        final Function<? super S, OptionalResult<N, ERR>> function) {
      return function.apply(value);
    }

    @Override
    public BooleanResult<ERR> flatMapToBooleanResult(final Function<? super S, BooleanResult<ERR>> function) {
      return function.apply(value);
    }

    @Override
    public VoidResult<ERR> flatMapToVoidResult(final Function<? super S, VoidResult<ERR>> function) {
      return function.apply(value);
    }

    @Override
    public <N> Result<N, ERR> replace(final Supplier<? extends N> supplier) {
      Objects.requireNonNull(supplier);
      return Result.success(supplier.get());
    }

    @Override
    public <N> OptionalResult<N, ERR> replaceWithOptional(final Supplier<Optional<? extends N>> supplier) {
      Objects.requireNonNull(supplier);
      return OptionalResult.success(supplier.get());
    }

    @Override
    public BooleanResult<ERR> replaceWithBoolean(final Supplier<Boolean> supplier) {
      Objects.requireNonNull(supplier);
      return BooleanResult.success(supplier.get());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> Result<N, ERR> flatReplace(final Supplier<Result<? extends N, ? extends ERR>> supplier) {
      Objects.requireNonNull(supplier);
      return (Result<N, ERR>) Objects.requireNonNull(supplier.get());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> flatReplaceToOptionalResult(
        final Supplier<OptionalResult<? extends N, ? extends ERR>> supplier) {
      Objects.requireNonNull(supplier);
      return (OptionalResult<N, ERR>) Objects.requireNonNull(supplier.get());
    }

    @SuppressWarnings("unchecked")
    @Override
    public BooleanResult<ERR> flatReplaceToBooleanResult(final Supplier<BooleanResult<? extends ERR>> supplier) {
      Objects.requireNonNull(supplier);
      return (BooleanResult<ERR>) Objects.requireNonNull(supplier.get());
    }

    @SuppressWarnings("unchecked")
    @Override
    public VoidResult<ERR> flatReplaceToVoidResult(final Supplier<VoidResult<? extends ERR>> supplier) {
      Objects.requireNonNull(supplier);
      return (VoidResult<ERR>) Objects.requireNonNull(supplier.get());
    }

    @Override
    public Result<S, ERR> recover(final Function<ERR, S> function) {
      return safeCast();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> Result<N, ERR> flatRecover(final Function<ERR, Result<? extends N, ? extends ERR>> function) {
      return (Result<N, ERR>) this;
    }

    @Override
    public Result<S, ERR> consume(final Consumer<? super S> consumer) {
      Objects.requireNonNull(consumer);
      consumer.accept(value);
      return safeCast();
    }

    @Override
    public Result<S, ERR> consumeError(final Consumer<? super ERR> errorConsumer) {
      Objects.requireNonNull(errorConsumer);
      return safeCast();
    }

    @Override
    public Result<S, ERR> consumeEither(final Consumer<? super S> valueConsumer,
        final Consumer<? super ERR> errorConsumer) {
      Objects.requireNonNull(errorConsumer);
      valueConsumer.accept(value);
      return safeCast();
    }

    @Override
    public Result<S, ERR> flatConsume(final Function<? super S, ? extends VoidResult<? extends ERR>> function) {
      final VoidResult<? extends ERR> apply = function.apply(value);
      return apply.fold(() -> this, Result::error);
    }

    @Override
    public Result<S, ERR> runIfSuccess(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public Result<S, ERR> runIfError(final Runnable runnable) {
      Objects.requireNonNull(runnable);
      return safeCast();
    }

    @Override
    public Result<S, ERR> runEither(final Runnable successRunnable, final Runnable errorRunnable) {
      successRunnable.run();
      return safeCast();
    }

    @Override
    public Result<S, ERR> runAlways(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public Result<S, ERR> flatRunIfSuccess(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      final VoidResult<? extends ERR> voidResult = supplier.get();
      return voidResult.fold(() -> this, Result::error);
    }

    @Override
    public Result<S, ERR> filter(final Predicate<? super S> predicate, final Supplier<? extends ERR> errorSupplier) {
      Objects.requireNonNull(errorSupplier);
      if (predicate.test(value)) {
        return safeCast();
      } else {
        return Result.error(errorSupplier.get());
      }
    }

    @Override
    public Result<S, ERR> filter(final Function<? super S, ? extends VoidResult<? extends ERR>> function) {
      final VoidResult<? extends ERR> apply = function.apply(value);
      return apply.fold(() -> this, Result::error);
    }

    @Override
    public <N> N fold(final Function<? super S, ? extends N> valueFunction,
        final Function<? super ERR, ? extends N> errorFunction) {
      Objects.requireNonNull(errorFunction);
      return valueFunction.apply(value);
    }

    @Override
    public S orElse(final S other) {
      Objects.requireNonNull(other);
      return value;
    }

    @Override
    public S orElse(final Supplier<? extends S> supplier) {
      Objects.requireNonNull(supplier);
      return value;
    }

    @Override
    public S orElse(final Function<? super ERR, ? extends S> function) {
      Objects.requireNonNull(function);
      return value;
    }

    @Override
    public S get() {
      return value;
    }

    @Override
    public <X extends Throwable> S getOrThrow(final Function<? super ERR, ? extends X> function) throws X {
      Objects.requireNonNull(function);
      return value;
    }

    @Override
    public S getOrThrow(final String message) {
      Objects.requireNonNull(message);
      return value;
    }

    @Override
    public boolean isSuccess() {
      return true;
    }

    @Override
    public boolean isError() {
      return false;
    }

    @Override
    public Stream<S> stream() {
      return Stream.of(value);
    }

    @Override
    public OptionalResult<S, ERR> toOptionalResult() {
      return OptionalResult.success(value);
    }

    @Override
    public VoidResult<ERR> toVoidResult() {
      return VoidResult.success();
    }

    @SuppressWarnings("unchecked")
    private <E1> Success<S, E1> safeCast() {
      return (Success<S, E1>) this;
    }

    @Override
    public boolean equals(final Object o) {
      if (o == null || getClass() != o.getClass())
        return false;
      final Success<?, ?> success = (Success<?, ?>) o;
      return Objects.equals(value, success.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(getClass(), value);
    }

    @Override
    public String toString() {
      return "Result[Value: %s]".formatted(value);
    }
  }

  record Error<S, ERR>(ERR error) implements Result<S, ERR> {
    public Error(final ERR error) {
      this.error = Objects.requireNonNull(error);
    }

    @Override
    public <N> Result<N, ERR> map(final Function<? super S, ? extends N> function) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @Override
    public <N> Result<N, ERR> map(final Supplier<? extends N> supplier) {
      Objects.requireNonNull(supplier);
      return safeCast();
    }

    @Override
    public <N> OptionalResult<N, ERR> mapToOptional(final Function<? super S, Optional<N>> function) {
      return OptionalResult.error(error);
    }

    @Override
    public BooleanResult<ERR> mapToBoolean(final Function<? super S, Boolean> function) {
      return BooleanResult.error(error);
    }

    @Override
    public <N> Result<S, N> mapError(final Function<? super ERR, ? extends N> function) {
      return new Error<>(function.apply(error));
    }

    @Override
    public <N> Result<N, ERR> flatMap(
        final Function<? super S, ? extends Result<? extends N, ? extends ERR>> function) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @Override
    public <N, U> Result<N, ERR> flatMap(
        final BiFunction<? super S, ? super U, ? extends Result<? extends N, ? extends ERR>> function,
        final U argument) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @Override
    public <N> Result<N, ERR> flatMap(
        final Supplier<? extends Result<? extends N, ? extends ERR>> supplier) {
      Objects.requireNonNull(supplier);
      return safeCast();
    }

    @Override
    public <N> OptionalResult<N, ERR> flatMapToOptionalResult(
        final Function<? super S, OptionalResult<N, ERR>> function) {
      return OptionalResult.error(error);
    }

    @Override
    public BooleanResult<ERR> flatMapToBooleanResult(final Function<? super S, BooleanResult<ERR>> function) {
      return BooleanResult.error(error);
    }

    @Override
    public VoidResult<ERR> flatMapToVoidResult(final Function<? super S, VoidResult<ERR>> function) {
      return VoidResult.error(error);
    }

    @Override
    public <N> Result<N, ERR> replace(final Supplier<? extends N> supplier) {
      Objects.requireNonNull(supplier);
      return Result.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> replaceWithOptional(final Supplier<Optional<? extends N>> supplier) {
      Objects.requireNonNull(supplier);
      return OptionalResult.error(error);
    }

    @Override
    public BooleanResult<ERR> replaceWithBoolean(final Supplier<Boolean> supplier) {
      Objects.requireNonNull(supplier);
      return BooleanResult.error(error);
    }

    @Override
    public <N> Result<N, ERR> flatReplace(final Supplier<Result<? extends N, ? extends ERR>> supplier) {
      Objects.requireNonNull(supplier);
      return Result.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> flatReplaceToOptionalResult(
        final Supplier<OptionalResult<? extends N, ? extends ERR>> supplier) {
      Objects.requireNonNull(supplier);
      return OptionalResult.error(error);
    }

    @Override
    public BooleanResult<ERR> flatReplaceToBooleanResult(final Supplier<BooleanResult<? extends ERR>> supplier) {
      Objects.requireNonNull(supplier);
      return BooleanResult.error(error);
    }

    @Override
    public VoidResult<ERR> flatReplaceToVoidResult(final Supplier<VoidResult<? extends ERR>> supplier) {
      Objects.requireNonNull(supplier);
      return VoidResult.error(error);
    }

    @Override
    public Result<S, ERR> recover(final Function<ERR, S> function) {
      return new Success<>(function.apply(error));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> Result<N, ERR> flatRecover(final Function<ERR, Result<? extends N, ? extends ERR>> function) {
      return (Result<N, ERR>) function.apply(error);
    }

    @Override
    public Result<S, ERR> consume(final Consumer<? super S> consumer) {
      Objects.requireNonNull(consumer);
      return safeCast();
    }

    @Override
    public Result<S, ERR> consumeError(final Consumer<? super ERR> errorConsumer) {
      errorConsumer.accept(error);
      return safeCast();
    }

    @Override
    public Result<S, ERR> consumeEither(final Consumer<? super S> valueConsumer,
        final Consumer<? super ERR> errorConsumer) {
      Objects.requireNonNull(valueConsumer);
      errorConsumer.accept(error);
      return safeCast();
    }

    @Override
    public Result<S, ERR> flatConsume(final Function<? super S, ? extends VoidResult<? extends ERR>> function) {
      return safeCast();
    }

    @Override
    public Result<S, ERR> runIfSuccess(final Runnable runnable) {
      Objects.requireNonNull(runnable);
      return safeCast();
    }

    @Override
    public Result<S, ERR> runIfError(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public Result<S, ERR> runEither(final Runnable successRunnable, final Runnable errorRunnable) {
      Objects.requireNonNull(successRunnable);
      errorRunnable.run();
      return safeCast();
    }

    @Override
    public Result<S, ERR> runAlways(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public Result<S, ERR> flatRunIfSuccess(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      return safeCast();
    }

    @Override
    public Result<S, ERR> filter(final Predicate<? super S> predicate, final Supplier<? extends ERR> errorSupplier) {
      Objects.requireNonNull(errorSupplier);
      return safeCast();
    }

    @Override
    public Result<S, ERR> filter(final Function<? super S, ? extends VoidResult<? extends ERR>> function) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @Override
    public <N> N fold(final Function<? super S, ? extends N> valueFunction,
        final Function<? super ERR, ? extends N> errorFunction) {
      return errorFunction.apply(error);
    }

    @Override
    public S orElse(final S other) {
      return other;
    }

    @Override
    public S orElse(final Supplier<? extends S> supplier) {
      return Objects.requireNonNull(supplier).get();
    }

    @Override
    public S orElse(final Function<? super ERR, ? extends S> function) {
      return function.apply(error);
    }

    @Override
    public S get() {
      return null;
    }

    @Override
    public <X extends Throwable> S getOrThrow(final Function<? super ERR, ? extends X> function) throws X {
      throw function.apply(error);
    }

    @Override
    public S getOrThrow(final String message) {
      throw new IllegalStateException(Objects.requireNonNull(message) + ": " + error);
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public boolean isError() {
      return true;
    }

    @Override
    public Stream<S> stream() {
      return Stream.empty();
    }

    @Override
    public OptionalResult<S, ERR> toOptionalResult() {
      return OptionalResult.error(error);
    }

    @Override
    public VoidResult<ERR> toVoidResult() {
      return VoidResult.error(error);
    }

    @SuppressWarnings("unchecked")
    private <R1> Error<R1, ERR> safeCast() {
      return (Error<R1, ERR>) this;
    }

    @Override
    public String toString() {
      return "Result[Error: %s]".formatted(error);
    }

    @Override
    public boolean equals(final Object o) {
      if (o == null || getClass() != o.getClass())
        return false;
      final Error<?, ?> error1 = (Error<?, ?>) o;
      return Objects.equals(error, error1.error);
    }

    @Override
    public int hashCode() {
      return Objects.hash(getClass(), error);
    }
  }
}
