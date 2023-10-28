package com.light672.roboscript.lang;
public class Chunk {
	Chunk(byte[] code, Object[] constants, int[] lines) {
		this.code = code;
		this.constants = constants;
		this.lines = lines;
	}

	final byte[] code;
	final Object[] constants;
	final int[] lines;
}
