package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class LiteralTermMatchPattern extends MatchPattern {

	public LiteralTermMatchPattern(SourceSection source) {
		super(source);
	}

	public static final class StringLiteralTermMatchPattern extends LiteralTermMatchPattern {

		private final String lit;

		public StringLiteralTermMatchPattern(String lit, SourceSection source) {
			super(source);
			this.lit = lit;
		}

		private final ConditionProfile conditionProfile = ConditionProfile.createBinaryProfile();

		@Override
		public boolean execute(Object term, VirtualFrame frame) {
			if (conditionProfile.profile(BuiltinTypesGen.isString(term))) {
				String s = BuiltinTypesGen.asString(term);
				return isStringEq(s);
			}
			return false;
		}

		@TruffleBoundary
		private boolean isStringEq(String s) {
			return lit.equals(s);
		}

	}

	public static final class IntLiteralTermMatchPattern extends LiteralTermMatchPattern {

		private final int lit;

		public IntLiteralTermMatchPattern(int lit, SourceSection source) {
			super(source);
			this.lit = lit;
		}

		private final ConditionProfile conditionProfile = ConditionProfile.createBinaryProfile();

		@Override
		public boolean execute(Object term, VirtualFrame frame) {
			if (conditionProfile.profile(BuiltinTypesGen.isInteger(term))) {
				int i = BuiltinTypesGen.asInteger(term);
				return lit == i;
			}
			return false;
		}

	}

	public static final class TrueLiteralTermMatchPattern extends LiteralTermMatchPattern {

		public TrueLiteralTermMatchPattern(SourceSection source) {
			super(source);
		}

		private final ConditionProfile conditionProfile = ConditionProfile.createBinaryProfile();

		@Override
		public boolean execute(Object term, VirtualFrame frame) {
			if (conditionProfile.profile(BuiltinTypesGen.isBoolean(term))) {
				return BuiltinTypesGen.asBoolean(term);
			}
			return false;
		}
	}

	public static final class FalseLiteralTermMatchPattern extends LiteralTermMatchPattern {

		public FalseLiteralTermMatchPattern(SourceSection source) {
			super(source);
		}

		private final ConditionProfile conditionProfile = ConditionProfile.createBinaryProfile();

		@Override
		public boolean execute(Object term, VirtualFrame frame) {
			if (conditionProfile.profile(BuiltinTypesGen.isBoolean(term))) {
				return !BuiltinTypesGen.asBoolean(term);
			}
			return false;
		}
	}

}
