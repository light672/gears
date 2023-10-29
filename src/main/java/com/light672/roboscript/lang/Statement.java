package com.light672.roboscript.lang;
import java.util.List;

abstract class Statement {
	interface Visitor<R> {
		R visitExpressionStatement(ExpressionStatement statement);

		R visitIfStatement(If statement);
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


	abstract <R> R accept(Visitor<R> visitor);
}
