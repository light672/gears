package com.light672.gears.lang;
import java.util.List;

public class ASTPrinter {
	final List<Statement> statements;

	ASTPrinter(List<Statement> statements) {
		this.statements = statements;
	}

	void print() {
		for (Statement statement : this.statements) {
			this.printStatement(statement, 0);
		}
	}

	private void printStatement(Statement statement, int indentLevel) {
		for (int i = 0; i < indentLevel; i++) {
			System.out.print("   ");
		}
		if (statement instanceof Statement.ExpressionStatement expressionStatement) {
			System.out.print("EXPRESSION ");
			this.printExpression(expressionStatement.expression);
			System.out.println();
		} else if (statement instanceof Statement.If ifStmt) {
			System.out.print("IF ");
			this.printExpression(ifStmt.condition);
			System.out.println();
			for (Statement stmt : ifStmt.thenBranch) {
				this.printStatement(stmt, indentLevel + 1);
			}
			for (int i = 0; i < indentLevel; i++) {
				System.out.print("   ");
			}
			System.out.println("ELSE ");
			for (Statement stmt : ifStmt.elseBranch) {
				this.printStatement(stmt, indentLevel + 1);
			}
		} else if (statement instanceof Statement.Var var) {
			System.out.print("VAR " + var.name.lexeme + " CONST=" + Boolean.toString(var.constant)
					.toUpperCase() + " : " + var.type + " ");
			if (var.initializer != null) this.printExpression(var.initializer);
			System.out.println();
		} else if (statement instanceof Statement.Loop loop) {
			System.out.println("LOOP ");
			for (Statement stmt : loop.body) {
				this.printStatement(stmt, indentLevel + 1);
			}
		} else if (statement instanceof Statement.Function function) {
			System.out.print("FUNCTION<");
			StringBuilder parameters = new StringBuilder();
			for (Statement.Var parameter : function.parameters) {
				parameters.append(parameter.name.lexeme).append(" : ").append(parameter.type.toString()).append(", ");
			}

			System.out.print(
					parameters.substring(0, parameters.length() - 2) + "> -> " + function.returnType.toString());
		} else if (statement instanceof Statement.While w) {
			System.out.print("WHILE ");
			this.printExpression(w.condition);
			System.out.println();
			for (Statement stmt : w.body) {
				this.printStatement(stmt, indentLevel + 1);
			}
		} else if (statement instanceof Statement.For f) {
			System.out.print("FOR " + f.iterator.name.lexeme + " : " + f.iterator.type.toString() + " ");
			this.printExpression(f.iterable);
			System.out.println();
			for (Statement stmt : f.body) {
				this.printStatement(stmt, indentLevel + 1);
			}
		} else if (statement instanceof Statement.Return r) {
			System.out.print("RETURN ");
			this.printExpression(r.value);
			System.out.println();
		}
	}

	/**
	 * Prints an expression and only uses 1 line.
	 *
	 * @param expression The expression being printed.
	 */
	private void printExpression(Expression expression) {
		System.out.print("(");
		if (expression instanceof Expression.Literal literal) {
			if (literal.literal == null) System.out.print("NULL");
			else System.out.print(literal.literal);
		} else if (expression instanceof Expression.Binary binary) {
			String operation = switch (binary.operation) {
				case ADDITION -> "ADD";
				case SUBTRACTION -> "SUBTRACT";
				case MULTIPLICATION -> "MULTIPLY";
				case DIVISION -> "DIVIDE";
				case MODULO -> "MODULO";
				case EXPONENT -> "EXPONENT";
				case EQUAL -> "EQUAL";
				case NOT_EQUAL -> "NOT_EQUAL";
				case GREATER -> "GREATER";
				case GREATER_EQUAL -> "GREATER_EQUAL";
				case LESS -> "LESS";
				case LESS_EQUAL -> "LESS_EQUAL";
			};
			System.out.print(operation + " ");
			this.printExpression(binary.left);
			System.out.print(" ");
			this.printExpression(binary.right);
		} else if (expression instanceof Expression.Variable variable) {
			System.out.print("VARIABLE [" + variable.name.lexeme + "]");
		} else if (expression instanceof Expression.Assign assign) {
			System.out.print("ASSIGN [" + assign.target.name.lexeme + "] ");
			this.printExpression(assign.value);
		} else if (expression instanceof Expression.Grouping grouping) {
			System.out.print("GROUPING ");
			this.printExpression(grouping.expression);
		} else if (expression instanceof Expression.Unary unary) {
			String operation = switch (unary.operation) {
				case NOT -> "NOT";
				case NEGATE -> "NEG";
			};
			System.out.print(operation + " ");
			this.printExpression(unary.expression);
		} else if (expression instanceof Expression.Logical logical) {
			String operation = switch (logical.operation) {
				case AND -> "AND";
				case OR -> "OR";
			};
			System.out.print(operation + " ");
			this.printExpression(logical.left);
			System.out.print(" ");
			this.printExpression(logical.right);
		} else if (expression instanceof Expression.Call call) {
			System.out.print("CALL <");
			for (int i = 0; i < call.arguments.size(); i++) {
				this.printExpression(call.arguments.get(i));
				if (i != call.arguments.size() - 1) {
					System.out.print(", ");
				}
			}
			System.out.print(">");
		}
		System.out.print(")");
	}
}
