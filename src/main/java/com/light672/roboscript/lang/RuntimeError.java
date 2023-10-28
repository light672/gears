package com.light672.roboscript.lang;
import java.io.Serial;

public class RuntimeError extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 8525251872887212490L;
	String message;

	public RuntimeError(String message) {
		super(message);
		this.message = message;
	}
}
