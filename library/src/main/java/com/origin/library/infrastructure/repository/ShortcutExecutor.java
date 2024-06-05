package com.origin.library.infrastructure.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

public interface ShortcutExecutor {
	default boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

	default boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

	default boolean isBlank(String s) {
		return s == null || s.isBlank();
	}

	default boolean isNotBlank(String s) {
		return !isBlank(s);
	}

	default String quoteLike(String s) {
		return "%" + s.replace("%", "\\%").replace("_", "\\_") + "%";
	}

	default Pageable pageable(@Nullable Sort sort, int pageNumber, int pageSize) {
		if (pageNumber < 0) {
			throw new IllegalArgumentException("Page number must be greater than or equal to 0");
		}
		// PageRequest is 0-based while we use 1-based page number
		if (pageNumber > 0) {
			pageNumber = pageNumber - 1;
		}
		if (sort == null) {
			return PageRequest.of(pageNumber, pageSize);
		}
		return PageRequest.of(pageNumber, pageSize, sort);
	}

	default Pageable pageable(int pageNumber, int pageSize) {
		return pageable(null, pageNumber, pageSize);
	}

	default Sort asc(String... properties) {
		return Sort.by(properties);
	}

	default Sort desc(String... properties) {
		return Sort.by(Sort.Direction.DESC, properties);
	}

	default ShortcutPredicateBuilder predicate() {
		return new ShortcutPredicateBuilder();
	}
}
