package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public final class FalseLiteralTermMatchPattern extends LiteralMatchPattern {

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