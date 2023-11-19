package com.light672.gears.lang;
public class Type {
	static class Any extends Type {
		Any(boolean nullable) {
			super(nullable);
		}

		@Override
		public java.lang.String toString() {
			return "ANY" + (this.nullable ? "?" : "");
		}
	}

	static class Object extends Type {
		Object(Token identifier, boolean nullable) {
			super(nullable);
			this.identifier = identifier;
		}

		final Token identifier;

		@Override
		public java.lang.String toString() {
			return "OBJECT" + (this.nullable ? "?" : "");
		}
	}

	static class Number extends Type {
		Number() {
			super(false);
		}

		@Override
		public java.lang.String toString() {
			return "NUMBER";
		}
	}

	static class Bool extends Type {
		Bool() {
			super(false);
		}

		@Override
		public java.lang.String toString() {
			return "BOOL";
		}
	}

	static class String extends Type {
		String(boolean nullable) {
			super(nullable);
		}

		@Override
		public java.lang.String toString() {
			return "STRING" + (this.nullable ? "?" : "");
		}
	}

	static class Range extends Type {
		Range(boolean nullable) {
			super(nullable);
		}

		@Override
		public java.lang.String toString() {
			return "RANGE" + (this.nullable ? "?" : "");
		}
	}

	static class List extends Type {
		List(Type entryType, boolean nullable) {
			super(nullable);
			this.entryType = entryType;
		}

		final Type entryType;

		@Override
		public java.lang.String toString() {
			return "LIST" + (this.nullable ? "?" : "") + "<" + this.entryType.toString() + ">";
		}
	}

	static class Map extends Type {
		Map(Type keyType, Type valueType, boolean nullable) {
			super(nullable);
			this.keyType = keyType;
			this.valueType = valueType;
		}

		final Type keyType;
		final Type valueType;

		@Override
		public java.lang.String toString() {
			return "MAP" + (this.nullable ? "?" : "") + "<" + this.keyType.toString() + ", " + this.valueType.toString() + ">";
		}
	}

	static class Function extends Type {

		Function(java.util.List<Type> argumentTypes, Type returnType, boolean nullable) {
			super(nullable);
			this.argumentTypes = argumentTypes;
			this.returnType = returnType;
		}

		final java.util.List<Type> argumentTypes;
		final Type returnType;

		@Override
		public java.lang.String toString() {
			StringBuilder arguments = new StringBuilder("<");
			for (Type t : this.argumentTypes) {
				arguments.append(t.toString()).append(", ");
			}
			return "FUNCTION" + (this.nullable ? "?" : "") + arguments.substring(0,
					arguments.length() - 2) + "> -> " + this.returnType.toString();
		}
	}

	static class Class extends Type {
		Class(Token identifier, boolean nullable) {
			super(nullable);
			this.identifier = identifier;
		}

		final Token identifier;

		@Override
		public java.lang.String toString() {
			return "CLASS " + this.identifier.lexeme;
		}
	}


	Type(boolean nullable) {
		this.nullable = nullable;
	}

	final boolean nullable;
}
