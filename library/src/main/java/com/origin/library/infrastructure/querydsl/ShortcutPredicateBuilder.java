package com.origin.library.infrastructure.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

public class ShortcutPredicateBuilder {
  private BooleanBuilder builder = new BooleanBuilder();

  public ShortcutPredicateBuilder and(boolean condition, Predicate b) {
    if (condition) {
      return and(b);
    }
    return this;
  }

  public ShortcutPredicateBuilder and(boolean condition, Predicate... b) {
    if (condition) {
      return and(b);
    }
    return this;
  }

  public ShortcutPredicateBuilder and(Predicate b) {
    builder.and(b);
    return this;
  }

  /**
   * Create the intersection of this and the union of the given args
   * {@code (this && (arg1 && arg2 ... && argN))}
   *
   * @param b union of predicates
   * @return the current object
   */
  public ShortcutPredicateBuilder and(Predicate... b) {
    if (b.length == 0) {
      return this;
    }
    builder.and(ExpressionUtils.allOf(b));
    return this;
  }

  public ShortcutPredicateBuilder andAnyOf(boolean condition, Predicate... b) {
    if (condition) {
      return addAnyOf(b);
    }
    return this;
  }

  /**
   * Create the intersection of this and the union of the given args
   * {@code (this && (arg1 || arg2 ... || argN))}
   *
   * @param b union of predicates
   * @return the current object
   */
  public ShortcutPredicateBuilder addAnyOf(Predicate... b) {
    if (b.length == 0) {
      return this;
    }
    builder.andAnyOf(b);
    return this;
  }

  public ShortcutPredicateBuilder or(boolean condition, Predicate b) {
    if (condition) {
      return or(b);
    }
    return this;
  }

  public ShortcutPredicateBuilder or(boolean condition, Predicate... b) {
    if (condition) {
      return or(b);
    }
    return this;
  }

  public ShortcutPredicateBuilder or(Predicate b) {
    builder.or(b);
    return this;
  }

  /**
   * Create the union of this and the intersection of the given args
   * {@code (this || (arg1 || arg2 ... || argN))}
   *
   * @param b intersection of predicates
   * @return the current object
   */
  public ShortcutPredicateBuilder or(Predicate... b) {
    if (b.length == 0) {
      return this;
    }
    builder.or(ExpressionUtils.anyOf(b));
    return this;
  }

  public ShortcutPredicateBuilder orAllOf(boolean condition, Predicate... b) {
    if (condition) {
      return orAllOf(b);
    }
    return this;
  }

  /**
   * Create the union of this and the intersection of the given args
   * {@code (this || (arg1 && arg2 ... && argN))}
   *
   * @param b intersection of predicates
   * @return the current object
   */
  public ShortcutPredicateBuilder orAllOf(Predicate... b) {
    if (b.length == 0) {
      return this;
    }
    builder.orAllOf(b);
    return this;
  }

  public BooleanBuilder build() {
    return builder;
  }
}