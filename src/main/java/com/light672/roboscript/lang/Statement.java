package com.light672.roboscript.lang;
abstract class Statement {
	interface Visitor<R> {
		R visitExpressionStatement(ExpressionStatement statement);
	}

	static class ExpressionStatement extends Statement {

		ExpressionStatement(Expression expression) {
			this.expression = expression;
		}

		final Expression expression;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitExpressionStatement(this);
		}
	}


	abstract <R> R accept(Visitor<R> visitor);
}
