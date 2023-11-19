package com.light672.gears.lang;
final class Token {
	final TokenType type;
	final String lexeme;
	final int line;

	Token(TokenType type, String lexeme, int line) {
		this.type = type;
		this.lexeme = lexeme;
		this.line = line;
	}

	public enum TokenType {
		// Single-character tokens.
		LEFT_PAREN,
		RIGHT_PAREN,
		LEFT_BRACE,
		RIGHT_BRACE,
		LEFT_BRACKET,
		RIGHT_BRACKET,
		COMMA,
		DOT,
		PLUS,
		MINUS,
		STAR,
		SLASH,

		PERCENT,
		CARET,

		COLON,
		SEMICOLON,
		QUESTION,

		// One or two character tokens.
		BANG,
		BANG_EQUAL,
		EQUAL,
		EQUAL_EQUAL,
		PLUS_EQUAL,
		PLUS_PLUS,
		MINUS_EQUAL,
		MINUS_MINUS,
		STAR_EQUAL,
		SLASH_EQUAL,
		CARET_EQUAL,
		PERCENT_EQUAL,
		GREATER,
		GREATER_EQUAL,
		LESS,
		LESS_EQUAL,
		ARROW,

		// Literals.
		IDENTIFIER,
		STRING_VALUE,
		DOUBLE_VALUE,

		// Keywords.
		CLASS,
		FUNCTION,

		FOR,

		IF,
		ELSE,
		ELIF,
		WHILE,
		LOOP,

		TRUE,
		FALSE,

		AND,
		OR,

		NULL,

		RETURN,
		BREAK,
		VAR,

		ANY,
		STRING,
		NUMBER,
		BOOL,
		RANGE,
		LIST,
		MAP,


		EOF,
		ERROR,
		NA
	}

	@Override
	public String toString() {
		return "Token: " + this.type + ' ' + '"' + this.lexeme + '"';
	}


}