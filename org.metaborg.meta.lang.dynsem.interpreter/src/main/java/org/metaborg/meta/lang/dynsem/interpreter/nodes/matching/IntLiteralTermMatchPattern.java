package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public final class IntLiteralTermMatchPattern extends LiteralMatchPattern {

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