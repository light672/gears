package com.light672.roboscript;

import com.light672.roboscript.lang.RoboScript;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) return;
		RoboScript roboScript = new RoboScript() {
			@Override
			public void handlePrintStatement(String message) {
				System.out.print(message);
			}

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
		roboScript.runString(Files.readString(Paths.get(args[0])));
	}
}