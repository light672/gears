package com.light672.roboscript.lang;
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
		List<Statement> statements = List.of(
				new Statement.If(new Expression.Literal(true), List.of(
						new Statement.ExpressionStatement(new Expression.Literal("ended up being true"))
				), List.of(
						new Statement.ExpressionStatement(new Expression.Literal("ended up being false"))
				)),
				new Statement.ExpressionStatement(new Expression.Literal("end"))
		);

		Compiler compiler = new Compiler(this);
		Chunk chunk = compiler.compile(statements);
		new Printer(chunk).print();
		System.out.println("\n");
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