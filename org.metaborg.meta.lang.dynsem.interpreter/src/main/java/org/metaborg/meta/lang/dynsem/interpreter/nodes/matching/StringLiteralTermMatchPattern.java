package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public final class StringLiteralTermMatchPattern extends LiteralMatchPattern {

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