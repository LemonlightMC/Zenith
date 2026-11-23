package com.lemonlightmc.zenith.utils.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.bukkit.command.CommandSender;

/**
 * A composable condition tree.
 */
public interface Condition<S extends CommandSender> {

    boolean test(ConditionContext<S> context);

    default Condition<S> and(final Condition<S> other) {
        return other == null || other instanceof AlwaysCondition ? this : AndCondition.of(this, other);
    }

    default Condition<S> or(final Condition<S> other) {
        if (other == null || other instanceof NeverCondition) {
            return this;
        }
        return other instanceof AlwaysCondition ? AlwaysCondition.of() : OrCondition.of(this, other);
    }

    default Condition<S> xor(final Condition<S> other) {
        if (other == null || other instanceof NeverCondition || other instanceof AlwaysCondition) {
            return NotCondition.of(this);
        }
        return XorCondition.of(this, other);
    }

    default Condition<S> negate() {
        return NotCondition.of(this);
    }

    public static <S extends CommandSender> Condition<S> always() {
        return AlwaysCondition.of();
    }

    public static <S extends CommandSender> Condition<S> never() {
        return NeverCondition.of();
    }

    public static <S extends CommandSender> Condition<S> of(final Predicate<ConditionContext<S>> predicate) {
        return PredicateCondition.of(predicate);
    }

    public static <S extends CommandSender> Condition<S> of(final Collection<? extends Condition<S>> children) {
        return new ConditionSet<>(children);
    }

    @SafeVarargs
    public static <S extends CommandSender> Condition<S> of(final Condition<S>... children) {
        return new ConditionSet<>(children);
    }

    public static <S extends CommandSender> Condition<S> and(final Condition<S> left, final Condition<S> right) {
        return AndCondition.of(left, right);
    }

    public static <S extends CommandSender> Condition<S> or(final Condition<S> left, final Condition<S> right) {
        return OrCondition.of(left, right);
    }

    public static <S extends CommandSender> Condition<S> xor(final Condition<S> left, final Condition<S> right) {
        return XorCondition.of(left, right);
    }

    public static <S extends CommandSender> Condition<S> not(final Condition<S> delegate) {
        return NotCondition.of(delegate);
    }

    public static <S extends CommandSender> ConditionSet<S> set() {
        return new ConditionSet<S>();
    }

    public static <S extends CommandSender> ConditionSet<S> set(final Collection<? extends Condition<S>> children) {
        return new ConditionSet<S>(children);
    }

    @SafeVarargs
    public static <S extends CommandSender> ConditionSet<S> set(final Condition<S>... children) {
        return new ConditionSet<S>(children);
    }

    /**
     * Mutable root/container node that requires all child conditions to pass.
     */
    public final class ConditionSet<S extends CommandSender> implements Condition<S> {
        private final List<Condition<S>> children = new ArrayList<>();

        public ConditionSet() {
        }

        public ConditionSet(final Collection<? extends Condition<S>> children) {
            addAll(children);
        }

        @SafeVarargs
        public ConditionSet(final Condition<S>... children) {
            addAll(children);
        }

        public static <S extends CommandSender> ConditionSet<S> of(final Collection<? extends Condition<S>> children) {
            return new ConditionSet<>(children);
        }

        @SafeVarargs
        public static <S extends CommandSender> ConditionSet<S> of(final Condition<S>... children) {
            return new ConditionSet<>(children);
        }

        public ConditionSet<S> add(final Condition<S> condition) {
            if (condition != null) {
                children.add(condition);
            }
            return this;
        }

        public ConditionSet<S> addAll(final Collection<? extends Condition<S>> conditions) {
            if (conditions == null || conditions.isEmpty()) {
                return this;
            }
            for (final Condition<S> condition : conditions) {
                if (condition != null) {
                    children.add(condition);
                }
            }
            return this;
        }

        @SafeVarargs
        public final ConditionSet<S> addAll(final Condition<S>... conditions) {
            if (conditions == null || conditions.length == 0) {
                return this;
            }
            for (final Condition<S> condition : conditions) {
                if (condition != null) {
                    children.add(condition);
                }
            }
            return this;
        }

        public ConditionSet<S> remove(final Condition<S> condition) {
            if (condition != null) {
                children.remove(condition);
            }
            return this;
        }

        public ConditionSet<S> removeAll(final Collection<Condition<S>> conditions) {
            if (conditions == null || conditions.isEmpty()) {
                return this;
            }
            for (final Condition<S> condition : conditions) {
                if (condition != null) {
                    children.remove(condition);
                }
            }
            return this;
        }

        @SafeVarargs
        public final ConditionSet<S> removeAll(final Condition<S>... conditions) {
            if (conditions == null || conditions.length == 0) {
                return this;
            }
            for (final Condition<S> condition : conditions) {
                if (condition != null) {
                    children.remove(condition);
                }
            }
            return this;
        }

        public boolean has(final Condition<S> condition) {
            return children.contains(condition);
        }

        public void clear() {
            children.clear();
        }

        public List<Condition<S>> children() {
            return Collections.unmodifiableList(children);
        }

        public boolean isEmpty() {
            for (final Condition<S> child : children) {
                if (child != null) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean test(final ConditionContext<S> context) {
            if (children.isEmpty()) {
                return true;
            }
            for (final Condition<S> child : children) {
                if (child != null && !child.test(context)) {
                    return false;
                }
            }
            return true;
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
            if (!(obj instanceof final ConditionSet other)) {
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
    abstract class ConditionOperator<S extends CommandSender> implements Condition<S> {
        protected final Condition<S> left;
        protected final Condition<S> right;

        protected ConditionOperator(final Condition<S> left, final Condition<S> right) {
            if (left == null) {
                throw new IllegalArgumentException("Left condition cannot be null or empty");
            }
            if (right == null) {
                throw new IllegalArgumentException("Right condition cannot be null or empty");
            }
            this.left = left;
            this.right = right;
        }

        public Condition<S> left() {
            return left;
        }

        public Condition<S> right() {
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
    final class AndCondition<S extends CommandSender> extends ConditionOperator<S> {

        private AndCondition(final Condition<S> left, final Condition<S> right) {
            super(left, right);
        }

        public static <S extends CommandSender> Condition<S> of(final Condition<S> left, final Condition<S> right) {
            if (left == null && right == null) {
                return AlwaysCondition.of();
            }
            if (left == null) {
                return right;
            }
            if (right == null) {
                return left;
            }
            return new AndCondition<S>(left, right);
        }

        @Override
        public boolean test(final ConditionContext<S> context) {
            return left.test(context) && right.test(context);
        }
    }

    /** Logical OR operator node. */
    final class OrCondition<S extends CommandSender> extends ConditionOperator<S> {

        private OrCondition(final Condition<S> left, final Condition<S> right) {
            super(left, right);
        }

        public static <S extends CommandSender> Condition<S> of(final Condition<S> left, final Condition<S> right) {
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
        public boolean test(final ConditionContext<S> context) {
            return left.test(context) || right.test(context);
        }
    }

    /** Logical XOR operator node. */
    final class XorCondition<S extends CommandSender> extends ConditionOperator<S> {

        private XorCondition(final Condition<S> left, final Condition<S> right) {
            super(left, right);
        }

        public static <S extends CommandSender> Condition<S> of(final Condition<S> left, final Condition<S> right) {
            if (left == null && right == null) {
                return AlwaysCondition.of();
            }
            if (left == null) {
                return right;
            }
            if (right == null) {
                return left;
            }
            return new XorCondition<S>(left, right);
        }

        @Override
        public boolean test(final ConditionContext<S> context) {
            return left.test(context) ^ right.test(context);
        }
    }

    /** Negates a child condition. */
    final class NotCondition<S extends CommandSender> implements Condition<S> {
        private final Condition<S> delegate;

        private NotCondition(final Condition<S> delegate) {
            this.delegate = delegate;
        }

        public static <S extends CommandSender> Condition<S> of(final Condition<S> delegate) {
            if (delegate == null) {
                return NeverCondition.of();
            }
            return new NotCondition<S>(delegate);
        }

        public Condition<S> delegate() {
            return delegate;
        }

        @Override
        public boolean test(final ConditionContext<S> context) {
            return !delegate.test(context);
        }

        @Override
        public Condition<S> negate() {
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
            if (!(obj instanceof final NotCondition other)) {
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
    final class AlwaysCondition<S extends CommandSender> implements Condition<S> {
        private static final AlwaysCondition<?> INSTANCE = new AlwaysCondition<>();

        private AlwaysCondition() {
        }

        @SuppressWarnings("unchecked")
        public static <S extends CommandSender> Condition<S> of() {
            return (AlwaysCondition<S>) INSTANCE;
        }

        @Override
        public boolean test(final ConditionContext<S> context) {
            return true;
        }

        @Override
        public Condition<S> negate() {
            return NeverCondition.of();
        }

        @Override
        public String toString() {
            return "AlwaysCondition";
        }
    }

    /** Always-false condition. */
    final class NeverCondition<S extends CommandSender> implements Condition<S> {
        private static final NeverCondition<?> INSTANCE = new NeverCondition<>();

        private NeverCondition() {
        }

        @SuppressWarnings("unchecked")
        public static <S extends CommandSender> Condition<S> of() {
            return (NeverCondition<S>) INSTANCE;
        }

        @Override
        public boolean test(final ConditionContext<S> context) {
            return false;
        }

        @Override
        public Condition<S> negate() {
            return AlwaysCondition.of();
        }

        @Override
        public String toString() {
            return "NeverCondition";
        }
    }

    /** Adapts a predicate into a condition leaf. */
    final class PredicateCondition<S extends CommandSender> implements Condition<S> {
        private final Predicate<ConditionContext<S>> predicate;

        private PredicateCondition(final Predicate<ConditionContext<S>> predicate) {
            this.predicate = predicate;
        }

        public static <S extends CommandSender> Condition<S> of(final Predicate<ConditionContext<S>> predicate) {
            return predicate == null ? AlwaysCondition.of() : new PredicateCondition<>(predicate);
        }

        @Override
        public boolean test(final ConditionContext<S> context) {
            return predicate != null && predicate.test(context);
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
            if (!(obj instanceof final PredicateCondition other)) {
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
