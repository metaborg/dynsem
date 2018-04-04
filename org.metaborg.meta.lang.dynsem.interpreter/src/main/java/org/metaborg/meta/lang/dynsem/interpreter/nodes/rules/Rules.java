package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.LinkedList;
import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public class Rules extends Rule {

	private final Rule primaryRule;
	private final Rule alternativeRule;

	@Child private DirectCallNode primaryCallNode;
	@Child private DirectCallNode alternativeCallNode;

	public Rules(DynSemLanguage lang, SourceSection source, Rule primaryRule, Rule alternativeRule) {
		super(lang, source);
		this.primaryRule = primaryRule;
		this.alternativeRule = alternativeRule;
		this.primaryCallNode = DirectCallNode.create(primaryRule.getCallTarget());
		this.alternativeCallNode = DirectCallNode.create(alternativeRule.getCallTarget());
		Truffle.getRuntime().createCallTarget(this);
	}

	private final BranchProfile alternativeTaken = BranchProfile.create();

	@Override
	public RuleResult execute(VirtualFrame frame) {
		try {
			// TODO: use CompilerDirectives.castExact introduced in Truffle 0.33
			return (RuleResult) primaryCallNode.call(frame.getArguments());
		} catch (PatternMatchFailure pmfx) {
			alternativeTaken.enter();
			// TODO: use CompilerDirectives.castExact introduced in Truffle 0.33
			return (RuleResult) alternativeCallNode.call(frame.getArguments());
		}
	}

	public Rule getPrimaryRule() {
		return primaryRule;
	}

	public Rule getAlternative() {
		return alternativeRule;
	}

	public List<Rule> getRules() {
		CompilerAsserts.neverPartOfCompilation();
		List<Rule> rules = new LinkedList<>();
		rules.add(primaryRule);
		if (alternativeRule instanceof Rules) {
			rules.addAll(((Rules) alternativeRule).getRules());
		} else {
			rules.add(alternativeRule);
		}
		return rules;
	}

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
