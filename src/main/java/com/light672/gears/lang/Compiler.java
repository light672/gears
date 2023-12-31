package com.light672.gears.lang;
import java.util.ArrayList;
import java.util.List;

import static com.light672.gears.lang.OpCode.*;

public class Compiler implements Statement.Visitor<Void>, Expression.Visitor<Void> {
	private final Gears gearsInstance;
	private final List<Byte> code = new ArrayList<>();
	private final List<Object> constants = new ArrayList<>();

	Compiler(Gears gearsInstance) {
		this.gearsInstance = gearsInstance;
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

	@Override
	public Void visitExpressionStatement(Statement.ExpressionStatement statement) {
		this.accept(statement.expression);
		this.emitByte(OP_POP);
		return null;
	}

	@Override
	public Void visitVarStatement(Statement.Var statement) {
		return null;
	}

	@Override
	public Void visitIfStatement(Statement.If statement) {
		this.accept(statement.condition);
		int thenJump = this.emitJump(OP_JUMP_IF_FALSE);
		this.emitByte(OP_POP); // pop the condition
		this.accept(statement.thenBranch);
		int elseJump = this.emitJump(OP_JUMP);
		this.patchJump(thenJump);
		this.emitByte(OP_POP);
		this.accept(statement.elseBranch);
		this.patchJump(elseJump);
		return null;
	}

	@Override
	public Void visitLoopStatement(Statement.Loop statement) {
		int loopStart = this.code.size();
		this.accept(statement.body);
		this.emitLoop(loopStart);
		return null;
	}

	@Override
	public Void visitWhileStatement(Statement.While statement) {
		int loopStart = this.code.size();
		this.accept(statement.condition);
		int exitJump = this.emitJump(OP_JUMP_IF_FALSE);
		this.emitByte(OP_POP);
		this.accept(statement.body);
		this.emitLoop(loopStart);
		this.patchJump(exitJump);
		this.emitByte(OP_POP);
		return null;
	}

	@Override
	public Void visitForStatement(Statement.For statement) {
		return null;
	}

	@Override
	public Void visitReturnStatement(Statement.Return statement) {
		return null;
	}

	@Override
	public Void visitFunctionStatement(Statement.Function statement) {
		return null;
	}

	@Override
	public Void visitGroupingExpression(Expression.Grouping expression) {
		return null;
	}

	@Override
	public Void visitVariableExpression(Expression.Variable expression) {
		return null;
	}

	@Override
	public Void visitAssignExpression(Expression.Assign expression) {
		return null;
	}

	@Override
	public Void visitCallExpression(Expression.Call expression) {
		return null;
	}

	@Override
	public Void visitBinaryExpression(Expression.Binary expression) {
		this.accept(expression.left);
		this.accept(expression.right);
		this.emitByte(switch (expression.operation) {
			case ADDITION -> OP_ADD;
			case SUBTRACTION -> OP_SUBTRACT;
			case MULTIPLICATION -> OP_MULTIPLY;
			case DIVISION -> OP_DIVIDE;
			case MODULO -> OP_MODULO;
			case EXPONENT -> OP_EXPONENT;
			case EQUAL -> OP_EQUAL;
			case NOT_EQUAL -> OP_NOT_EQUAL;
			case GREATER -> OP_GREATER;
			case GREATER_EQUAL -> OP_GREATER_EQUAL;
			case LESS -> OP_LESS;
			case LESS_EQUAL -> OP_LESS_EQUAL;
		});
		return null;
	}

	@Override
	public Void visitUnaryExpression(Expression.Unary expression) {
		return null;
	}

	@Override
	public Void visitLogicalExpression(Expression.Logical expression) {
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

	private void accept(List<Statement> statements) {
		for (Statement statement : statements) {
			this.accept(statement);
		}
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

	private void emitLoop(int loopStart) {
		this.emitByte(OP_LOOP);
		int offset = this.code.size() - loopStart + 2;
		if (offset > Short.MAX_VALUE) {
			// too much code to jump over throw error
			return;
		}
		this.emitShort((short) offset);
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
