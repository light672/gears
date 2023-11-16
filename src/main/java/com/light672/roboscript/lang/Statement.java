package com.light672.roboscript.lang;
import java.util.List;

abstract class Statement {
	interface Visitor<R> {
		R visitExpressionStatement(ExpressionStatement statement);

		R visitIfStatement(If statement);

		R visitLoopStatement(Loop statement);

		R visitVarStatement(Var statement);

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

	static class Var extends Statement {

		Var(Token name, Type type, Expression initializer, boolean functionArgument) {
			this.name = name;
			this.type = type;
			this.initializer = initializer;
			this.functionArgument = functionArgument;
		}

		final Token name;
		final Type type;
		final Expression initializer;
		final boolean functionArgument;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVarStatement(this);
		}
	}

	static class If extends Statement {
		If(Expression condition, List<Statement> thenBranch, List<Statement> elsebranch) {
			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elsebranch;
		}

		final Expression condition;
		final List<Statement> thenBranch;
		final List<Statement> elseBranch;


		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitIfStatement(this);
		}
	}

	static class Loop extends Statement {
		Loop(List<Statement> body) {
			this.body = body;
		}

		final List<Statement> body;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLoopStatement(this);
		}
	}


	abstract <R> R accept(Visitor<R> visitor);
}
