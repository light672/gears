package com.light672.roboscript.lang;
public interface OpCode {
	byte OP_CONSTANT = 0;
	byte OP_TRUE = 1;
	byte OP_FALSE = 2;
	byte OP_NULL = 3;
	byte OP_POP = 4;
	byte OP_ADD = 5;
	byte OP_SUBTRACT = 6;
	byte OP_MULTIPLY = 7;
	byte OP_DIVIDE = 8;
	byte OP_RETURN = 9;
	byte OP_INTERRUPT = 10;

}
