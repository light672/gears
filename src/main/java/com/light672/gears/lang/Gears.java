package com.light672.gears.lang;
import java.util.List;

public abstract class Gears {

	/**
	 * Includes a module globally that can be accessed through any RoboScript instance.
	 *
	 * @param module The included module.
	 */
	public static void includeModule(GearsModule module) {

	}

	public void runString(String source) {
		Parser parser = new Parser(this);
		List<Statement> statements = parser.parse(source);
		new ASTPrinter(statements).print();
		/*Compiler compiler = new Compiler(this);
		Chunk chunk = compiler.compile(statements);
		new ChunkPrinter(chunk).print();
		System.out.println("\n");
		VirtualMachine vm = new VirtualMachine(this);
		vm.interpret(chunk);*/
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