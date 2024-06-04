package com.origin.library.infrastructure.exp;

import java.util.ArrayList;
import java.util.List;

public class LambdaQueryWrapper {
	private List<BaseExpression> expressions;

	public LambdaQueryWrapper() {
		this.expressions = new ArrayList<>();
	}

	public LambdaQueryWrapper and(LambdaQueryWrapperFunction wrapper) {
		BaseExpression tempExpressions[] = wrapper.exec(new LambdaQueryWrapper()).getExpressions();
		expressions.add(ExpressionList.and(tempExpressions));
		return this;
	}

	public LambdaQueryWrapper or(LambdaQueryWrapperFunction wrapper) {
		BaseExpression tempExpressions[] = wrapper.exec(new LambdaQueryWrapper()).getExpressions();
		expressions.add(ExpressionList.or(tempExpressions));
		return this;
	}

	public LambdaQueryWrapper when(boolean condition, LambdaQueryWrapperFunction wrapper) {
		if (condition) {
			BaseExpression tempExpressions[] = wrapper.exec(new LambdaQueryWrapper()).getExpressions();
			expressions.add(ExpressionList.and(tempExpressions));
		}
		return this;
	}

	BaseExpression[] getExpressions() {
		return expressions.toArray(new BaseExpression[expressions.size()]);
	}

	public LambdaQueryWrapper eq(String column, Comparable<?> value) {
		expressions.add(Expression.op(Operator.EQ, column, value));
		return this;
	}

	public LambdaQueryWrapper ne(String column, Comparable<?> value) {
		expressions.add(Expression.op(Operator.NE, column, value));
		return this;
	}

	public LambdaQueryWrapper gt(String column, Comparable<?> value) {
		expressions.add(Expression.op(Operator.GT, column, value));
		return this;
	}

	public LambdaQueryWrapper ge(String column, Comparable<?> value) {
		expressions.add(Expression.op(Operator.GE, column, value));
		return this;
	}

	public LambdaQueryWrapper lt(String column, Comparable<?> value) {
		expressions.add(Expression.op(Operator.LT, column, value));
		return this;
	}

	public LambdaQueryWrapper le(String column, Comparable<?> value) {
		expressions.add(Expression.op(Operator.LE, column, value));
		return this;
	}

	public LambdaQueryWrapper like(String column, String value) {
		expressions.add(Expression.op(Operator.LIKE, column, value));
		return this;
	}

	public LambdaQueryWrapper notLike(String column, String value) {
		expressions.add(Expression.op(Operator.NOT_LIKE, column, value));
		return this;
	}

	public <T> LambdaQueryWrapper between(String column, T from, T to) {
		expressions.add(Expression.op(Operator.BETWEEN, column, new Bwtween(from, to)));
		return this;
	}

	public <T> LambdaQueryWrapper notBetween(String column, T from, T to) {
		expressions.add(Expression.op(Operator.NOT_BETWEEN, column, new Bwtween(from, to)));
		return this;
	}

	public <T> LambdaQueryWrapper in(String column, List<T> value) {
		expressions.add(Expression.op(Operator.IN, column, value));
		return this;
	}

	public <T> LambdaQueryWrapper notIn(String column, List<T> value) {
		expressions.add(Expression.op(Operator.NOT_IN, column, value));
		return this;
	}

	public LambdaQueryWrapper isNull(String column) {
		expressions.add(Expression.op(Operator.IS_NULL, column, null));
		return this;
	}

	public LambdaQueryWrapper isNotNull(String column) {
		expressions.add(Expression.op(Operator.IS_NOT_NULL, column, null));
		return this;
	}
}
