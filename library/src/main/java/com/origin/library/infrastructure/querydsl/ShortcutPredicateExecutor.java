package com.origin.library.infrastructure.querydsl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.Nullable;

import com.origin.library.domain.Page;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

public interface ShortcutPredicateExecutor<T> extends ShortcutExecutor, QuerydslPredicateExecutor<T> {

	default Page<T> findAll(@Nullable Predicate predicate, @Nullable Sort sort, int pageNumber, int pageSize) {
		Pageable pageable = pageable(sort, pageNumber, pageSize);
		predicate = predicate != null ? predicate : new BooleanBuilder();
		return Page.of(findAll(predicate, pageable));
	}

	default Page<T> findAll(Sort sort, int pageNumber, int pageSize) {
		return findAll(null, sort, pageNumber, pageSize);
	}

	default Page<T> findAll(int pageNumber, int pageSize) {
		return findAll(null, pageNumber, pageSize);
	}

	default Page<T> findAll(int pageSize) {
		return findAll(null, 0, pageSize);
	}
}
