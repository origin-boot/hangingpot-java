package com.origin.hangingpot.infrastructure.querydsl;

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

	default int pageNumber(int pageNumber) {
		// PageRequest is 0-based while we use 1-based page number
		// when pageNumber is 0 means we dot not want to use paging
		if (pageNumber < 0) {
			throw new IllegalArgumentException("Page number must be greater than or equal to 0");
		}
		return pageNumber < 1 ? 1 : pageNumber;
	}

	default int pageSize(int pageSize) {
		if (pageSize < 1) {
			throw new IllegalArgumentException("Page size must be greater than 0");
		}
		return pageSize;
	}

	default int pageOffset(int pageNumber, int pageSize) {
		return (pageNumber(pageNumber) - 1) * pageSize(pageSize);
	}

	default Pageable pageable(@Nullable Sort sort, int pageNumber, int pageSize) {
		pageNumber = pageNumber(pageNumber);
		pageSize = pageSize(pageSize);
		if (sort == null) {
			return PageRequest.of(pageNumber-1, pageSize);
		}
		return PageRequest.of(pageNumber-1, pageSize, sort);
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
