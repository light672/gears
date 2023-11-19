package com.light672.gears;

import com.light672.gears.lang.Gears;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) return;
		Gears gears = new Gears() {
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
		gears.runString(Files.readString(Paths.get(args[0])));
	}
}