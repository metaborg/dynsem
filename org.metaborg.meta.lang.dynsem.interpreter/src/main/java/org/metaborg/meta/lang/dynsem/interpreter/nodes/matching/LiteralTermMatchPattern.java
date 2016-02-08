package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class LiteralTermMatchPattern extends MatchPattern {

	public LiteralTermMatchPattern(SourceSection source) {
		super(source);
	}

	public static final class StringLiteralTermMatchPattern extends
			LiteralTermMatchPattern {

		private final String lit;

		public StringLiteralTermMatchPattern(String lit, SourceSection source) {
			super(source);
			this.lit = lit;
		}

		@Override
		public boolean execute(Object term, VirtualFrame frame) {
			if (BuiltinTypesGen.isString(term)) {
				String s = BuiltinTypesGen.asString(term);
				return lit.equals(s);
			}
			return false;
		}

	}

	public static final class IntLiteralTermMatchPattern extends
			LiteralTermMatchPattern {

		private final int lit;

		public IntLiteralTermMatchPattern(int lit, SourceSection source) {
			super(source);
			this.lit = lit;
		}

		@Override
		public boolean execute(Object term, VirtualFrame frame) {
			if (BuiltinTypesGen.isInteger(term)) {
				int i = BuiltinTypesGen.asInteger(term);
				return lit == i;
			}
			return false;
		}

	}

	public static final class TrueLiteralTermMatchPattern extends
			LiteralTermMatchPattern {

		public TrueLiteralTermMatchPattern(SourceSection source) {
			super(source);
		}

		@Override
		public boolean execute(Object term, VirtualFrame frame) {
			if (BuiltinTypesGen.isBoolean(term)) {
				return BuiltinTypesGen.asBoolean(term);
			}
			return false;
		}
	}

	public static final class FalseLiteralTermMatchPattern extends
			LiteralTermMatchPattern {

		public FalseLiteralTermMatchPattern(SourceSection source) {
			super(source);
		}

		@Override
		public boolean execute(Object term, VirtualFrame frame) {
			if (BuiltinTypesGen.isBoolean(term)) {
				return !BuiltinTypesGen.asBoolean(term);
			}
			return false;
		}
	}

}
