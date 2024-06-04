package com.origin.library.infrastructure.exp;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.origin.library.domain.Page;

public interface RepositoryShortcutExecutor<T> extends JpaSpecificationExecutor<T> {
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

	default Pageable pageable(int pageNumber, int pageSize) {
		if (pageNumber < 1) {
			throw new IllegalArgumentException("Page number must be greater than 0");
		}
		return PageRequest.of(pageNumber-1, pageSize);
	}

	default Page<T> findAll(Specification<T> spec, int pageNumber, int pageSize) {
		return Page.of(findAll(spec, pageable(pageNumber, pageSize)));
	}

	default Page<T> findAll(Specification<T> spec, int pageSize) {
		return Page.of(findAll(spec, pageable(1, pageSize)));
	}
}
