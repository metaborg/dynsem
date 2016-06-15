package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public abstract class LiteralMatchPattern extends MatchPattern {

	public LiteralMatchPattern(SourceSection source) {
		super(source);
	}

	public static LiteralMatchPattern create(IStrategoAppl t, FrameDescriptor fd) {
		SourceSection source = SourceSectionUtil.fromStrategoTerm(t);
		if (Tools.hasConstructor(t, "True", 0)) {
			return TrueLiteralTermMatchPatternNodeGen.create(source);
		}
		if (Tools.hasConstructor(t, "False", 0)) {
			return FalseLiteralTermMatchPatternNodeGen.create(source);
		}
		if (Tools.hasConstructor(t, "Int", 1)) {

			return IntLiteralTermMatchPatternNodeGen.create(Integer.parseInt(Tools.stringAt(t, 0).stringValue()),
					source);
		}
		if (Tools.hasConstructor(t, "String", 1)) {

			return StringLiteralTermMatchPatternNodeGen.create(Tools.stringAt(t, 0).stringValue(), source);
		}

		throw new NotImplementedException("Unsupported literal: " + t);
	}
}
