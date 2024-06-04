package com.origin.library.infrastructure.exp;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

abstract class BaseExpression {
	public <T> Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		if (this instanceof Expression) {
			Expression expression = (Expression) this;
			return expression.toPredicate(root, query, criteriaBuilder);
		}
		if (this instanceof ExpressionList) {
			ExpressionList expressionList = (ExpressionList) this;
			return expressionList.toPredicate(root, query, criteriaBuilder);
		}
		throw new RuntimeException("Unknown BaseExpression subInstance " + this.getClass().getName());
	}
}

enum Operator {
	AND, OR,
	EQ, NE,
	GT, GE,
	LT, LE,
	LIKE, NOT_LIKE,
	BETWEEN, NOT_BETWEEN,
	IN, NOT_IN,
	IS_NULL, IS_NOT_NULL;
}

class Bwtween {
	Object from;
	Object to;

	Bwtween(Object from, Object to) {
		this.from = from;
		this.to = to;
	}
}

class OrderBy {
	boolean isAsc;
	String column;

	OrderBy(boolean isAsc, String column) {
		this.isAsc = isAsc;
		this.column = column;
	}
}

@Data
@EqualsAndHashCode(callSuper = false)
class Expression extends BaseExpression {
	private String column;
	private Operator operator;
	private Object value;

	static Expression op(Operator operator, String column, Object value) {
		Expression expression = new Expression();
		expression.column = column;
		expression.operator = operator;
		expression.value = value;
		return expression;
	}

	public <T> Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		jakarta.persistence.criteria.Expression<String> x = root.get(this.column);
		switch (this.operator) {
			case EQ:
				return criteriaBuilder.equal(x, this.value);
			case NE:
				return criteriaBuilder.notEqual(x, this.value);
			case LIKE:
				return criteriaBuilder.like(x, this.value.toString());
			case NOT_LIKE:
				return criteriaBuilder.notLike(x, this.value.toString());
			case IS_NULL:
				return criteriaBuilder.isNull(x);
			case IS_NOT_NULL:
				return criteriaBuilder.isNotNull(x);
			// FIXME: Implement the rest of the operators
			default:
				throw new RuntimeException("TODO Expression operator " + this.operator);
		}
	}
}

class ExpressionList extends BaseExpression {
	Operator operator;
	BaseExpression[] expressions;

	static ExpressionList and(BaseExpression... expressions) {
		ExpressionList expressionList = new ExpressionList();
		expressionList.operator = Operator.AND;
		expressionList.expressions = expressions;
		return expressionList;
	}

	static ExpressionList or(BaseExpression... expressions) {
		ExpressionList expressionList = new ExpressionList();
		expressionList.operator = Operator.OR;
		expressionList.expressions = expressions;
		return expressionList;
	}

	public <T> Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		Predicate[] predicates = new Predicate[this.expressions.length];
		for (int i = 0; i < this.expressions.length; i++) {
			predicates[i] = this.expressions[i].toPredicate(root, query, criteriaBuilder);
		}
		if (this.operator == Operator.AND) {
			return criteriaBuilder.and(predicates);
		}
		if (this.operator == Operator.OR) {
			return criteriaBuilder.or(predicates);
		}
		throw new RuntimeException("Invalid ExpressionList operator " + this.operator);
	}
}
