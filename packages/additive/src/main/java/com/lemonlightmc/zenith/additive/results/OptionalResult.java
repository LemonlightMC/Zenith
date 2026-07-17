package com.lemonlightmc.zenith.additive.results;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A result object which either is in success state, where it may contain a
 * non-{@code null} success value or be empty, or it may be in error state
 * containing a non-{@code null} error value.
 * <p>
 * A variable whose type is {@code OptionalResult} should never itself be
 * {@code null}, it should always point to an {@code OptionalResult} instance.
 *
 * @param <T> the type of the success value
 * @param <E> the type of the error value
 */
public sealed interface OptionalResult<T, E> {

  /**
   * Returns an {@code OptionalResult} in success state containing the given
   * non-{@code null} value as success value.
   *
   * @param maybeValue an {@code Optional} which may contain an success value,
   *                   or may be empty
   * @param <T>        the type of the success value which may be present in the
   *                   given {@code Optional}
   * @param <E>        the type of the error value
   * @return an {@code OptionalResult} in success state which either contains
   *         a success value or is empty
   * @throws NullPointerException if given {@code Optional} is {@code null}
   */
  static <T, E> OptionalResult<T, E> success(final Optional<? extends T> maybeValue) {
    @SuppressWarnings("unchecked")
    final Optional<T> t = (Optional<T>) Objects.requireNonNull(maybeValue);
    return t.map(OptionalResult::<T, E>success).orElse(empty());
  }

  /**
   * Returns an {@code OptionalResult} in success state containing the given
   * non-{@code null} value as success value.
   *
   * @param value the success value, which must be non-{@code null}
   * @param <T>   the type of the success value
   * @param <E>   the type of the error value
   * @return an {@code OptionalResult} in success state containing the given
   *         success value
   * @throws NullPointerException if given success value is {@code null}
   */
  static <T, E> OptionalResult<T, E> success(final T value) {
    return new Value<>(Objects.requireNonNull(value));
  }

  /**
   * Returns an {@code OptionalResult} in success state either containing the
   * given value as success value, or empty if the given value is null.
   *
   * @param value the success value, which may be {@code null}
   * @param <T>   the type of the success value
   * @param <E>   the type of the error value
   * @return an {@code OptionalResult} in success state containing the given
   *         success value if not null, otherwise an empty {@code OptionalResult}
   */
  static <T, E> OptionalResult<T, E> successNullable(final T value) {
    if (value == null) {
      return empty();
    }
    return new Value<T, E>(value);
  }

  /**
   * Returns an {@code OptionalResult} in success state, which is empty with
   * no success value.
   *
   * @param <T> the type of the success value
   * @param <E> the type of the error value
   * @return an empty {@code OptionalResult} in success state
   */
  static <T, E> OptionalResult<T, E> empty() {
    return new Empty<>();
  }

  /**
   * Returns an {@code OptionalResult} in error state containing the given
   * non-{@code null} value as error value.
   *
   * @param value the error value, which must be non-{@code null}
   * @param <T>   the type of the success value
   * @param <E>   the type of the error value
   * @return an {@code OptionalResult} in error state containing the given
   *         error value
   * @throws NullPointerException if given error value is {@code null}
   */
  static <T, E> OptionalResult<T, E> error(final E value) {
    return new Error<>(Objects.requireNonNull(value));
  }

  /**
   * If in success state, returns a {@code Result} containing the result of
   * applying the given mapping function to the optional success value of this
   * {@code OptionalResult}, otherwise returns a {@code Result} containing the
   * error value of this {@code OptionalResult}.
   *
   * @param function the mapping function to apply to the optional success
   *                 value, if success state
   * @param <N>      the type of the value returned from the mapping function
   * @return a {@code Result} containing the result of applying the mapping
   *         function to the optional success value of this
   *         {@code OptionalResult}, if
   *         in success state, otherwise a {@code Result} containing the error
   *         value
   *         of this {@code OptionalResult}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> Result<N, E> map(Function<Optional<T>, ? extends N> function);

  /**
   * If in success state, returns a {@code OptionalResult} containing the
   * result of applying the given mapping function to the optional success
   * value, otherwise returns the unaltered {@code OptionalResult} in error
   * state.
   *
   * @param function the mapping function to apply to the optional success
   *                 value, if success state
   * @param <N>      the type of the success value which may be present in the
   *                 {@code Optional} returned from the mapping function
   * @return a {@code OptionalResult} containing the result of applying the
   *         mapping function to the optional success value of this
   *         {@code OptionalResult}, if in success state, otherwise the unaltered
   *         {@code OptionalResult} in error state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> OptionalResult<N, E> mapToOptional(
      Function<Optional<T>, ? extends Optional<? extends N>> function);

  /**
   * If in success state, returns a {@code BooleanResult} containing the
   * result of applying the given mapping function to the optional success
   * value, otherwise returns a {@code BooleanResult} containing the error
   * value of this {@code OptionalResult}.
   *
   * @param function the mapping function to apply to the optional success
   *                 value, if success state
   * @return a {@code BooleanResult} containing the result of applying the
   *         mapping function to the optional success value of this
   *         {@code OptionalResult}, if in success state, otherwise a
   *         {@code BooleanResult} containing the error value of this
   *         {@code OptionalResult}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  BooleanResult<E> mapToBoolean(Function<Optional<T>, Boolean> function);

  /** Replaces the optional success value without reading it. */
  default <N> Result<N, E> replace(final Supplier<? extends N> supplier) {
    Objects.requireNonNull(supplier);
    return map(ignored -> supplier.get());
  }

  /**
   * If in error state, returns a {@code OptionalResult} containing the result
   * of applying the given mapping function to the error value, otherwise
   * returns the unaltered {@code OptionalResult} in success state.
   *
   * @param function the mapping function to apply to the error value, if
   *                 error state
   * @param <N>      the type of the value returned from the mapping function
   * @return a {@code OptionalResult} containing the result of applying the
   *         mapping function to the error value of this {@code OptionalResult},
   *         if in
   *         error state, otherwise the unaltered {@code OptionalResult} in
   *         success
   *         state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> OptionalResult<T, N> mapError(Function<? super E, ? extends N> function);

  /**
   * If in success state with a success value, returns an
   * {@code OptionalResult} containing the result of applying the given
   * mapping function to the success value, otherwise returns the unaltered
   * {@code OptionalResult} which may be empty or in error state.
   * <p>
   * If the given mapping function returns null, then the returned
   * {@code OptionalResult} will be empty.
   *
   * @param function the mapping function to apply to the success value, if
   *                 success state with a success value
   * @param <N>      the type of the value returned from the mapping function
   * @return an {@code OptionalResult} containing the result of applying the
   *         mapping function to the success value of this {@code OptionalResult},
   *         if
   *         in success state with a success value, otherwise the unaltered
   *         {@code OptionalResult} which may be empty or in error state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null}
   */
  <N> OptionalResult<N, E> mapValue(
      Function<? super T, ? extends N> function);

  /**
   * If in success state with a success value, returns an
   * {@code OptionalResult} containing the result of applying the given
   * mapping function to the success value, otherwise returns the unaltered
   * {@code OptionalResult} which may be empty or in error state.
   * <p>
   * If the given mapping function returns a non-empty {@code Optional}, then
   * the returned {@code OptionalResult} will contain the {@code Optional}
   * content as success value. If the given mapping function returns an empty
   * {@code Optional}, then the returned {@code OptionalResult} will also be
   * empty.
   *
   * @param function the mapping function to apply to the success value, if
   *                 success state with a success value
   * @param <N>      the type of the success value which may be present in the
   *                 {@code Optional} returned from the mapping function
   * @return an {@code OptionalResult} containing the result of applying the
   *         mapping function to the success value of this {@code OptionalResult},
   *         if
   *         in success state with a success value, otherwise the unaltered
   *         {@code OptionalResult} which may be empty or in error state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> OptionalResult<N, E> mapValueToOptional(
      Function<? super T, Optional<N>> function);

  /**
   * If in success state, returns the {@code Result} from applying the given
   * mapping function to the optional success value, otherwise returns a
   * {@code Result} containing the error value of this {@code OptionalResult}.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code Result} returned by the mapping function
   * @param function the mapping function to apply to the optional success
   *                 value, if success state
   * @return the {@code Result} returned from the mapping function, if in
   *         success state, otherwise a {@code Result} containing the error value
   *         of
   *         this {@code OptionalResult}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> Result<N, E> flatMap(
      Function<Optional<T>, Result<? extends N, ? extends E>> function);

  /**
   * If in success state, returns the {@code OptionalResult} from applying
   * the given mapping function to the optional success value, otherwise
   * returns the unaltered {@code OptionalResult} in error state.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code OptionalResult} returned by the mapping function
   * @param function the mapping function to apply to the optional success
   *                 value, if success state
   * @return the {@code OptionalResult} returned from the mapping function, if
   *         in success state, otherwise the unaltered {@code OptionalResult} in
   *         error
   *         state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> OptionalResult<N, E> flatMapToOptionalResult(
      Function<Optional<T>, OptionalResult<? extends N, ? extends E>> function);

  /**
   * If in success state, returns the {@code BooleanResult} from applying
   * the given mapping function to the optional success value, otherwise
   * returns a {@code BooleanResult} containing the error value of this
   * {@code OptionalResult}.
   *
   * @param function the mapping function to apply to the optional success
   *                 value, if success state
   * @return the {@code BooleanResult} returned from the mapping function, if
   *         in success state, otherwise a {@code BooleanResult} containing the
   *         error
   *         value of this {@code OptionalResult}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  BooleanResult<E> flatMapToBooleanResult(
      Function<Optional<? extends T>, BooleanResult<? extends E>> function);

  /**
   * If in success state, returns the {@code VoidResult} from applying the
   * given mapping function to the optional success value, otherwise returns a
   * {@code VoidResult} containing the error value of this
   * {@code OptionalResult}.
   *
   * @param function the mapping function to apply to the optional success
   *                 value, if success state
   * @return the {@code VoidResult} returned from the mapping function, if in
   *         success state, otherwise a {@code VoidResult} containing the error
   *         value
   *         of this {@code OptionalResult}
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  VoidResult<E> flatMapToVoidResult(
      Function<Optional<? extends T>, VoidResult<? extends E>> function);

  /**
   * If in success state with a success value, returns an
   * {@code OptionalResult} from applying the given mapping function to the
   * success value, otherwise returns the unaltered {@code OptionalResult}
   * which may be empty or in error state.
   * <p>
   * If the {@code Result} returned from the mapping function is in success
   * state. the returned {@code OptionalResult} will contain the success value
   * from the {@code Result}. If the {@code Result} is in error state, the
   * returned {@code OptionalResult} will contain the error value from the
   * {@code Result}.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code OptionalResult} after applying the mapping function
   * @param function the mapping function to apply to the success value, if
   *                 success state with a success value
   * @return an {@code OptionalResult} after applying the mapping function, if
   *         in success state with a success value, otherwise the unaltered
   *         {@code OptionalResult} which may be empty or in error state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> OptionalResult<N, E> flatMapValueWithResult(
      Function<? super T, Result<? extends N, ? extends E>> function);

  /**
   * If in success state with a success value, returns the
   * {@code OptionalResult} from applying the given mapping function to the
   * success value, otherwise returns the unaltered {@code OptionalResult}
   * which may be empty or in error state.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code OptionalResult} returned by the mapping function
   * @param function the mapping function to apply to the success value, if
   *                 success state with a success value
   * @return the {@code OptionalResult} returned from the mapping function, if
   *         in success state with a success value, otherwise the unaltered
   *         {@code OptionalResult} which may be empty or in error state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  <N> OptionalResult<N, E> flatMapValueWithOptionalResult(
      Function<? super T, OptionalResult<? extends N, ? extends E>> function);

  /**
   * If in success state with a success value, returns an
   * {@code OptionalResult} from applying the given mapping function to the
   * success value, otherwise returns the unaltered {@code OptionalResult}
   * which may be empty or in error state.
   * <p>
   * If the {@code BooleanResult} returned from the mapping function is in
   * success state. the returned {@code OptionalResult} will contain the
   * boolean success value from the {@code BooleanResult}. If the
   * {@code BooleanResult} is in error state, the returned
   * {@code OptionalResult} will contain the error value from the
   * {@code BooleanResult}.
   *
   * @param function the mapping function to apply to the success value, if
   *                 success state with a success value
   * @return an {@code OptionalResult} after applying the mapping function, if
   *         in success state with a success value, otherwise the unaltered
   *         {@code OptionalResult} which may be empty or in error state
   * @throws NullPointerException if the given mapping function is
   *                              {@code null} or returns {@code null}
   */
  OptionalResult<Boolean, E> flatMapValueWithBooleanResult(
      Function<? super T, BooleanResult<? extends E>> function);

  /** Replaces this result with a supplied result when in success state. */
  default <N> Result<N, E> flatReplace(
      final Supplier<Result<? extends N, ? extends E>> supplier) {
    Objects.requireNonNull(supplier);
    return flatMap(ignored -> supplier.get());
  }

  /**
   * If in empty success state, returns the {@code OptionalResult} from
   * applying the given supplier, otherwise returns the unaltered
   * {@code OptionalResult} in success state with value or error state.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code OptionalResult} returned by this function.
   * @param supplier the supplier to call if in empty success state.
   * @return the {@code OptionalResult} returned from the supplier, if in empty
   *         state, otherwise the unaltered {@code OptionalResult} in success
   *         state
   *         with value or error state
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  <N> OptionalResult<N, E> flatReplaceEmpty(
      Supplier<OptionalResult<N, E>> supplier);

  /**
   * If in empty success state, returns the {@code Result} from applying the
   * given supplier, otherwise returns a {@code Result} with the existing
   * success value or error value.
   *
   * @param <N>      the type of success value which may be present in the
   *                 {@code Result} returned by this function.
   * @param supplier the supplier to call if in empty success state.
   * @return the {@code Result} returned from the supplier, if in empty state,
   *         otherwise a {@code Result} with the existing success value or error
   *         value.
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  <N> Result<N, E> flatReplaceEmptyWithResult(
      Supplier<Result<N, E>> supplier);

  /**
   * If in error state, returns a {@code OptionalResult} with the success
   * value from applying the given mapping function to the error value,
   * otherwise returns the unaltered {@code OptionalResult} in success state.
   *
   * @param function the mapping function to apply to the error value to
   *                 convert to a new success value, if error state
   * @return A {@code OptionalResult} containing the value from the mapping
   *         function, if in error state, otherwise the unaltered
   *         {@code OptionalResult} in success state
   */
  OptionalResult<T, E> recover(
      Function<E, Optional<T>> function);

  /**
   * If in error state, returns the {@code OptionalResult} from applying the
   * given mapping function to the error value, otherwise returns the
   * unaltered {@code OptionalResult} in success state.
   *
   * @param function the mapping function to apply to the error value to
   *                 convert to a new {@code OptionalResult}, if error state
   * @param <N>      the type of success value which may be present in the
   *                 {@code OptionalResult} returned by the mapping function
   * @return the {@code OptionalResult} returned from the mapping function, if
   *         in error state, otherwise the unaltered {@code OptionalResult} in
   *         success
   *         state
   */
  <N> OptionalResult<N, E> flatRecover(
      Function<E, OptionalResult<? extends N, ? extends E>> function);

  /**
   * If in success state, applies the optional success value to the given
   * consumer, otherwise does nothing.
   *
   * @param consumer the consumer which accepts the optional success value
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if the given consumer is {@code null}
   */
  OptionalResult<T, E> consume(Consumer<Optional<T>> consumer);

  /**
   * If in success state with a success value, applies the success value to
   * the given consumer, otherwise does nothing.
   *
   * @param consumer the consumer which accepts the success value
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if the given consumer is {@code null}
   */
  OptionalResult<T, E> consumeValue(Consumer<T> consumer);

  /**
   * If in error state, applies the error value to the given consumer,
   * otherwise does nothing.
   *
   * @param errorConsumer the consumer which accepts the error value
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if the given consumer is {@code null}
   */
  OptionalResult<T, E> consumeError(Consumer<? super E> errorConsumer);

  /**
   * If in success state, applies the optional success value to the given
   * success consumer. If in error state, applies the error value to the given
   * error consumer.
   *
   * @param successConsumer the consumer which accepts the optional success
   *                        value
   * @param errorConsumer   the consumer which accepts the error value
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if one of the given consumers is {@code null}
   */
  OptionalResult<T, E> consumeEither(
      Consumer<Optional<T>> successConsumer,
      Consumer<? super E> errorConsumer);

  /**
   * If in success state with a success value, applies the success value to
   * the given value consumer. If empty, run the given empty runnable. If in
   * error state, applies the error value to the given error consumer.
   *
   * @param valueConsumer the consumer which accepts the optional success
   *                      value
   * @param emptyRunnable the runnable to run if empty
   * @param errorConsumer the consumer which accepts the error value
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if one of the given consumers or runnable is
   *                              {@code null}
   */
  OptionalResult<T, E> consumeEither(
      Consumer<? super T> valueConsumer,
      Runnable emptyRunnable,
      Consumer<? super E> errorConsumer);

  /**
   * If in success state, applies the optional success value to the given
   * function. If the function returns a {@code VoidResult} in success state,
   * the original {@code OptionalResult} is returned unaltered. If the function
   * returns a {@code VoidResult} in error state, a {@code OptionalResult}
   * containing the error value is returned. If in error state, the original
   * {@code OptionalResult} is returned unaltered.
   *
   * @param function the function which accepts the optional success value
   * @return the original {@code OptionalResult} unaltered if the given
   *         function returns success or the original {@code OptionalResult} is in
   *         error state, otherwise a {@code OptionalResult} containing the error
   *         value from the function result
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  OptionalResult<T, E> flatConsume(
      Function<Optional<T>, ? extends VoidResult<? extends E>> function);

  /**
   * If in success state, applies the success value to the given function. If
   * the function returns a {@code VoidResult} in success state, the original
   * {@code OptionalResult} is returned unaltered. If the function returns a
   * {@code VoidResult} in error state, a {@code OptionalResult} containing
   * the error value is returned. If in empty or error state, the original
   * {@code OptionalResult} is returned unaltered.
   *
   * @param function the function which accepts the success value
   * @return the original {@code OptionalResult} unaltered if the given
   *         function returns success or the original {@code OptionalResult} is in
   *         empty or error state, otherwise a {@code OptionalResult} containing
   *         the
   *         error value from the function result
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  OptionalResult<T, E> flatConsumeValue(
      Function<T, ? extends VoidResult<? extends E>> function);

  /**
   * If in success state, runs the given runnable, otherwise does nothing.
   *
   * @param runnable the runnable to run if success state
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  OptionalResult<T, E> runIfSuccess(Runnable runnable);

  /**
   * If in success state with a success value, runs the given runnable,
   * otherwise does nothing.
   *
   * @param runnable the runnable to run if success state with a success value
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  OptionalResult<T, E> runIfValue(Runnable runnable);

  /**
   * If in empty success state or error state, runs the given runnable,
   * otherwise does nothing.
   *
   * @param runnable the runnable to run if empty success state or error state
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  OptionalResult<T, E> runIfNoValue(Runnable runnable);

  /**
   * If in empty success state with no success value, runs the given runnable,
   * otherwise does nothing.
   *
   * @param runnable the runnable to run if empty success state
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  OptionalResult<T, E> runIfEmpty(Runnable runnable);

  /**
   * If in error state, runs the given runnable, otherwise does nothing.
   *
   * @param runnable the runnable to run if error state
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  OptionalResult<T, E> runIfError(Runnable runnable);

  /**
   * If in success state, runs the given success runnable. If in error state,
   * runs the given error runnable.
   *
   * @param successRunnable the runnable to run if success state
   * @param errorRunnable   the runnable to run if error state
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if one of the given runnables is {@code null}
   */
  OptionalResult<T, E> runEither(Runnable successRunnable,
      Runnable errorRunnable);

  /**
   * If in success state with a success value, runs the given value runnable.
   * If empty, runs the given empty runnable. If in error state, runs the
   * given error runnable.
   *
   * @param valueRunnable the runnable to run if success state with a success
   *                      value
   * @param emptyRunnable the runnable to run if empty
   * @param errorRunnable the runnable to run if error state
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if one of the given runnables is {@code null}
   */
  OptionalResult<T, E> runEither(Runnable valueRunnable,
      Runnable emptyRunnable,
      Runnable errorRunnable);

  /**
   * Runs the given runnable, no matter the state.
   *
   * @param runnable the runnable to run
   * @return the original {@code OptionalResult} unaltered
   * @throws NullPointerException if the given runnable is {@code null}
   */
  OptionalResult<T, E> runAlways(Runnable runnable);

  /**
   * If in success state, runs the given supplier. If the supplier returns a
   * {@code VoidResult} in success state, the original {@code OptionalResult}
   * is returned unaltered. If the supplier returns a {@code VoidResult} in
   * error state, a {@code OptionalResult} containing the error value is
   * returned. If in error state, the original {@code OptionalResult} is
   * returned unaltered.
   *
   * @param supplier the supplier to run
   * @return the original {@code OptionalResult} unaltered if the given
   *         supplier returns success or the original {@code OptionalResult} is in
   *         error state, otherwise a {@code OptionalResult} containing the error
   *         value from the supplier result
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  OptionalResult<T, E> flatRunIfSuccess(
      Supplier<? extends VoidResult<? extends E>> supplier);

  /**
   * If in success state with a success value, runs the given supplier. If the
   * supplier returns a {@code VoidResult} in success state, the original
   * {@code OptionalResult} is returned unaltered. If the supplier returns a
   * {@code VoidResult} in error state, a {@code OptionalResult} containing
   * the error value is returned. If in error state or empty, the original
   * {@code OptionalResult} is returned unaltered.
   *
   * @param supplier the supplier to run
   * @return the original {@code OptionalResult} unaltered if the given
   *         supplier returns success or the original {@code OptionalResult} is in
   *         empty or error state, otherwise a {@code OptionalResult} containing
   *         the
   *         error value from the supplier result
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  OptionalResult<T, E> flatRunIfValue(
      Supplier<? extends VoidResult<? extends E>> supplier);

  /**
   * If in success state, verifies the optional success value of this
   * {@code OptionalResult} by testing it with the given predicate. If the
   * predicate evaluates to false, a new {@code OptionalResult} is returned
   * containing the error value provided by the given error supplier. If the
   * predicate evaluates to true, or the {@code OptionalResult} already was
   * in error state, the original {@code OptionalResult} is returned
   * unaltered.
   *
   * @param predicate     the predicate used to filter the optional success value,
   *                      if success state
   * @param errorSupplier supplier providing the error if predicate evaluates
   *                      to false
   * @return the original {@code OptionalResult} unaltered, unless the
   *         predicate evaluates to false, then a new {@code OptionalResult} in
   *         error
   *         state is returned containing the supplied error value
   * @throws NullPointerException if the given predicate is {@code null} or
   *                              returns {@code null}, or the given error
   *                              supplier is {@code null} or
   *                              returns {@code null}
   */
  OptionalResult<T, E> filter(Predicate<Optional<T>> predicate,
      Supplier<? extends E> errorSupplier);

  /**
   * If in success state, verifies the success value of this
   * {@code OptionalResult} by mapping it to a {@code VoidResult}. If the
   * returned {@code VoidResult} is in error state, a new
   * {@code OptionalResult} is returned containing the error value of the
   * {@code VoidResult}. If the {@code VoidResult} is in success state, or the
   * {@code OptionalResult} already was in error state, the original
   * {@code OptionalResult} is returned unaltered.
   *
   * @param function the function applied to the success value, if success
   *                 state
   * @return the original {@code OptionalResult} unaltered, unless the
   *         {@code VoidResult} returned by the mapping function is in error
   *         state,
   *         then a new {@code OptionalResult} in error state is returned
   *         containing
   *         the error value from the {@code VoidResult}
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  OptionalResult<T, E> filter(
      Function<Optional<T>, ? extends VoidResult<? extends E>> function);

  /**
   * If in success state with a success value, verifies the success value of
   * this {@code OptionalResult} by testing it with the given predicate. If
   * the predicate evaluates to false, a new {@code OptionalResult} is returned
   * containing the error value provided by the given error supplier. If the
   * predicate evaluates to true, or the {@code OptionalResult} already was
   * empty or in error state, the original {@code OptionalResult} is returned
   * unaltered.
   *
   * @param predicate     the predicate used to filter the success value, if
   *                      success state with a success value
   * @param errorSupplier supplier providing the error if predicate evaluates
   *                      to false
   * @return the original {@code OptionalResult} unaltered, unless the
   *         predicate evaluates to false, then a new {@code OptionalResult} in
   *         error
   *         state is returned containing the supplied error value
   * @throws NullPointerException if the given predicate is {@code null} or
   *                              returns {@code null}, or the given error
   *                              supplier is {@code null} or
   *                              returns {@code null}
   */
  OptionalResult<T, E> verifyValue(Predicate<? super T> predicate,
      Supplier<? extends E> errorSupplier);

  /**
   * If in non-empty success state, verifies the success value of this
   * {@code OptionalResult} by mapping it to a {@code VoidResult}. If the
   * returned {@code VoidResult} is in error state, a new
   * {@code OptionalResult} is returned containing the error value of the
   * {@code VoidResult}. If the {@code VoidResult} is in success state, or the
   * {@code OptionalResult} already was empty or in error state, the original
   * {@code OptionalResult} is returned unaltered.
   *
   * @param function the function applied to the success value, if non-empty
   *                 success state
   * @return the original {@code OptionalResult} unaltered, unless the
   *         {@code VoidResult} returned by the mapping function is in error
   *         state,
   *         then a new {@code OptionalResult} in error state is returned
   *         containing
   *         the error value from the {@code VoidResult}
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  OptionalResult<T, E> verifyValue(
      Function<? super T, ? extends VoidResult<? extends E>> function);

  /**
   * Retrieve a value from this {@code OptionalResult} by folding the states.
   * If in success state, return the value of applying the success function to
   * the optional success value. If in error state, return the value of
   * applying the error function to the error value.
   *
   * @param <N>             the type of the retrieved value
   * @param successFunction the mapping function to apply to the optional
   *                        success value, if success state, may return
   *                        {@code null}
   * @param errorFunction   the mapping function to apply to the error value, if
   *                        error state, may return {@code null}
   * @return the folded value mapped from either the success value or error
   *         value, may be {@code null}
   * @throws NullPointerException if one of the given functions is
   *                              {@code null}
   */
  <N> N fold(Function<Optional<T>, ? extends N> successFunction,
      Function<? super E, ? extends N> errorFunction);

  /**
   * Retrieve a value from this {@code OptionalResult} by folding the states.
   * If in success state with a success value, return the value of applying
   * the value function to the success value. If empty, return the value
   * provided by the empty-supplier. If in error state, return the value of
   * applying the error function to the error value.
   *
   * @param <N>           the type of the retrieved value
   * @param valueFunction the mapping function to apply to the success value,
   *                      if success state, may return {@code null}
   * @param emptySupplier the supplier to provide the value if empty, may
   *                      return {@code null}
   * @param errorFunction the mapping function to apply to the error value, if
   *                      error state, may return {@code null}
   * @return the folded value mapped from either the success value or error
   *         value, may be {@code null}
   * @throws NullPointerException if one of the given functions or the
   *                              supplier is {@code null}
   */
  <N> N fold(Function<? super T, ? extends N> valueFunction,
      Supplier<? extends N> emptySupplier,
      Function<? super E, ? extends N> errorFunction);

  /**
   * If in success state, returns the optional success value, otherwise
   * returns {@code other}.
   *
   * @param other the value to be returned, if not in success state, may not
   *              be {@code null}
   * @return the optional success value, if success state, otherwise
   *         {@code other}
   * @throws NullPointerException if the other value is {@code null}
   */
  Optional<T> orElse(Optional<T> other);

  /**
   * If in success state with a success value, returns the success value,
   * otherwise returns {@code other}.
   *
   * @param other the value to be returned, if not in success state, may be
   *              {@code null}
   * @return the success value, if success state with a success value,
   *         otherwise {@code other}
   */
  T valueOrElse(T other);

  /**
   * If in success state, returns the optional success value, otherwise
   * returns the value returned from the given function.
   *
   * @param function the mapping function to apply to the error value, if not
   *                 in success state, it may not return {@code null}
   * @return the optional success value, if success state, otherwise the
   *         result returned from the given function
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  Optional<T> orElseGet(Function<? super E, ? extends Optional<T>> function);

  /**
   * If in success state with a success value, returns the success value,
   * otherwise returns the value returned from the given function.
   *
   * @param supplier the supplier providing the return value, if not in
   *                 success state with a success value, it may return
   *                 {@code null}
   * @return the success value, if success state with a success value,
   *         otherwise the result returned from the given function
   * @throws NullPointerException if the given function is {@code null}
   */
  T valueOrElseGet(Supplier<? extends T> supplier);

  /**
   * If in success state, returns the optional success value, otherwise throws
   * the exception returned by the given function.
   *
   * @param <X>      type of the exception to be thrown
   * @param function the mapping function producing an exception by applying
   *                 the error value, if not in success state
   * @return the optional success value, if success state
   * @throws X                    if in error state
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  <X extends Throwable> Optional<T> orElseThrow(
      Function<? super E, ? extends X> function) throws X;

  /**
   * If in success state with a success value, returns the success value,
   * otherwise throws the exception returned by the given function.
   *
   * @param <X>      type of the exception to be thrown
   * @param supplier the supplier providing the return value, if not in
   *                 success state with a success value
   * @return the success value, if success state with success value
   * @throws X                    if empty or in error state
   * @throws NullPointerException if the given function is {@code null} or
   *                              returns {@code null}
   */
  <X extends Throwable> T valueOrElseThrow(
      Supplier<? extends X> supplier) throws X;

  /**
   * Transforms this {@code OptionalResult} to a {@code Result}. If in
   * non-empty succes state, the {@code Result} will be in success state
   * containing the success value of this {@code OptionalResult}. If empty
   * success state, the {@code Result} will be in error state, containing the
   * error value supplied by the given error supplier. If in error state, the
   * {@code Result} will be in error state containing the error value of this
   * {@code OptionalResult}.
   *
   * @param errorSupplier supplier providing the error value if empty success
   *                      state
   * @return a {@code Result} either in success state containing the success
   *         value from this {@code OptionalResult}, or in error state containing
   *         either the error value from the given error supplier or the error
   *         value
   *         present in this {@code OptionalResult}
   * @throws NullPointerException if the given supplier is {@code null} or
   *                              returns {@code null}
   */
  Result<T, E> toResult(Supplier<? extends E> errorSupplier);

  /**
   * Transforms this {@code OptionalResult} to a {@code VoidResult}. If in
   * success state, the {@code VoidResult} will be in success state. If in
   * error state, the {@code VoidResult} will be in error state containing the
   * error value from this {@code OptionalResult}.
   *
   * @return a {@code VoidResult} either in success state or in error state
   *         containing the error value from this {@code OptionalResult}
   */
  VoidResult<E> toVoidResult();

  /**
   * Handle the given {@code Callable}. If the {@code Callable} executes
   * successfully, the {@code OptionalResult} will be in success state
   * containing the returned value. If the {@code Callable} throws an
   * exception, the {@code OptionalResult} will be in error state containing
   * the thrown exception.
   *
   * @param callable the {@code Callable} to handle
   * @param <T>      type of the return value of the {@code Callable}
   * @return a {@code OptionalResult} either in success state containing the
   *         value from the {@code Callable}, or in error state containing the
   *         exception thrown by the {@code Callable}
   * @throws NullPointerException if the given callable is {@code null} or
   *                              returns {@code null}
   */
  static <T> OptionalResult<T, Exception> handle(final Callable<Optional<T>> callable) {
    Objects.requireNonNull(callable);
    final Optional<T> value;
    try {
      value = callable.call();
    } catch (final Exception e) {
      return OptionalResult.error(e);
    }
    return OptionalResult.success(value);
  }

  /**
   * Handle the given {@code Callable}. If the {@code Callable} executes
   * successfully, the {@code OptionalResult} will be in success state
   * containing the returned value. If the {@code Callable} throws an
   * exception, the {@code OptionalResult} will be in error state containing
   * the result after mapping the exception with the given exception mapper
   * function.
   *
   * @param callable the {@code Callable} to handle
   * @param <T>      type of the return value of the {@code Callable}
   * @param <E>      type of the error value after mapping a thrown exception
   * @return a {@code OptionalResult} either in success state containing the
   *         value from the {@code Callable}, or in error state containing the
   *         result
   *         after mapping the exception thrown by the {@code Callable}
   * @throws NullPointerException if the given callable is {@code null} or
   *                              returns {@code null}, or if the given exception
   *                              mapper function is
   *                              {@code null} or returns {@code null}
   */
  static <T, E> OptionalResult<T, E> handle(final Callable<Optional<T>> callable,
      final Function<Exception, E> exceptionMapper) {
    Objects.requireNonNull(exceptionMapper);
    return handle(callable).mapError(exceptionMapper);
  }

  record Value<S, ERR>(S value) implements OptionalResult<S, ERR> {

    public Value(final S value) {
      this.value = Objects.requireNonNull(value);
    }

    @Override
    public <N> Result<N, ERR> map(final Function<Optional<S>, ? extends N> function) {
      return Result.success(function.apply(Optional.of(value)));
    }

    @Override
    public <N> OptionalResult<N, ERR> mapToOptional(
        final Function<Optional<S>, ? extends Optional<? extends N>> function) {
      return OptionalResult.success(function.apply(Optional.of(value)));
    }

    @Override
    public BooleanResult<ERR> mapToBoolean(final Function<Optional<S>, Boolean> function) {
      return BooleanResult.success(function.apply(Optional.of(value)));
    }

    @Override
    public <N> OptionalResult<S, N> mapError(final Function<? super ERR, ? extends N> function) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @Override
    public <N> OptionalResult<N, ERR> mapValue(final Function<? super S, ? extends N> function) {
      return OptionalResult.success(
          Optional.ofNullable(function.apply(value)));
    }

    @Override
    public <N> OptionalResult<N, ERR> mapValueToOptional(final Function<? super S, Optional<N>> function) {
      return OptionalResult.success(function.apply(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> Result<N, ERR> flatMap(final Function<Optional<S>, Result<? extends N, ? extends ERR>> function) {
      return (Result<N, ERR>) function.apply(Optional.of(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> flatMapToOptionalResult(
        final Function<Optional<S>, OptionalResult<? extends N, ? extends ERR>> function) {
      return (OptionalResult<N, ERR>) function.apply(Optional.of(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> flatReplaceEmpty(final Supplier<OptionalResult<N, ERR>> supplier) {
      return (OptionalResult<N, ERR>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> Result<N, ERR> flatReplaceEmptyWithResult(final Supplier<Result<N, ERR>> supplier) {
      return (Result<N, ERR>) Result.success(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BooleanResult<ERR> flatMapToBooleanResult(
        final Function<Optional<? extends S>, BooleanResult<? extends ERR>> function) {
      Objects.requireNonNull(function);
      return (BooleanResult<ERR>) function.apply(Optional.of(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public VoidResult<ERR> flatMapToVoidResult(
        final Function<Optional<? extends S>, VoidResult<? extends ERR>> function) {
      Objects.requireNonNull(function);
      return (VoidResult<ERR>) function.apply(Optional.of(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> flatMapValueWithResult(
        final Function<? super S, Result<? extends N, ? extends ERR>> function) {
      Objects.requireNonNull(function);
      return (OptionalResult<N, ERR>) function.apply(value).toOptionalResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> flatMapValueWithOptionalResult(
        final Function<? super S, OptionalResult<? extends N, ? extends ERR>> function) {
      Objects.requireNonNull(function);
      final OptionalResult<N, ERR> result = (OptionalResult<N, ERR>) function.apply(value);
      return Objects.requireNonNull(result);
    }

    @SuppressWarnings("unchecked")
    @Override
    public OptionalResult<Boolean, ERR> flatMapValueWithBooleanResult(
        final Function<? super S, BooleanResult<? extends ERR>> function) {
      Objects.requireNonNull(function);
      return (OptionalResult<Boolean, ERR>) function.apply(value).toOptionalResult();
    }

    @Override
    public OptionalResult<S, ERR> recover(final Function<ERR, Optional<S>> function) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> flatRecover(
        final Function<ERR, OptionalResult<? extends N, ? extends ERR>> function) {
      Objects.requireNonNull(function);
      return (OptionalResult<N, ERR>) this;
    }

    @Override
    public OptionalResult<S, ERR> consume(final Consumer<Optional<S>> consumer) {
      consumer.accept(Optional.of(value));
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeValue(final Consumer<S> consumer) {
      consumer.accept(value);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeError(final Consumer<? super ERR> errorConsumer) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeEither(final Consumer<Optional<S>> successConsumer,
        final Consumer<? super ERR> errorConsumer) {
      successConsumer.accept(Optional.of(value));
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeEither(final Consumer<? super S> valueConsumer, final Runnable emptyRunnable,
        final Consumer<? super ERR> errorConsumer) {
      Objects.requireNonNull(valueConsumer);
      Objects.requireNonNull(emptyRunnable);
      Objects.requireNonNull(errorConsumer);
      valueConsumer.accept(value);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> flatConsume(
        final Function<Optional<S>, ? extends VoidResult<? extends ERR>> function) {
      final VoidResult<? extends ERR> apply = function.apply(Optional.of(value));
      return apply.fold(() -> this, OptionalResult::error);
    }

    @Override
    public OptionalResult<S, ERR> flatConsumeValue(final Function<S, ? extends VoidResult<? extends ERR>> function) {
      final VoidResult<? extends ERR> result = function.apply(value);
      return result.fold(() -> this, OptionalResult::error);
    }

    @Override
    public OptionalResult<S, ERR> runIfSuccess(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfValue(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfNoValue(final Runnable runnable) {
      Objects.requireNonNull(runnable);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfEmpty(final Runnable runnable) {
      Objects.requireNonNull(runnable);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfError(final Runnable runnable) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runEither(final Runnable successRunnable, final Runnable errorRunnable) {
      successRunnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runEither(final Runnable valueRunnable, final Runnable emptyRunnable,
        final Runnable errorRunnable) {
      valueRunnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runAlways(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> flatRunIfSuccess(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      final VoidResult<? extends ERR> voidResult = supplier.get();
      return voidResult.fold(() -> this, OptionalResult::error);
    }

    @Override
    public OptionalResult<S, ERR> flatRunIfValue(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      final VoidResult<? extends ERR> voidResult = supplier.get();
      return voidResult.fold(() -> this, OptionalResult::error);
    }

    @Override
    public OptionalResult<S, ERR> filter(final Predicate<Optional<S>> predicate,
        final Supplier<? extends ERR> errorSupplier) {
      Objects.requireNonNull(errorSupplier);
      if (predicate.test(Optional.of(value))) {
        return safeCast();
      } else {
        return OptionalResult.error(errorSupplier.get());
      }
    }

    @Override
    public OptionalResult<S, ERR> filter(final Function<Optional<S>, ? extends VoidResult<? extends ERR>> function) {
      final VoidResult<? extends ERR> apply = function.apply(Optional.of(value));
      final var instance = this;
      return apply.fold(() -> instance, OptionalResult::error);
    }

    @Override
    public OptionalResult<S, ERR> verifyValue(final Predicate<? super S> predicate,
        final Supplier<? extends ERR> errorSupplier) {
      Objects.requireNonNull(errorSupplier);
      if (predicate.test(value)) {
        return safeCast();
      } else {
        return OptionalResult.error(errorSupplier.get());
      }
    }

    @Override
    public OptionalResult<S, ERR> verifyValue(final Function<? super S, ? extends VoidResult<? extends ERR>> function) {
      final VoidResult<? extends ERR> apply = function.apply(value);
      return apply.fold(() -> this, OptionalResult::error);
    }

    @Override
    public <N> N fold(final Function<Optional<S>, ? extends N> successFunction,
        final Function<? super ERR, ? extends N> errorFunction) {
      return successFunction.apply(Optional.of(value));
    }

    @Override
    public <N> N fold(final Function<? super S, ? extends N> valueFunction, final Supplier<? extends N> emptySupplier,
        final Function<? super ERR, ? extends N> errorFunction) {
      Objects.requireNonNull(emptySupplier);
      Objects.requireNonNull(errorFunction);
      return valueFunction.apply(value);
    }

    @Override
    public Optional<S> orElse(final Optional<S> other) {
      return Optional.of(value);
    }

    @Override
    public S valueOrElse(final S other) {
      return value;
    }

    @Override
    public Optional<S> orElseGet(final Function<? super ERR, ? extends Optional<S>> function) {
      return Optional.of(value);
    }

    @Override
    public S valueOrElseGet(final Supplier<? extends S> supplier) {
      Objects.requireNonNull(supplier);
      return value;
    }

    @Override
    public <X extends Throwable> Optional<S> orElseThrow(final Function<? super ERR, ? extends X> function) throws X {
      return Optional.of(value);
    }

    @Override
    public <X extends Throwable> S valueOrElseThrow(final Supplier<? extends X> supplier) throws X {
      return value;
    }

    @Override
    public Result<S, ERR> toResult(final Supplier<? extends ERR> errorSupplier) {
      return Result.success(value);
    }

    @Override
    public VoidResult<ERR> toVoidResult() {
      return VoidResult.success();
    }

    @SuppressWarnings("unchecked")
    private <E1> OptionalResult.Value<S, E1> safeCast() {
      return (OptionalResult.Value<S, E1>) this;
    }
  }

  record Empty<S, ERR>() implements OptionalResult<S, ERR> {

    @Override
    public <N> Result<N, ERR> map(final Function<Optional<S>, ? extends N> function) {
      return Result.success(function.apply(Optional.empty()));
    }

    @Override
    public <N> OptionalResult<N, ERR> mapToOptional(
        final Function<Optional<S>, ? extends Optional<? extends N>> function) {
      return OptionalResult.empty();
    }

    @Override
    public BooleanResult<ERR> mapToBoolean(final Function<Optional<S>, Boolean> function) {
      return BooleanResult.success(function.apply(Optional.empty()));
    }

    @Override
    public <N> OptionalResult<S, N> mapError(final Function<? super ERR, ? extends N> function) {
      return safeCast();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> mapValue(final Function<? super S, ? extends N> function) {
      return (OptionalResult<N, ERR>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> mapValueToOptional(final Function<? super S, Optional<N>> function) {
      return (OptionalResult<N, ERR>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> Result<N, ERR> flatMap(final Function<Optional<S>, Result<? extends N, ? extends ERR>> function) {
      return (Result<N, ERR>) function.apply(Optional.empty());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> flatMapToOptionalResult(
        final Function<Optional<S>, OptionalResult<? extends N, ? extends ERR>> function) {
      return (OptionalResult<N, ERR>) function.apply(Optional.empty());
    }

    @Override
    public <N> OptionalResult<N, ERR> flatReplaceEmpty(final Supplier<OptionalResult<N, ERR>> supplier) {
      Objects.requireNonNull(supplier);
      final OptionalResult<N, ERR> result = supplier.get();
      Objects.requireNonNull(result);
      return result;
    }

    @Override
    public <N> Result<N, ERR> flatReplaceEmptyWithResult(final Supplier<Result<N, ERR>> supplier) {
      Objects.requireNonNull(supplier);
      return Objects.requireNonNull(supplier.get());
    }

    @SuppressWarnings("unchecked")
    @Override
    public BooleanResult<ERR> flatMapToBooleanResult(
        final Function<Optional<? extends S>, BooleanResult<? extends ERR>> function) {
      Objects.requireNonNull(function);
      return (BooleanResult<ERR>) function.apply(Optional.empty());
    }

    @SuppressWarnings("unchecked")
    @Override
    public VoidResult<ERR> flatMapToVoidResult(
        final Function<Optional<? extends S>, VoidResult<? extends ERR>> function) {
      Objects.requireNonNull(function);
      return (VoidResult<ERR>) function.apply(Optional.empty());
    }

    @Override
    public <N> OptionalResult<N, ERR> flatMapValueWithResult(
        final Function<? super S, Result<? extends N, ? extends ERR>> function) {
      Objects.requireNonNull(function);
      return OptionalResult.empty();
    }

    @Override
    public <N> OptionalResult<N, ERR> flatMapValueWithOptionalResult(
        final Function<? super S, OptionalResult<? extends N, ? extends ERR>> function) {
      Objects.requireNonNull(function);
      return OptionalResult.empty();
    }

    @Override
    public OptionalResult<Boolean, ERR> flatMapValueWithBooleanResult(
        final Function<? super S, BooleanResult<? extends ERR>> function) {
      Objects.requireNonNull(function);
      return OptionalResult.empty();
    }

    @Override
    public OptionalResult<S, ERR> recover(final Function<ERR, Optional<S>> function) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> flatRecover(
        final Function<ERR, OptionalResult<? extends N, ? extends ERR>> function) {
      Objects.requireNonNull(function);
      return (OptionalResult<N, ERR>) this;
    }

    @Override
    public OptionalResult<S, ERR> consume(final Consumer<Optional<S>> consumer) {
      consumer.accept(Optional.empty());
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeValue(final Consumer<S> consumer) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeError(final Consumer<? super ERR> errorConsumer) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeEither(final Consumer<Optional<S>> successConsumer,
        final Consumer<? super ERR> errorConsumer) {
      successConsumer.accept(Optional.empty());
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeEither(final Consumer<? super S> valueConsumer, final Runnable emptyRunnable,
        final Consumer<? super ERR> errorConsumer) {
      Objects.requireNonNull(valueConsumer);
      Objects.requireNonNull(emptyRunnable);
      Objects.requireNonNull(errorConsumer);
      emptyRunnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> flatConsume(
        final Function<Optional<S>, ? extends VoidResult<? extends ERR>> function) {
      final VoidResult<? extends ERR> apply = function.apply(Optional.empty());
      return apply.fold(() -> this, OptionalResult::error);
    }

    @Override
    public OptionalResult<S, ERR> flatConsumeValue(final Function<S, ? extends VoidResult<? extends ERR>> function) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfSuccess(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfValue(final Runnable runnable) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfNoValue(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfEmpty(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfError(final Runnable runnable) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runEither(final Runnable successRunnable, final Runnable errorRunnable) {
      successRunnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runEither(final Runnable valueRunnable, final Runnable emptyRunnable,
        final Runnable errorRunnable) {
      emptyRunnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runAlways(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> flatRunIfSuccess(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      final VoidResult<? extends ERR> voidResult = supplier.get();
      return voidResult.fold(() -> this, OptionalResult::error);
    }

    @Override
    public OptionalResult<S, ERR> flatRunIfValue(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> filter(final Predicate<Optional<S>> predicate,
        final Supplier<? extends ERR> errorSupplier) {
      if (predicate.test(Optional.empty())) {
        return safeCast();
      } else {
        return OptionalResult.error(errorSupplier.get());
      }
    }

    @Override
    public OptionalResult<S, ERR> filter(final Function<Optional<S>, ? extends VoidResult<? extends ERR>> function) {
      final VoidResult<? extends ERR> apply = function.apply(Optional.empty());
      final var instance = this;
      return apply.fold(() -> instance, OptionalResult::error);
    }

    @Override
    public OptionalResult<S, ERR> verifyValue(final Predicate<? super S> predicate,
        final Supplier<? extends ERR> errorSupplier) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> verifyValue(final Function<? super S, ? extends VoidResult<? extends ERR>> function) {
      return safeCast();
    }

    @Override
    public <N> N fold(final Function<Optional<S>, ? extends N> successFunction,
        final Function<? super ERR, ? extends N> errorFunction) {
      return successFunction.apply(Optional.empty());
    }

    @Override
    public <N> N fold(final Function<? super S, ? extends N> valueFunction, final Supplier<? extends N> emptySupplier,
        final Function<? super ERR, ? extends N> errorFunction) {
      return emptySupplier.get();
    }

    @Override
    public Optional<S> orElse(final Optional<S> other) {
      return Optional.empty();
    }

    @Override
    public S valueOrElse(final S other) {
      return other;
    }

    @Override
    public Optional<S> orElseGet(final Function<? super ERR, ? extends Optional<S>> function) {
      return Optional.empty();
    }

    @Override
    public S valueOrElseGet(final Supplier<? extends S> supplier) {
      Objects.requireNonNull(supplier);
      return supplier.get();
    }

    @Override
    public <X extends Throwable> Optional<S> orElseThrow(final Function<? super ERR, ? extends X> function) throws X {
      return Optional.empty();
    }

    @Override
    public <X extends Throwable> S valueOrElseThrow(final Supplier<? extends X> supplier) throws X {
      throw supplier.get();
    }

    @Override
    public Result<S, ERR> toResult(final Supplier<? extends ERR> errorSupplier) {
      return Result.error(errorSupplier.get());
    }

    @Override
    public VoidResult<ERR> toVoidResult() {
      return VoidResult.success();
    }

    @SuppressWarnings("unchecked")
    private <E1> OptionalResult.Empty<S, E1> safeCast() {
      return (OptionalResult.Empty<S, E1>) this;
    }
  }

  record Error<S, ERR>(ERR error) implements OptionalResult<S, ERR> {

    @Override
    public <N> Result<N, ERR> map(final Function<Optional<S>, ? extends N> function) {
      return Result.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> mapToOptional(
        final Function<Optional<S>, ? extends Optional<? extends N>> function) {
      return OptionalResult.error(error);
    }

    @Override
    public BooleanResult<ERR> mapToBoolean(final Function<Optional<S>, Boolean> function) {
      return BooleanResult.error(error);
    }

    @Override
    public <N> OptionalResult<S, N> mapError(final Function<? super ERR, ? extends N> function) {
      Objects.requireNonNull(function);
      return OptionalResult.error(
          Objects.requireNonNull(function.apply(error)));
    }

    @Override
    public <N> OptionalResult<N, ERR> mapValue(final Function<? super S, ? extends N> function) {
      return OptionalResult.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> mapValueToOptional(final Function<? super S, Optional<N>> function) {
      return OptionalResult.error(error);
    }

    @Override
    public <N> Result<N, ERR> flatMap(final Function<Optional<S>, Result<? extends N, ? extends ERR>> function) {
      return Result.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> flatMapToOptionalResult(
        final Function<Optional<S>, OptionalResult<? extends N, ? extends ERR>> function) {
      return OptionalResult.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> flatReplaceEmpty(final Supplier<OptionalResult<N, ERR>> supplier) {
      return OptionalResult.error(error);
    }

    @Override
    public <N> Result<N, ERR> flatReplaceEmptyWithResult(final Supplier<Result<N, ERR>> supplier) {
      return Result.error(error);
    }

    @Override
    public BooleanResult<ERR> flatMapToBooleanResult(
        final Function<Optional<? extends S>, BooleanResult<? extends ERR>> function) {
      return BooleanResult.error(error);
    }

    @Override
    public VoidResult<ERR> flatMapToVoidResult(
        final Function<Optional<? extends S>, VoidResult<? extends ERR>> function) {
      return VoidResult.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> flatMapValueWithResult(
        final Function<? super S, Result<? extends N, ? extends ERR>> function) {
      Objects.requireNonNull(function);

      return OptionalResult.error(error);
    }

    @Override
    public <N> OptionalResult<N, ERR> flatMapValueWithOptionalResult(
        final Function<? super S, OptionalResult<? extends N, ? extends ERR>> function) {
      Objects.requireNonNull(function);
      return OptionalResult.error(error);
    }

    @Override
    public OptionalResult<Boolean, ERR> flatMapValueWithBooleanResult(
        final Function<? super S, BooleanResult<? extends ERR>> function) {
      Objects.requireNonNull(function);

      return OptionalResult.error(error);
    }

    @Override
    public OptionalResult<S, ERR> recover(final Function<ERR, Optional<S>> function) {
      Objects.requireNonNull(function);

      return OptionalResult.success(function.apply(error));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N> OptionalResult<N, ERR> flatRecover(
        final Function<ERR, OptionalResult<? extends N, ? extends ERR>> function) {
      Objects.requireNonNull(function);

      return (OptionalResult<N, ERR>) function.apply(error);
    }

    @Override
    public OptionalResult<S, ERR> consume(final Consumer<Optional<S>> consumer) {
      Objects.requireNonNull(consumer);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeValue(final Consumer<S> consumer) {
      Objects.requireNonNull(consumer);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeError(final Consumer<? super ERR> errorConsumer) {
      errorConsumer.accept(error);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeEither(final Consumer<Optional<S>> successConsumer,
        final Consumer<? super ERR> errorConsumer) {
      errorConsumer.accept(error);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> consumeEither(final Consumer<? super S> valueConsumer, final Runnable emptyRunnable,
        final Consumer<? super ERR> errorConsumer) {
      Objects.requireNonNull(valueConsumer);
      Objects.requireNonNull(emptyRunnable);
      Objects.requireNonNull(errorConsumer);
      errorConsumer.accept(error);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> flatConsume(
        final Function<Optional<S>, ? extends VoidResult<? extends ERR>> function) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> flatConsumeValue(final Function<S, ? extends VoidResult<? extends ERR>> function) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfSuccess(final Runnable runnable) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfValue(final Runnable runnable) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfNoValue(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfEmpty(final Runnable runnable) {
      Objects.requireNonNull(runnable);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runIfError(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runEither(final Runnable successRunnable, final Runnable errorRunnable) {
      errorRunnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runEither(final Runnable valueRunnable, final Runnable emptyRunnable,
        final Runnable errorRunnable) {
      Objects.requireNonNull(valueRunnable);
      Objects.requireNonNull(emptyRunnable);
      errorRunnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> runAlways(final Runnable runnable) {
      runnable.run();
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> flatRunIfSuccess(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> flatRunIfValue(final Supplier<? extends VoidResult<? extends ERR>> supplier) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> filter(final Predicate<Optional<S>> predicate,
        final Supplier<? extends ERR> errorSupplier) {
      Objects.requireNonNull(errorSupplier);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> filter(final Function<Optional<S>, ? extends VoidResult<? extends ERR>> function) {
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> verifyValue(final Predicate<? super S> predicate,
        final Supplier<? extends ERR> errorSupplier) {
      Objects.requireNonNull(predicate);
      Objects.requireNonNull(errorSupplier);
      return safeCast();
    }

    @Override
    public OptionalResult<S, ERR> verifyValue(final Function<? super S, ? extends VoidResult<? extends ERR>> function) {
      Objects.requireNonNull(function);
      return safeCast();
    }

    @Override
    public <N> N fold(final Function<Optional<S>, ? extends N> successFunction,
        final Function<? super ERR, ? extends N> errorFunction) {
      Objects.requireNonNull(successFunction);
      return errorFunction.apply(error);
    }

    @Override
    public <N> N fold(final Function<? super S, ? extends N> valueFunction, final Supplier<? extends N> emptySupplier,
        final Function<? super ERR, ? extends N> errorFunction) {
      Objects.requireNonNull(valueFunction);
      Objects.requireNonNull(emptySupplier);
      return errorFunction.apply(error);
    }

    @Override
    public Optional<S> orElse(final Optional<S> other) {
      return other;
    }

    @Override
    public S valueOrElse(final S other) {
      return other;
    }

    @Override
    public Optional<S> orElseGet(final Function<? super ERR, ? extends Optional<S>> function) {
      return function.apply(error);
    }

    @Override
    public S valueOrElseGet(final Supplier<? extends S> supplier) {
      Objects.requireNonNull(supplier);
      return supplier.get();
    }

    @Override
    public <X extends Throwable> Optional<S> orElseThrow(final Function<? super ERR, ? extends X> function) throws X {
      throw function.apply(error);
    }

    @Override
    public <X extends Throwable> S valueOrElseThrow(final Supplier<? extends X> supplier) throws X {
      throw supplier.get();
    }

    @Override
    public Result<S, ERR> toResult(final Supplier<? extends ERR> errorSupplier) {
      return Result.error(error);
    }

    @Override
    public VoidResult<ERR> toVoidResult() {
      return VoidResult.error(error);
    }

    @SuppressWarnings("unchecked")
    private <E1> OptionalResult.Error<S, E1> safeCast() {
      return (OptionalResult.Error<S, E1>) this;
    }
  }
}
