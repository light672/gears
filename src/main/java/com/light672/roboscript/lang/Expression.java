package com.light672.roboscript.lang;
abstract class Expression {
	interface Visitor<R> {
		R visitBinaryExpression(Binary expression);

		R visitLiteralExpression(Literal expression);
	}

	static class Binary extends Expression {
		enum Type {
			ADDITION,
			SUBTRACTION,
			MULTIPLICATION,
			DIVISION
		}

		Binary(Expression left, Expression right, Type type) {
			this.left = left;
			this.right = right;
			this.type = type;
		}

		final Expression left;
		final Expression right;
		final Type type;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpression(this);
		}
	}

	static class Literal extends Expression {
		Literal(Object literal) {
			this.literal = literal;
		}

		final Object literal;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralExpression(this);
		}
	}


	abstract <R> R accept(Visitor<R> visitor);
}
