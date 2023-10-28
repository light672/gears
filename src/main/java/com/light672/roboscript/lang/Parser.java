package com.light672.roboscript.lang;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import static com.light672.roboscript.lang.Token.TokenType.EOF;
import static com.light672.roboscript.lang.Token.TokenType.ERROR;

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
		return statements;
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

	boolean isAtEnd() {
		return this.current.type == EOF;
	}

	private void reportError(int line, String finalMessage) {

	}

	private static class ParseError extends RuntimeException {

		@Serial
		private static final long serialVersionUID = -3616635255734899575L;
	}
}
