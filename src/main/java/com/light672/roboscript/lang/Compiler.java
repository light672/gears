package com.light672.roboscript.lang;
import java.util.ArrayList;
import java.util.List;

import static com.light672.roboscript.lang.OpCode.*;

public class Compiler implements Statement.Visitor<Void>, Expression.Visitor<Void> {
	private final RoboScript roboScriptInstance;
	private final List<Byte> code = new ArrayList<>();
	private final List<Object> constants = new ArrayList<>();

	Compiler(RoboScript roboScriptInstance) {
		this.roboScriptInstance = roboScriptInstance;
	}

	Chunk compile(List<Statement> statements) {
		for (Statement statement : statements) {
			this.accept(statement);
		}
		this.emitByte(OP_INTERRUPT);
		byte[] code = new byte[this.code.size()];
		int[] lines = new int[this.code.size()];
		for (int i = 0; i < this.code.size(); i++) {
			code[i] = this.code.get(i);
		}
		return new Chunk(code, this.constants.toArray(), lines);
	}

	private void compileBlock(List<Statement> statements) {
		for (Statement statement : statements) {
			this.accept(statement);
		}
	}

	@Override
	public Void visitExpressionStatement(Statement.ExpressionStatement statement) {
		this.accept(statement.expression);
		this.emitByte(OP_POP);
		return null;
	}

	@Override
	public Void visitIfStatement(Statement.If statement) {
		this.accept(statement.condition);
		int thenJump = this.emitJump(OP_JUMP_IF_FALSE);
		this.emitByte(OP_POP); // pop the condition
		this.compileBlock(statement.thenBranch);
		int elseJump = this.emitJump(OP_JUMP);
		this.patchJump(thenJump);
		this.emitByte(OP_POP);

		this.patchJump(elseJump);
		return null;
	}

	@Override
	public Void visitBinaryExpression(Expression.Binary expression) {
		this.accept(expression.left);
		this.accept(expression.right);
		this.emitByte(switch (expression.type) {
			case ADDITION -> OP_ADD;
			case SUBTRACTION -> OP_SUBTRACT;
			case MULTIPLICATION -> OP_MULTIPLY;
			case DIVISION -> OP_DIVIDE;
		});
		return null;
	}

	@Override
	public Void visitLiteralExpression(Expression.Literal expression) {
		if (expression.literal instanceof Boolean b) {
			this.emitByte(b ? OP_TRUE : OP_FALSE);
		} else if (expression.literal == null) {
			this.emitByte(OP_NULL);
		} else {
			this.emitConstant(expression.literal);
		}
		return null;
	}

	private void accept(Expression e) {
		e.accept(this);
	}

	private void accept(Statement s) {
		s.accept(this);
	}

	private void emitConstant(Object o) {
		this.emitBytes(OP_CONSTANT);
		this.constants.add(o);
		this.emitShort((short) (this.constants.size() - 1));
	}

	private int emitJump(byte instruction) {
		this.emitBytes(instruction, (byte) 0xFF, (byte) 0xFF);
		return this.code.size() - 2;
	}

	private void patchJump(int offset) {
		int jump = this.code.size() - offset - 2;
		if (jump > Short.MAX_VALUE) {
			// too much code to jump over throw error
			return;
		}
		byte[] s = this.shortToBytes((short) jump);
		this.code.set(offset, s[0]);
		this.code.set(offset + 1, s[1]);
	}

	private void emitShort(short s) {
		this.emitBytes((byte) ((s >> 8) & 0xFF), (byte) (s & 0xFF));
	}

	private byte[] shortToBytes(short s) {
		byte[] b = new byte[2];
		b[0] = (byte) ((s >> 8) & 0xFF);
		b[1] = (byte) (s & 0xFF);
		return b;
	}

	private void emitByte(byte code) {
		this.code.add(code);
	}

	private void emitBytes(byte... bytes) {
		for (byte code : bytes) {
			this.emitByte(code);
		}
	}
}
