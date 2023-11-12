package com.light672.roboscript.lang;
import java.util.List;

abstract class Expression {
	interface Visitor<R> {
		R visitGroupingExpression(Grouping expression);

		R visitVariableExpression(Variable expression);

		R visitAssignExpression(Assign expression);

		R visitCallExpression(Call expression);

		R visitBinaryExpression(Binary expression);

		R visitUnaryExpression(Unary expression);

		R visitLogicalExpression(Logical expression);

		R visitLiteralExpression(Literal expression);
	}

	static class Grouping extends Expression {

		Grouping(Expression expression) {
			this.expression = expression;
		}

		final Expression expression;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitGroupingExpression(this);
		}
	}

	static class Variable extends Expression {
		Variable(Token name) {
			this.name = name;
		}

		final Token name;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVariableExpression(this);
		}
	}

	static class Assign extends Expression {
		Assign(Expression.Variable target, Expression value) {
			this.target = target;
			this.value = value;
		}

		final Expression.Variable target;
		final Expression value;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitAssignExpression(this);
		}
	}

	static class Call extends Expression {
		Call(Expression callee, List<Expression> arguments) {
			this.callee = callee;
			this.arguments = arguments;
		}

		final Expression callee;
		final List<Expression> arguments;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitCallExpression(this);
		}
	}

	static class Binary extends Expression {
		enum Type {
			ADDITION,
			SUBTRACTION,
			MULTIPLICATION,
			DIVISION,
			MODULO,
			EXPONENT,
			EQUAL,
			NOT_EQUAL,
			GREATER,
			GREATER_EQUAL,
			LESS,
			LESS_EQUAL,

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

	static class Unary extends Expression {
		enum Type {
			NOT,
			NEGATE
		}

		Unary(Expression expression, Type type) {
			this.expression = expression;
			this.type = type;
		}

		final Expression expression;
		final Type type;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitUnaryExpression(this);
		}
	}

	static class Logical extends Expression {
		enum Type {
			AND,
			OR
		}

		Logical(Expression left, Expression right, Type type) {
			this.left = left;
			this.right = right;
			this.type = type;
		}

		final Expression left;
		final Expression right;
		final Type type;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLogicalExpression(this);
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
