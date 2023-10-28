package com.light672.roboscript.lang;
import java.util.Arrays;
import java.util.List;

public abstract class RoboScript {

	/**
	 * Includes a module globally that can be accessed through any RoboScript instance.
	 *
	 * @param module The included module.
	 */
	public static void includeModule(RoboScriptModule module) {

	}

	public void runString(String source) {
		Parser parser = new Parser(this);
		// List<Statement> statements = parser.parse(source);
		List<Statement> statements = Arrays.asList(new Statement.ExpressionStatement(
				new Expression.Binary(new Expression.Binary(new Expression.Literal(5d), new Expression.Literal(2d),
						Expression.Binary.Type.SUBTRACTION), new Expression.Literal(4d),
						Expression.Binary.Type.ADDITION)));
		Compiler compiler = new Compiler(this);
		Chunk chunk = compiler.compile(statements);
		VirtualMachine vm = new VirtualMachine(this);
		vm.interpret(chunk);
	}

	/**
	 * Should not print line as message will already include new line character if necessary
	 *
	 * @param message The message being printed (includes \n if necessary, do not println)
	 */
	public abstract void handlePrintStatement(String message);

	public abstract void reportRuntimeError(String message);

	public abstract void reportCompilationError(String message);

	public abstract void reportCompilationWarning(String message);
}