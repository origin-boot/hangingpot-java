package com.origin.library.infrastructure.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

public class ShortcutPredicateBuilder {
	private BooleanBuilder q = new BooleanBuilder();

	public ShortcutPredicateBuilder and(boolean condition, Predicate b) {
		if (condition) {
			return and(b);
		}
		return this;
	}

	public ShortcutPredicateBuilder and(Predicate b) {
		q.and(b);
		return this;
	}

	public ShortcutPredicateBuilder or(boolean condition, Predicate b) {
		if (condition) {
			return or(b);
		}
		return this;
	}

	public ShortcutPredicateBuilder or(Predicate b) {
		q.or(b);
		return this;
	}

	public BooleanBuilder build() {
		return q;
	}
}