package com.lemonlightmc.zenith.utils.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.lemonlightmc.zenith.utils.actions.Action.ActionContext;

/**
 * A composable condition tree.
 */
public interface Condition {

    boolean test(ActionContext context);

    default Condition and(final Condition other) {
        return other == null || other instanceof AlwaysCondition ? this : AndCondition.of(this, other);
    }

    default Condition or(final Condition other) {
        if (other == null || other instanceof NeverCondition) {
            return this;
        }
        return other instanceof AlwaysCondition ? AlwaysCondition.of() : OrCondition.of(this, other);
    }

    default Condition xor(final Condition other) {
        if (other == null || other instanceof NeverCondition || other instanceof AlwaysCondition) {
            return NotCondition.of(this);
        }
        return XorCondition.of(this, other);
    }

    default Condition negate() {
        return NotCondition.of(this);
    }

    /**
     * Mutable root/container node that requires all child conditions to pass.
     */
    final class ConditionSet implements Condition {
        private final List<Condition> children = new ArrayList<>();

        private ConditionSet() {
        }

        private ConditionSet(final Collection<? extends Condition> children) {
            addAll(children);
        }

        @SafeVarargs
        private ConditionSet(final Condition... children) {
            addAll(children);
        }

        public static ConditionSet of(final Collection<? extends Condition> children) {
            return new ConditionSet(children);
        }

        @SafeVarargs
        public static ConditionSet of(final Condition... children) {
            return new ConditionSet(children);
        }

        public ConditionSet add(final Condition condition) {
            if (condition == null) {
                return this;
            }
            final Condition cast = (Condition) condition;
            children.add(cast);
            return this;
        }

        public ConditionSet addAll(final Collection<? extends Condition> conditions) {
            if (conditions == null) {
                return this;
            }
            for (final Condition condition : conditions) {
                add(condition);
            }
            return this;
        }

        @SafeVarargs
        public final ConditionSet addAll(final Condition... conditions) {
            if (conditions == null) {
                return this;
            }
            for (final Condition condition : conditions) {
                add(condition);
            }
            return this;
        }

        public boolean remove(final Condition condition) {
            return children.remove(condition);
        }

        public boolean has(final Condition condition) {
            return children.contains(condition);
        }

        public void clear() {
            children.clear();
        }

        public List<Condition> children() {
            return Collections.unmodifiableList(children);
        }

        public boolean isEmpty() {
            for (final Condition child : children) {
                if (child != null) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean test(final ActionContext context) {
            boolean evaluated = false;
            for (final Condition child : children) {
                if (child == null) {
                    continue;
                }
                evaluated = true;
                if (!child.test(context)) {
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
    abstract class ConditionOperator implements Condition {
        protected final Condition left;
        protected final Condition right;

        protected ConditionOperator(final Condition left, final Condition right) {
            if (left == null) {
                throw new IllegalArgumentException("Left condition cannot be null or empty");
            }
            if (right == null) {
                throw new IllegalArgumentException("Right condition cannot be null or empty");
            }
            this.left = left;
            this.right = right;
        }

        protected static Condition cast(final Condition condition) {
            return (Condition) condition;
        }

        public Condition left() {
            return left;
        }

        public Condition right() {
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
            final ConditionOperator other = (ConditionOperator) obj;
            return Objects.equals(left, other.left) && Objects.equals(right, other.right);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + left + ", " + right + "]";
        }
    }

    /** Logical AND operator node. */
    final class AndCondition extends ConditionOperator {

        private AndCondition(final Condition left, final Condition right) {
            super(cast(left), cast(right));
        }

        public static Condition of(final Condition left, final Condition right) {
            if (left == null && right == null) {
                return AlwaysCondition.of();
            }
            if (left == null) {
                return right;
            }
            if (right == null) {
                return left;
            }
            return new AndCondition(left, right);
        }

        @Override
        public boolean test(final ActionContext context) {
            return left.test(context) && right.test(context);
        }
    }

    /** Logical OR operator node. */
    final class OrCondition extends ConditionOperator {

        private OrCondition(final Condition left, final Condition right) {
            super(cast(left), cast(right));
        }

        public static Condition of(final Condition left, final Condition right) {
            if (left == null && right == null) {
                return AlwaysCondition.of();
            }
            if (left == null) {
                return right;
            }
            if (right == null) {
                return left;
            }
            return new OrCondition(left, right);
        }

        @Override
        public boolean test(final ActionContext context) {
            return left.test(context) || right.test(context);
        }
    }

    /** Logical XOR operator node. */
    final class XorCondition extends ConditionOperator {

        private XorCondition(final Condition left, final Condition right) {
            super(cast(left), cast(right));
        }

        public static Condition of(final Condition left, final Condition right) {
            if (left == null && right == null) {
                return AlwaysCondition.of();
            }
            if (left == null) {
                return right;
            }
            if (right == null) {
                return left;
            }
            return new XorCondition(left, right);
        }

        @Override
        public boolean test(final ActionContext context) {
            return left.test(context) ^ right.test(context);
        }
    }

    /** Negates a child condition. */
    final class NotCondition implements Condition {
        private final Condition delegate;

        private NotCondition(final Condition delegate) {
            this.delegate = delegate;
        }

        public static Condition of(final Condition delegate) {
            if (delegate == null) {
                return NeverCondition.of();
            }
            return new NotCondition(delegate);
        }

        public Condition delegate() {
            return delegate;
        }

        @Override
        public boolean test(final ActionContext context) {
            return !delegate.test(context);
        }

        @Override
        public Condition negate() {
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
    final class AlwaysCondition implements Condition {
        private static final AlwaysCondition INSTANCE = new AlwaysCondition();

        private AlwaysCondition() {
        }

        public static Condition of() {
            return (AlwaysCondition) INSTANCE;
        }

        @Override
        public boolean test(final ActionContext context) {
            return true;
        }

        @Override
        public Condition negate() {
            return NeverCondition.of();
        }

        @Override
        public String toString() {
            return "AlwaysCondition";
        }
    }

    /** Always-false condition. */
    final class NeverCondition implements Condition {
        private static final NeverCondition INSTANCE = new NeverCondition();

        private NeverCondition() {
        }

        public static Condition of() {
            return (NeverCondition) INSTANCE;
        }

        @Override
        public boolean test(final ActionContext context) {
            return false;
        }

        @Override
        public Condition negate() {
            return AlwaysCondition.of();
        }

        @Override
        public String toString() {
            return "NeverCondition";
        }
    }

    /** Adapts a predicate into a condition leaf. */
    final class PredicateCondition implements Condition {
        private final Predicate<ActionContext> predicate;

        private PredicateCondition(final Predicate<ActionContext> predicate) {
            this.predicate = predicate;
        }

        public static Condition of(final Predicate<ActionContext> predicate) {
            return predicate == null ? AlwaysCondition.of() : new PredicateCondition(predicate);
        }

        @Override
        public boolean test(final ActionContext context) {
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
