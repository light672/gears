package com.light672.gears.lang;
import static com.light672.gears.lang.OpCode.*;

public class VirtualMachine {
	private final Gears gearsInstance;
	private Chunk chunk;


	// actual vm stuff
	private final Object[] stack;
	private int stackSize = 0;
	private int programCounter = 0;
	private int basePointer = 0;

	private final Object[] globalVariables;

	boolean stopQueued = false;

	VirtualMachine(Gears gearsInstance, int stackSize, int globalsSize) {
		this.gearsInstance = gearsInstance;
		this.stack = new Object[stackSize];
		this.globalVariables = new Object[globalsSize];
	}

	VirtualMachine(Gears gearsInstance) {
		this(gearsInstance, 256, 256);
	}

	void interpret(Chunk chunk) {
		this.chunk = chunk;

		// initialize base values
		this.programCounter = 0;
		this.basePointer = 0;
		this.stackSize = 0;
		this.stopQueued = false;

		try {
			this.run();
		} catch (RuntimeError e) {
			this.gearsInstance.reportRuntimeError(
					"[line " + this.chunk.lines[this.programCounter] + "]: " + e.message);
		}
	}

	private void run() {
		// main loop
		for (; ; ) {
			if (this.stopQueued) return;

			switch (this.readByte()) {
				case OP_CONSTANT -> this.constantInstruction();
				case OP_TRUE -> this.pushStack(true);
				case OP_FALSE -> this.pushStack(false);
				case OP_NULL -> this.pushStack(null);
				case OP_POP -> this.gearsInstance.handlePrintStatement(this.stringify(this.popStack()) + '\n');
				case OP_ADD -> this.addInstruction();
				case OP_SUBTRACT -> this.subtractInstruction();
				case OP_MULTIPLY -> this.multiplyInstruction();
				case OP_DIVIDE -> this.divideInstruction();
				case OP_JUMP -> this.jumpInstruction();
				case OP_JUMP_IF_FALSE -> this.jumpIfFalseInstruction();
				case OP_LOOP -> this.loopInstruction();
				case OP_RETURN -> this.returnInstruction();
				case OP_INTERRUPT -> {
					return;
				}
			}
		}
	}


	private void constantInstruction() {
		this.pushStack(this.chunk.constants[this.readShort()]);
	}

	private void addInstruction() {
		Object b = this.popStack();
		Object a = this.popStack();

		if (a instanceof String || b instanceof String) {
			this.pushStack(this.stringify(a) + this.stringify(b));
		} else if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Addition must be between strings or numbers.");
		this.pushStack((double) a + (double) b);
	}

	private void subtractInstruction() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (!(b instanceof Double bd)) throw new RuntimeError("Subtrahend must always be a number.");
		if (a instanceof String s) {
			if (Math.floor(bd) != bd || bd < 0)
				throw new RuntimeError("Subtrahend in string subtraction must always be a positive whole number.");
			if (bd > s.length()) throw new RuntimeError("Subtrahend in string subtraction was '" + this.stringify(
					bd) + "', greater than string length of '" + s.length() + "'.");
			this.pushStack(s.substring(0, s.length() - (int) b));
		} else if (!(a instanceof Double))
			throw new RuntimeError("Subtraction must be between a string and a number or two numbers.");
		this.pushStack((double) a - bd);
	}

	private void multiplyInstruction() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Multiplication must be between two numbers.");
		this.pushStack((double) a * (double) b);
	}

	private void divideInstruction() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Division must be between two numbers.");
		if ((double) b == 0)
			throw new RuntimeError("Divisor may not be 0.");
		this.pushStack((double) a * (double) b);
	}

	private void jumpInstruction() {
		this.programCounter = this.readShort() + this.programCounter; // cannot be +=, readShort() changes pc and the old pc gets used in addition.
	}

	private void jumpIfFalseInstruction() {
		short jump = this.readShort();
		if (!isTruthy(this.peekStack()))
			this.programCounter += jump;
	}

	private void loopInstruction() {
		short jumpLocation = this.readShort();
		this.programCounter -= jumpLocation;
	}

	private void returnInstruction() {
		Object returnValue = this.popStack();
		this.basePointer = (int) this.popStack();
		this.programCounter = (int) this.popStack();
	}


	private byte readByte() {
		return this.chunk.code[this.programCounter++];
	}

	private short readShort() {
		this.programCounter += 2;
		return (short) ((this.chunk.code[this.programCounter - 2] << 8) | (this.chunk.code[this.programCounter - 1] & 0xFF));
	}

	private void pushStack(Object object) {
		this.stack[this.stackSize++] = object;
	}

	private Object popStack() {
		return this.stack[--this.stackSize];
	}

	private Object peekStack() {
		return this.stack[this.stackSize - 1];
	}

	private Object peekStack(int distance) {
		return this.stack[this.stackSize - 1 - distance];
	}

	private String stringify(Object object) {
		if (object == null) return "null";
		if (object instanceof Double) {
			String text = object.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}
		return object.toString();
	}

	private static boolean isTruthy(Object o) {
		if (o == null) return false;
		if (o instanceof Boolean b) return b;
		if (o instanceof Double d) return d != 0;
		return true;
	}
}
