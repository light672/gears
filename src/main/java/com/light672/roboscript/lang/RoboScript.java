package com.light672.roboscript.lang;
public abstract class RoboScript {

	/**
	 * Includes a module globally that can be accessed through any RoboScript instance.
	 *
	 * @param module The included module.
	 */
	public static void includeModule(RoboScriptModule module) {

	}

	public void runString(String message) {

	}

	public abstract void reportRuntimeError(String message);

	public abstract void reportCompilationError(String message);

	public abstract void reportCompilationWarning(String message);
}