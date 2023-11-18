package com.light672.roboscript.lang;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import static com.light672.roboscript.lang.Token.TokenType.*;

public class Parser {
	private final RoboScript roboScriptInstance;
	private Token previous;
	private Token current;

	private Scanner scanner;

	Parser(RoboScript roboScriptInstance) {
		this.roboScriptInstance = roboScriptInstance;
	}

	List<Statement> parse(String source) {
		List<Statement> statements = new ArrayList<>();
		this.scanner = new Scanner(source);
		while (!this.isAtEnd()) {
			statements.add(this.declaration());
		}
		return statements;
	}

	private List<Statement> parseBlock() {
		List<Statement> statements = new ArrayList<>();
		while (this.isNextToken(RIGHT_BRACE) && !this.isAtEnd()) {
			statements.add(this.declaration());
		}
		this.consumeOrThrow(RIGHT_BRACE, "Expected '}' to close block.");
		return statements;
	}

	private Statement declaration() {
		try {
			if (this.matchAndAdvance(VAR)) {
				return this.varDeclaration();
			} else if (this.matchAndAdvance(FUNCTION)) {
				return this.functionDeclaration();
			} else {
				return this.statement();
			}
		} catch (ParseError error) {
			// synchronize
			return null;
		}
	}

	private Statement varDeclaration() {

	}

	private Statement.Var parseVariable(boolean constant) {
		this.consumeOrThrow(IDENTIFIER, "Expected variable name.");
		Token name = this.previous;
		Type type;
		if (this.matchAndAdvance(COLON)) {
			type = this.parseType("Expected a type after ':'.");
		} else {
			type = new Type.Any(true);
		}
		Expression initializer = null;
		if (this.matchAndAdvance(EQUAL)) {
			initializer = this.exponent();
		}
		this.consumeOrThrow(SEMICOLON, "Expected ';' after statement.");
		return new Statement.Var(name, type, initializer, constant);
	}

	private Type parseType(String errorMessage) {
		if (!this.matchAndAdvance(ANY, STRING, NUMBER, BOOL, RANGE, LIST, MAP, FUNCTION, CLASS, IDENTIFIER)) {
			throw this.errorAtCurrent(errorMessage);
		}
		Token typeToken = this.previous;
		boolean nullable = this.matchAndAdvance(QUESTION);
		return switch (typeToken.type) {
			case ANY -> new Type.Any(nullable);
			case STRING -> new Type.String(nullable);
			case NUMBER -> {
				if (nullable) throw this.error("Type 'number' cannot be nullable.");
				yield new Type.Number();
			}
			case BOOL -> {
				if (nullable) throw this.error("Type 'bool' cannot be nullable.");
				yield new Type.Bool();
			}
			case RANGE -> new Type.Range(nullable);
			case LIST -> {
				if (!this.matchAndAdvance(LESS)) yield new Type.List(new Type.Any(true), nullable);
				Type t = new Type.List(this.parseType("Expected a type after '<'."), nullable);
				this.consumeOrThrow(GREATER, "Expected '>' after list entry type.");
				yield t;
			}
			case MAP -> {
				if (!this.matchAndAdvance(LESS)) yield new Type.Map(new Type.Any(true), new Type.Any(true), nullable);
				Type keyType = this.parseType("Expected a type for map key type.");
				this.consumeOrThrow(COMMA, "Expected ',' after key type.");
				Type valueType = this.parseType("Expected a type for map value type.");
				this.consumeOrThrow(GREATER, "Expected '>' after map value type.");
				yield new Type.Map(keyType, valueType, nullable);
			}
			case FUNCTION -> {
				List<Type> argTypes = new ArrayList<>();
				if (this.matchAndAdvance(LESS)) {
					do {
						argTypes.add(this.parseType("Expected a type in function type arguments."));
					} while (this.matchAndAdvance(COMMA));
					this.consumeOrThrow(GREATER, "Expected '>' after function type arguments.");
				}
				Type returnType = this.matchAndAdvance(ARROW) ? this.parseType(
						"Expected a type in function type return type.") : new Type.Any(true);
				yield new Type.Function(argTypes, returnType, nullable);
			}
			case CLASS -> {
				Token identifier = null;
				if (this.matchAndAdvance(LESS)) {
					this.consumeOrThrow(IDENTIFIER, "Expected a class name after '<'.");
					identifier = this.previous;
					this.consumeOrThrow(GREATER, "Expected '>' after class type.");
				}
				yield new Type.Class(identifier, nullable);
			}
			case IDENTIFIER -> new Type.Object(typeToken, nullable);
			default -> throw new IllegalArgumentException("this should not be possible");
		};
	}

	private Statement statement() {
		if (this.matchAndAdvance(IF)) {
			return this.ifStatement();
		} else if (this.matchAndAdvance(WHILE)) {
			return this.whileStatement();
		} else if (this.matchAndAdvance(FOR)) {
			return this.forStatement();
		} else if (this.matchAndAdvance(LOOP)) {
			return this.loopStatement();
		} else if (this.matchAndAdvance(RETURN)) {
			return this.returnStatement();
		} else {
			return this.expressionStatement();
		}
	}

	private Statement.If ifStatement() {
		Expression expression = this.expression();
		this.consumeOrThrow(LEFT_BRACE, "Expected '{' after if statement expression.");
		List<Statement> block = this.parseBlock();
		List<Statement> elseBlock;
		if (this.matchAndAdvance(ELSE)) {
			if (this.matchAndAdvance(IF)) {
				elseBlock = List.of(this.ifStatement());
			} else {
				this.consumeOrThrow(LEFT_BRACE, "Expected '{' after 'else'.");
				elseBlock = this.parseBlock();
			}
		} else if (this.matchAndAdvance(ELIF)) {
			elseBlock = List.of(this.ifStatement());
		} else {
			elseBlock = new ArrayList<>();
		}
		return new Statement.If(expression, block, elseBlock);
	}

	private Statement.While whileStatement() {
		Expression expression = this.expression();
		this.consumeOrThrow(LEFT_BRACE, "Expected '{' after while statement expression.");
		return new Statement.While(expression, this.parseBlock());
	}

	private Statement.For forStatement() {
		this.consumeOrThrow(IDENTIFIER, "Expected variable name after 'for'.");
		Statement.Var iterator = this.parseVariable(true);
		this.consumeOrThrow(COLON, "Expected ':' after variable name.");
		Expression iterable = this.expression();
		this.consumeOrThrow(LEFT_BRACE, "Expected '{' after for loop iterable.");
		return new Statement.For(iterator, iterable, this.parseBlock());
	}

	private Statement.Loop loopStatement() {
		this.consumeOrThrow(LEFT_BRACE, "Expected '{' after 'loop'.");
		return new Statement.Loop(this.parseBlock());
	}

	private Statement.Return returnStatement() {
		Token keyword = this.previous;
		Expression returnValue = !this.isNextToken(SEMICOLON) ? this.expression() : null;
		this.consumeOrThrow(SEMICOLON, "Expected ';' after return statement.");
		return new Statement.Return(keyword, returnValue);
	}

	private Statement.ExpressionStatement expressionStatement() {
		Expression expression = this.expression();
		this.consumeOrThrow(SEMICOLON, "Expected ';' after expression statement.");
		return new Statement.ExpressionStatement(expression);
	}

	private Expression expression() {
		return this.assignment(); // start the precedence chain
	}

	private Expression assignment() {
		Expression expression = this.or();
		if (this.matchAndAdvance(EQUAL)) {
			Token equals = this.previous;
			Expression value = this.assignment();
			if (expression instanceof Expression.Variable var)
				return new Expression.Assign(var, value);
			throw this.errorAt(equals, "Invalid assignment target.");
		}
		return expression;
	}

	private Expression or() {
		Expression expression = this.and();
		while (this.matchAndAdvance(OR)) {
			Expression right = this.and();
			expression = new Expression.Logical(expression, right, Expression.Logical.Operation.OR);
		}
		return expression;
	}

	private Expression and() {
		Expression expression = this.equality();
		while (this.matchAndAdvance(BANG_EQUAL, EQUAL_EQUAL)) {
			Token operator = this.previous;
			Expression right = this.equality();
			expression = new Expression.Logical(expression, right, Expression.Logical.Operation.AND);
		}
		return expression;
	}

	private Expression equality() {
		Expression expression = this.comparison();
		while (this.matchAndAdvance(BANG_EQUAL, EQUAL_EQUAL)) {
			Token operator = this.previous;
			Expression right = this.comparison();
			expression = new Expression.Binary(expression, right,
					operator.type == EQUAL_EQUAL ? Expression.Binary.Operation.EQUAL : Expression.Binary.Operation.NOT_EQUAL);
		}
		return expression;
	}

	private Expression comparison() {
		Expression expression = this.modulo();
		while (this.matchAndAdvance(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
			Token operator = this.previous;
			Expression right = this.modulo();
			expression = new Expression.Binary(expression, right, switch (operator.type) {
				case GREATER -> Expression.Binary.Operation.GREATER;
				case GREATER_EQUAL -> Expression.Binary.Operation.GREATER_EQUAL;
				case LESS -> Expression.Binary.Operation.LESS;
				case LESS_EQUAL -> Expression.Binary.Operation.LESS_EQUAL;
				default -> throw new IllegalArgumentException("how did this happen?");
			});
		}
		return expression;
	}

	private Expression modulo() {
		Expression expression = this.term();
		while (this.matchAndAdvance(PERCENT)) {
			Token operator = this.previous;
			Expression right = this.term();
			expression = new Expression.Binary(expression, right, Expression.Binary.Operation.MODULO);
		}
		return expression;
	}

	private Expression term() {
		Expression expression = this.factor();
		while (this.matchAndAdvance(PLUS, MINUS)) {
			Token operator = this.previous;
			Expression right = this.factor();
			expression = new Expression.Binary(expression, right,
					operator.type == PLUS ? Expression.Binary.Operation.ADDITION : Expression.Binary.Operation.SUBTRACTION);
		}
		return expression;
	}

	private Expression factor() {
		Expression expression = this.exponent();
		while (this.matchAndAdvance(SLASH, STAR)) {
			Token operator = this.previous;
			Expression right = this.exponent();
			expression = new Expression.Binary(expression, right,
					operator.type == SLASH ? Expression.Binary.Operation.DIVISION : Expression.Binary.Operation.MULTIPLICATION);
		}
		return expression;
	}

	private Expression exponent() {
		Expression expression = this.unary();
		while (this.matchAndAdvance(CARET)) {
			Token operator = this.previous;
			Expression right = this.unary();
			expression = new Expression.Binary(expression, right, Expression.Binary.Operation.EXPONENT);
		}
		return expression;
	}

	private Expression unary() {
		if (this.matchAndAdvance(BANG, MINUS)) {
			Token operator = this.previous;
			Expression expression = this.unary();
			return new Expression.Unary(expression,
					operator.type == BANG ? Expression.Unary.Operation.NOT : Expression.Unary.Operation.NEGATE);
		}
		return this.call();
	}

	private Expression call() {
		Expression expression = this.primary();

		while (true) {
			if (this.matchAndAdvance(LEFT_PAREN))
				expression = this.finishCall(expression);
			else break;
			// TODO: class '.' calling, array '[' calling.
		}
		return expression;
	}

	private Expression finishCall(Expression callee) {
		List<Expression> arguments = new ArrayList<>();
		if (!this.isNextToken(RIGHT_PAREN)) {
			do {
				arguments.add(this.expression());
			} while (this.matchAndAdvance(COMMA));
		}

		this.consumeOrThrow(RIGHT_PAREN, "Expected ')' after arguments.");
		return new Expression.Call(callee, arguments);
	}

	private Expression primary() {
		if (this.matchAndAdvance(FALSE)) return new Expression.Literal(false);
		if (this.matchAndAdvance(TRUE)) return new Expression.Literal(true);
		if (this.matchAndAdvance(NULL)) return new Expression.Literal(null);
		if (this.matchAndAdvance(DOUBLE_VALUE)) return new Expression.Literal(Double.parseDouble(this.previous.lexeme));
		if (this.matchAndAdvance(STRING_VALUE)) return new Expression.Literal(this.previous.lexeme);
		if (this.matchAndAdvance(IDENTIFIER)) return new Expression.Variable(this.previous);

		if (this.matchAndAdvance(LEFT_PAREN)) {
			Expression expression = this.expression();
			this.consumeOrThrow(RIGHT_PAREN, "Expected ')' after grouping expression.");
			return new Expression.Grouping(expression);
		}

		throw this.errorAtCurrent("Expected expression.");
	}

	private void advance() {
		this.previous = this.current;
		this.current = this.scanner.scanToken();
		if (this.current.type == ERROR)
			throw this.errorAtCurrent(this.current.lexeme);
	}

	private boolean isNextToken(Token.TokenType type) {
		return this.current.type == type;
	}

	private boolean isNextToken(Token.TokenType... types) {
		for (Token.TokenType type : types) {
			if (this.current.type == type) {
				return true;
			}
		}
		return false;
	}

	private boolean matchAndAdvance(Token.TokenType type) {
		if (!this.isNextToken(type)) return false;
		this.advance();
		return true;
	}

	private boolean matchAndAdvance(Token.TokenType... types) {
		if (!this.isNextToken(types)) return false;
		this.advance();
		return true;
	}

	private void consumeOrThrow(Token.TokenType type, String message) {
		if (this.current.type == type) {
			this.advance();
			return;
		}
		throw this.errorAtCurrent(message);
	}

	private ParseError error(String message) {
		return this.errorAt(this.previous, message);
	}

	private ParseError errorAtCurrent(String message) {
		return this.errorAt(this.current, message);
	}

	private ParseError errorAt(Token token, String message) {
		String finalMessage = "Error";
		if (token.type == EOF) {
			finalMessage += " at end";
		} else if (token.type != ERROR) {
			finalMessage += " at " + token.lexeme;
		} else {
			finalMessage += " with scanning";
		}
		finalMessage += ": '" + message + "'";
		this.reportError(token.line, finalMessage);
		return new ParseError();
	}

	private boolean isAtEnd() {
		return this.current.type == EOF;
	}

	private void reportError(int line, String finalMessage) {

	}

	private static class ParseError extends RuntimeException {

		@Serial
		private static final long serialVersionUID = -3616635255734899575L;
	}
}
