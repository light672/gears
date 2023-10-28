package com.light672.roboscript;

import com.light672.roboscript.lang.RoboScript;

public class Main {
	public static void main(String[] args) {
		RoboScript roboScript = new RoboScript() {

			@Override
			public void reportRuntimeError(String message) {
				System.err.println(message);
			}

			@Override
			public void reportCompilationError(String message) {
				System.err.println(message);
			}

			@Override
			public void reportCompilationWarning(String message) {
				System.out.println("WARNING: " + message);
			}
		};
	}
}