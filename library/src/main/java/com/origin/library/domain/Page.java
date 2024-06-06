package com.origin.library.domain;

import java.util.List;

import lombok.Data;

@Data
public class Page<T> {
	protected long total;
	protected List<T> records;

	public Page(List<T> records, long total) {
		this.total = total;
		this.records = records;
	}

	public static <T> Page<T> empty() {
		return new Page<T>(List.of(), 0);
	}

	public static <T> Page<T> of(org.springframework.data.domain.Page<T> page) {
		return new Page<T>(page.getContent(), page.getTotalElements());
	}
}
