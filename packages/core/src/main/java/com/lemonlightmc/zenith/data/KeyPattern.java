package com.lemonlightmc.zenith.data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;

/**
 * An annotation used to annotate elements that must match against a valid
 * {@link Key} pattern.
 */
@Documented
@Pattern("(?:(" + KeyPattern.NAMESPACE_PATTERN + ":)?|:)" + KeyPattern.VALUE_PATTERN)
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER })
public @interface KeyPattern {
    /**
     * The pattern for namespaces.
     */
    @RegExp
    String NAMESPACE_PATTERN = "[a-z0-9_\\-.]+";
    /**
     * The pattern for values.
     */
    @RegExp
    String VALUE_PATTERN = "[a-z0-9_\\-./]+";

    /**
     * An annotation used to annotate elements that must match against a valid
     * {@link Key} {@link Key#namespace() namespace} pattern.
     */
    @Documented
    @Pattern(KeyPattern.NAMESPACE_PATTERN)
    @Retention(RetentionPolicy.CLASS)
    @Target({ ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER })
    @interface Namespace {
    }

    /**
     * An annotation used to annotate elements that must match against a valid
     * {@link Key} {@link Key#value() value} pattern.
     */
    @Documented
    @Pattern(KeyPattern.VALUE_PATTERN)
    @Retention(RetentionPolicy.CLASS)
    @Target({ ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER })
    @interface Value {
    }
}
