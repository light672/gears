package com.light672.roboscript.lang;
public interface OpCode {
	byte OP_CONSTANT = 0;
	byte OP_ADD = 1;
	byte OP_ADD_NUMBER = 2;
	byte OP_ADD_STRING = 3;
	byte OP_SUBTRACT = 4;
	byte OP_SUBTRACT_NUMBER = 5;
	byte OP_SUBTRACT_STRING = 6;
	byte OP_MULTIPLY = 7;
	byte OP_MULTIPLY_NUMBER = 8;
	byte OP_DIVIDE = 9;
	byte OP_DIVIDE_NUMBER = 10;
	byte OP_RETURN = 11;
	byte OP_INTERRUPT = 12;

}
