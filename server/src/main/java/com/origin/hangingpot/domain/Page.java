package com.origin.hangingpot.domain;

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
}
