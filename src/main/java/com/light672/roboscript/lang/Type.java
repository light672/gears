package com.light672.roboscript.lang;
public class Type {
	static class Any extends Type {
		Any(boolean nullable) {
			super(nullable);
		}
	}

	static class Object extends Type {
		Object(Token identifier, boolean nullable) {
			super(nullable);
			this.identifier = identifier;
		}

		final Token identifier;
	}

	static class Number extends Type {
		Number() {
			super(false);
		}
	}

	static class Bool extends Type {
		Bool() {
			super(false);
		}
	}

	static class String extends Type {
		String(boolean nullable) {
			super(nullable);
		}
	}

	static class Range extends Type {
		Range(boolean nullable) {
			super(nullable);
		}
	}

	static class List extends Type {
		List(Type entryType, boolean nullable) {
			super(nullable);
			this.entryType = entryType;
		}

		final Type entryType;
	}

	static class Map extends Type {
		Map(Type keyType, Type valueType, boolean nullable) {
			super(nullable);
			this.keyType = keyType;
			this.valueType = valueType;
		}

		final Type keyType;
		final Type valueType;
	}

	static class Function extends Type {

		Function(java.util.List<Type> argumentTypes, Type returnType, boolean nullable) {
			super(nullable);
			this.argumentTypes = argumentTypes;
			this.returnType = returnType;
		}

		final java.util.List<Type> argumentTypes;
		final Type returnType;
	}

	static class Class extends Type {
		Class(Token identifier, boolean nullable) {
			super(nullable);
			this.identifier = identifier;
		}

		final Token identifier;
	}


	Type(boolean nullable) {
		this.nullable = nullable;
	}

	final boolean nullable;
}
