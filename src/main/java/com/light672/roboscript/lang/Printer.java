package com.light672.roboscript.lang;
import static com.light672.roboscript.lang.OpCode.*;

public class Printer {
	private int printCounter = 0;
	final Chunk chunk;

	Printer(Chunk chunk) {
		this.chunk = chunk;
	}

	void print() {
		for (this.printCounter = 0; this.printCounter < this.chunk.code.length; this.printCounter++) {
			switch (this.chunk.code[this.printCounter]) {
				case OP_CONSTANT -> System.out.println(this.shortInstruction("CON"));
				case OP_TRUE -> System.out.println(this.simpleInstruction("TRU"));
				case OP_FALSE -> System.out.println(this.simpleInstruction("FAL"));
				case OP_NULL -> System.out.println(this.simpleInstruction("NUL"));
				case OP_POP -> System.out.println(this.simpleInstruction("POP"));
				case OP_ADD -> System.out.println(this.simpleInstruction("ADD"));
				case OP_SUBTRACT -> System.out.println(this.simpleInstruction("SUB"));
				case OP_MULTIPLY -> System.out.println(this.simpleInstruction("MUL"));
				case OP_DIVIDE -> System.out.println(this.simpleInstruction("DIV"));
				case OP_JUMP -> System.out.println(this.shortInstruction("JMP"));
				case OP_JUMP_IF_FALSE -> System.out.println(this.shortInstruction("JMF"));
				case OP_LOOP -> System.out.println(this.shortInstruction("LOP"));
				case OP_RETURN -> System.out.println(this.byteInstruction("RET"));
				case OP_INTERRUPT -> System.out.println(this.simpleInstruction("INT"));
			}
		}
	}

	String simpleInstruction(String s) {
		return String.format("%03d  %s", this.printCounter, s);
	}


	String shortInstruction(String s) {

		return String.format("%03d  %s  %03d", this.printCounter, s, this.readShort());
	}

	String byteInstruction(String s) {
		byte num = this.readByte();
		return String.format("%03d  %s  %03d", this.printCounter, s, num);
	}

	private byte readByte() {
		return this.chunk.code[this.printCounter++];
	}

	private short readShort() {
		this.printCounter += 2;
		return (short) ((this.chunk.code[this.printCounter - 1] << 8) | (this.chunk.code[this.printCounter] & 0xFF));
	}
}
