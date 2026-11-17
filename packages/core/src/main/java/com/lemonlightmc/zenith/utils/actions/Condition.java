package com.lemonlightmc.zenith.utils.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.lemonlightmc.zenith.internal.ConditionSerializer;

/**
 * A composable condition tree.
 */
public interface Condition<T> {

    @SuppressWarnings("rawtypes")
    static ConditionSerializer serializer() {
        return ConditionSerializer.INSTANCE;
    }

    boolean test(T value);

    default Condition<T> and(final Condition<T> other) {
        return other == null || other instanceof AlwaysCondition ? this : AndCondition.of(this, other);
    }

    default Condition<T> or(final Condition<T> other) {
        if (other == null || other instanceof NeverCondition) {
            return this;
        }
        return other instanceof AlwaysCondition ? AlwaysCondition.of() : OrCondition.of(this, other);
    }

    default Condition<T> xor(final Condition<T> other) {
        if (other == null || other instanceof NeverCondition || other instanceof AlwaysCondition) {
            return NotCondition.of(this);
        }
        return XorCondition.of(this, other);
    }

    default Condition<T> negate() {
        return NotCondition.of(this);
    }

    /**
     * Mutable root/container node that requires all child conditions to pass.
     */
    final class ConditionSet<T> implements Condition<T> {
        private final List<Condition<T>> children = new ArrayList<>();

        private ConditionSet() {
        }

        private ConditionSet(final Collection<? extends Condition<T>> children) {
            addAll(children);
        }

        @SafeVarargs
        private ConditionSet(final Condition<T>... children) {
            addAll(children);
        }

        public static <T> ConditionSet<T> of(final Collection<? extends Condition<T>> children) {
            return new ConditionSet<>(children);
        }

        @SafeVarargs
        public static <T> ConditionSet<T> of(final Condition<T>... children) {
            return new ConditionSet<>(children);
        }

        public ConditionSet<T> add(final Condition<T> condition) {
            if (condition == null) {
                return this;
            }
            final Condition<T> cast = (Condition<T>) condition;
            children.add(cast);
            return this;
        }

        public ConditionSet<T> addAll(final Collection<? extends Condition<T>> conditions) {
            if (conditions == null) {
                return this;
            }
            for (final Condition<T> condition : conditions) {
                add(condition);
            }
            return this;
        }

        @SafeVarargs
        public final ConditionSet<T> addAll(final Condition<T>... conditions) {
            if (conditions == null) {
                return this;
            }
            for (final Condition<T> condition : conditions) {
                add(condition);
            }
            return this;
        }

        public boolean remove(final Condition<?> condition) {
            return children.remove(condition);
        }

        public boolean has(final Condition<?> condition) {
            return children.contains(condition);
        }

        public void clear() {
            children.clear();
        }

        public List<Condition<T>> children() {
            return Collections.unmodifiableList(children);
        }

        public boolean isEmpty() {
            for (final Condition<T> child : children) {
                if (child != null) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean test(final T value) {
            if (value == null) {
                return false;
            }
            boolean evaluated = false;
            for (final Condition<T> child : children) {
                if (child == null) {
                    continue;
                }
                evaluated = true;
                if (!child.test(value)) {
                    return false;
                }
            }
            return evaluated || children.isEmpty();
        }

        @Override
        public int hashCode() {
            return Objects.hash(children);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof final ConditionSet<?> other)) {
                return false;
            }
            return Objects.equals(children, other.children);
        }

        @Override
        public String toString() {
            return "ConditionSet" + children;
        }
    }

    /**
     * Base node for logical operator conditions in the tree.
     */
    abstract class ConditionOperator<T> implements Condition<T> {
        protected final Condition<T> left;
        protected final Condition<T> right;

        protected ConditionOperator(final Condition<T> left, final Condition<T> right) {
            if (left == null) {
                throw new IllegalArgumentException("Left condition cannot be null or empty");
            }
            if (right == null) {
                throw new IllegalArgumentException("Right condition cannot be null or empty");
            }
            this.left = left;
            this.right = right;
        }

        protected static <T> Condition<T> cast(final Condition<T> condition) {
            return (Condition<T>) condition;
        }

        public Condition<T> left() {
            return left;
        }

        public Condition<T> right() {
            return right;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getClass(), left, right);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final ConditionOperator<?> other = (ConditionOperator<?>) obj;
            return Objects.equals(left, other.left) && Objects.equals(right, other.right);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + left + ", " + right + "]";
        }
    }

    /** Logical AND operator node. */
    final class AndCondition<T> extends ConditionOperator<T> {

        private AndCondition(final Condition<T> left, final Condition<T> right) {
            super(cast(left), cast(right));
        }

        public static <T> Condition<T> of(final Condition<T> left, final Condition<T> right) {
            if (left == null && right == null) {
                return AlwaysCondition.of();
            }
            if (left == null) {
                return right;
            }
            if (right == null) {
                return left;
            }
            return new AndCondition<>(left, right);
        }

        @Override
        public boolean test(final T value) {
            if (value == null) {
                return false;
            }
            return left.test(value) && right.test(value);
        }
    }

    /** Logical OR operator node. */
    final class OrCondition<T> extends ConditionOperator<T> {

        private OrCondition(final Condition<T> left, final Condition<T> right) {
            super(cast(left), cast(right));
        }

        public static <T> Condition<T> of(final Condition<T> left, final Condition<T> right) {
            if (left == null && right == null) {
                return AlwaysCondition.of();
            }
            if (left == null) {
                return right;
            }
            if (right == null) {
                return left;
            }
            return new OrCondition<>(left, right);
        }

        @Override
        public boolean test(final T value) {
            if (value == null) {
                return false;
            }
            return left.test(value) || right.test(value);
        }
    }

    /** Logical XOR operator node. */
    final class XorCondition<T> extends ConditionOperator<T> {

        private XorCondition(final Condition<T> left, final Condition<T> right) {
            super(cast(left), cast(right));
        }

        public static <T> Condition<T> of(final Condition<T> left, final Condition<T> right) {
            if (left == null && right == null) {
                return AlwaysCondition.of();
            }
            if (left == null) {
                return right;
            }
            if (right == null) {
                return left;
            }
            return new XorCondition<>(left, right);
        }

        @Override
        public boolean test(final T value) {
            if (value == null) {
                return false;
            }
            return left.test(value) ^ right.test(value);
        }
    }

    /** Negates a child condition. */
    final class NotCondition<T> implements Condition<T> {
        private final Condition<T> delegate;

        private NotCondition(final Condition<T> delegate) {
            this.delegate = delegate == null ? AlwaysCondition.of() : delegate;
        }

        public static <T> Condition<T> of(final Condition<T> delegate) {
            if (delegate == null) {
                return NeverCondition.of();
            }
            return new NotCondition<>(delegate);
        }

        public Condition<T> delegate() {
            return delegate;
        }

        @Override
        public boolean test(final T value) {
            return value != null && !delegate.test(value);
        }

        @Override
        public Condition<T> negate() {
            return delegate;
        }

        @Override
        public int hashCode() {
            return Objects.hash(delegate);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof final NotCondition<?> other)) {
                return false;
            }
            return Objects.equals(delegate, other.delegate);
        }

        @Override
        public String toString() {
            return "NotCondition[" + delegate + "]";
        }
    }

    /** Always-true condition. */
    final class AlwaysCondition<T> implements Condition<T> {
        private static final AlwaysCondition<?> INSTANCE = new AlwaysCondition<>();

        private AlwaysCondition() {
        }

        @SuppressWarnings("unchecked")
        public static <T> Condition<T> of() {
            return (AlwaysCondition<T>) INSTANCE;
        }

        @Override
        public boolean test(final T value) {
            return true;
        }

        @Override
        public Condition<T> negate() {
            return NeverCondition.of();
        }

        @Override
        public String toString() {
            return "AlwaysCondition";
        }
    }

    /** Always-false condition. */
    final class NeverCondition<T> implements Condition<T> {
        private static final NeverCondition<?> INSTANCE = new NeverCondition<>();

        private NeverCondition() {
        }

        @SuppressWarnings("unchecked")

        public static <T> Condition<T> of() {
            return (NeverCondition<T>) INSTANCE;
        }

        @Override
        public boolean test(final T value) {
            return false;
        }

        @Override
        public Condition<T> negate() {
            return AlwaysCondition.of();
        }

        @Override
        public String toString() {
            return "NeverCondition";
        }
    }

    /** Adapts a predicate into a condition leaf. */
    final class PredicateCondition<T> implements Condition<T> {
        private final Predicate<T> predicate;

        private PredicateCondition(final Predicate<T> predicate) {
            this.predicate = predicate;
        }

        public static <T> Condition<T> of(final Predicate<T> predicate) {
            return predicate == null ? AlwaysCondition.of() : new PredicateCondition<>(predicate);
        }

        @Override
        public boolean test(final T value) {
            return predicate != null && value != null && predicate.test(value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(predicate);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof final PredicateCondition<?> other)) {
                return false;
            }
            return Objects.equals(predicate, other.predicate);
        }

        @Override
        public String toString() {
            return "PredicateCondition[" + predicate + "]";
        }
    }
}
