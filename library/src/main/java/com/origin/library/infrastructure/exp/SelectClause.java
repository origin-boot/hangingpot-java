package com.origin.library.infrastructure.exp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;

@Data
public class SelectClause {
	// FIXME: implement rest of the attributes
	private String[] commonTables;
	private String[] selectColumns;
	private String[] distinct;
	private String[] from;
	private Object[] joins;
	private BaseExpression[] where;
	private String alias;
	private String[] groupBy;
	private BaseExpression[] having;
	private OrderBy[] order;
	private int limit;
	private int offset;

	public SelectClause clone() {
		SelectClause selectClause = new SelectClause();
		selectClause.setCommonTables(this.commonTables);
		selectClause.setSelectColumns(this.selectColumns);
		selectClause.setDistinct(this.distinct);
		selectClause.setFrom(this.from);
		selectClause.setJoins(this.joins);
		selectClause.setWhere(this.where);
		selectClause.setAlias(this.alias);
		selectClause.setGroupBy(this.groupBy);
		selectClause.setHaving(this.having);
		selectClause.setOrder(this.order);
		selectClause.setLimit(this.limit);
		selectClause.setOffset(this.offset);
		return selectClause;
	}

	public <T> Specification<T> build() {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = toPredicates(root, query, criteriaBuilder);
				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		};
	}

	public <T> List<Predicate> toPredicates(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		List<Predicate> predicates = new ArrayList<>();
		for (BaseExpression expression : where) {
			predicates.add(expression.toPredicate(root, query, criteriaBuilder));
		}
		if (groupBy != null) {
			for (String column : groupBy) {
				query.groupBy(root.get(column));
			}
		}
		if (having != null) {
			for (BaseExpression expression : having) {
				predicates.add(expression.toPredicate(root, query, criteriaBuilder));
			}
		}
		if (order != null) {
			for (OrderBy or : order) {
				if (or.isAsc) {
					query.orderBy(criteriaBuilder.asc(root.get(or.column)));
				} else {
					query.orderBy(criteriaBuilder.desc(root.get(or.column)));
				}
			}
		}
		return predicates;
	}
}
