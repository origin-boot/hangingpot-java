package com.origin.library.infrastructure.util;

import org.springframework.data.domain.PageRequest;

public class PageUtil {
	public static PageRequest pageable(int pageNumber, int pageSize) {
		if (pageNumber < 1) {
			throw new IllegalArgumentException("Page number must be greater than 0");
		}
		return PageRequest.of(pageNumber-1, pageSize);
	}
}