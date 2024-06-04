package com.origin.library.infrastructure.exp;

import org.springframework.data.jpa.domain.Specification;

public class SelectDataset {
	private SelectClause selectClause;

	public SelectDataset() {
		this.selectClause = new SelectClause();
	}

	public SelectDataset clone() {
		SelectDataset selectDataset = new SelectDataset();
		selectDataset.selectClause = selectClause.clone();
		return selectDataset;
	}

	public <T> Specification<T> build() {
		return selectClause.build();
	}

	public SelectDataset select(String... columns) {
		selectClause.setSelectColumns(columns);
		return this;
	}

	public SelectDataset from(String from) {
		selectClause.setFrom(new String[] { from });
		return this;
	}

	public SelectDataset where(LambdaQueryWrapper wrapper) {
		selectClause.setWhere(wrapper.getExpressions());
		return this;
	}

	public SelectDataset groupBy(String... columns) {
		selectClause.setGroupBy(columns);
		return this;
	}

	public SelectDataset having(LambdaQueryWrapper wrapper) {
		selectClause.setHaving(wrapper.getExpressions());
		return this;
	}

	// FIXME: implement order by foo asc, bar desc
	public SelectDataset orderBy(boolean isAsc, String... columns) {
		OrderBy[] orderBys = new OrderBy[columns.length];
		for (int i = 0; i < columns.length; i++) {
			orderBys[i] = new OrderBy(isAsc, columns[i]);
		}
		selectClause.setOrder(orderBys);
		return this;
	}

	public SelectDataset orderByAsc(String... columns) {
		return orderBy(true, columns);
	}

	public SelectDataset orderByDesc(String... columns) {
		return orderBy(false, columns);
	}

	public SelectDataset offset(int offset) {
		selectClause.setOffset(offset);
		return this;
	}

	public SelectDataset limit(int limit) {
		selectClause.setLimit(limit);
		return this;
	}
}
