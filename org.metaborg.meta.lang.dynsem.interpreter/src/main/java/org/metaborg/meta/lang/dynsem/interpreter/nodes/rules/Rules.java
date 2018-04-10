package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Rules extends Rule {

	protected final Rule primaryRule;
	protected final Rule alternativeRule;

	public Rules(DynSemLanguage lang, SourceSection source, Rule primaryRule, Rule alternativeRule) {
		super(lang, source);
		this.primaryRule = primaryRule;
		this.alternativeRule = alternativeRule;
		Truffle.getRuntime().createCallTarget(this);
	}

	private final BranchProfile alternativeTaken = BranchProfile.create();

	@Specialization
	public RuleResult executeCached(VirtualFrame frame,
			@Cached("create(primaryRule.getCallTarget())") DirectCallNode primaryCallNode,
			@Cached("create(alternativeRule.getCallTarget())") DirectCallNode alternativeCallNode) {

		try {
			// TODO: 0.33 use CompilerDirectives.castExact introduced in Truffle 0.33
			return (RuleResult) primaryCallNode.call(frame.getArguments());
		} catch (PatternMatchFailure pmfx) {
			alternativeTaken.enter();
			// TODO: 0.33 use CompilerDirectives.castExact introduced in Truffle 0.33
			return (RuleResult) alternativeCallNode.call(frame.getArguments());
		}
	}

	@Override
	protected Rule cloneUninitialized() {
		return RulesNodeGen.create(language(), getSourceSection(), primaryRule.cloneUninitialized(),
				alternativeRule.cloneUninitialized());
	}

	public Rule makeUninitializedCloneWithoutFallback() {
		CompilerAsserts.neverPartOfCompilation();
		if (alternativeRule instanceof FallbackRule) {
			return primaryRule.cloneUninitialized();
		} else if (alternativeRule instanceof Rules) {
			return RulesNodeGen.create(language(), getSourceSection(), primaryRule.cloneUninitialized(),
					((Rules) alternativeRule).makeUninitializedCloneWithoutFallback());
		} else {
			return cloneUninitialized();
		}
	}

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

	public int count() {
		CompilerAsserts.neverPartOfCompilation();
		if (alternativeRule instanceof Rules) {
			return 1 + ((Rules) alternativeRule).count();
		} else {
			return 2;
		}
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return primaryRule.toString() + " " + count() + " seq";
	}
}
