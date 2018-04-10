package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Rules extends Rule {

	@Child private DirectCallNode primaryCallNode;
	@Child private DirectCallNode alternativeCallNode;

	public Rules(DynSemLanguage lang, SourceSection source, CallTarget primaryCT, CallTarget alternativeCT) {
		super(lang, source);
		this.primaryCallNode = DirectCallNode.create(primaryCT);
		this.alternativeCallNode = DirectCallNode.create(alternativeCT);
		Truffle.getRuntime().createCallTarget(this);
	}

	private final BranchProfile alternativeTaken = BranchProfile.create();

	@Specialization
	public RuleResult executeFixed(VirtualFrame frame) {
		try {
			return (RuleResult) primaryCallNode.call(frame.getArguments());
		} catch (PatternMatchFailure pmfx) {
			alternativeTaken.enter();
			return (RuleResult) alternativeCallNode.call(frame.getArguments());
		}
	}

	@Override
	public boolean isCloningAllowed() {
		return true;
	}

	@Override
	protected boolean isCloneUninitializedSupported() {
		return false;
	}

	@Override
	protected Rule cloneUninitialized() {
		throw new UnsupportedOperationException();
	}

	//
	// public Rule makeUninitializedCloneWithoutFallback() {
	// CompilerAsserts.neverPartOfCompilation();
	// if (alternativeRule instanceof FallbackRule) {
	// return primaryRule.cloneUninitialized();
	// } else if (alternativeRule instanceof Rules) {
	// return RulesNodeGen.create(language(), getSourceSection(), primaryRule.cloneUninitialized(),
	// ((Rules) alternativeRule).makeUninitializedCloneWithoutFallback());
	// } else {
	// return cloneUninitialized();
	// }
	// }

	// public void replaceFallbackWith(Rule replacement) {
	// CompilerAsserts.neverPartOfCompilation();
	// if (alternativeRule instanceof FallbackRule) {
	// this.alternativeRule = replacement;
	// } else if (alternativeRule instanceof Rules) {
	// ((Rules) alternativeRule).replaceFallbackWith(replacement);
	// } else {
	// throw new IllegalStateException("Rules do not contain a fallback rule");
	// }
	// }

	@Override
	@TruffleBoundary
	public String toString() {
		return "(seq) --> " + primaryCallNode.getCallTarget();
	}
}
