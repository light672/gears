package com.light672.roboscript.lang;
import java.util.List;

abstract class Statement {
	interface Visitor<R> {
		R visitExpressionStatement(ExpressionStatement statement);

		R visitVarStatement(Var statement);

		R visitIfStatement(If statement);

		R visitLoopStatement(Loop statement);

		R visitWhileStatement(While statement);

		R visitForStatement(For statement);

		R visitReturnStatement(Return statement);

		R visitFunctionStatement(Function statement);
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

		Var(Token name, Type type, Expression initializer, boolean constant) {
			this.name = name;
			this.type = type;
			this.initializer = initializer;
			this.constant = constant;
		}

		final Token name;
		final Type type;
		final Expression initializer;
		final boolean constant;

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

	static class While extends Statement {
		While(Expression condition, List<Statement> body) {
			this.body = body;
		}

		final List<Statement> body;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitWhileStatement(this);
		}
	}

	static class For extends Statement {
		For(Var iterator, Expression iterable, List<Statement> body) {
			this.iterator = iterator;
			this.iterable = iterable;
			this.body = body;
		}

		final Var iterator;
		final Expression iterable;
		final List<Statement> body;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitForStatement(this);
		}
	}

	static class Return extends Statement {
		Return(Token keyword, Expression value) {
			this.keyword = keyword;
			this.value = value;
		}

		final Token keyword;
		final Expression value;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitReturnStatement(this);
		}
	}

	static class Function extends Statement {
		Function(List<Var> parameters, Type returnType, List<Statement> body) {
			this.parameters = parameters;
			this.returnType = returnType;
			this.body = body;
		}

		final List<Var> parameters;
		final Type returnType;
		final List<Statement> body;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitFunctionStatement(this);
		}
	}


	abstract <R> R accept(Visitor<R> visitor);
}
