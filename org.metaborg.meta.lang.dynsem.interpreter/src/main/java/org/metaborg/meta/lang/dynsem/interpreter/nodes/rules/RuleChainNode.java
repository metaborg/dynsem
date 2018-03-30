package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

/**
 * An abstract class to aggregate multiple rules. Existence of multiple rules is typically the case with overloaded
 * rules. Instances (of descendants) of this node can be used to aggregate these overloaded rules.
 * 
 * 
 * @author vladvergu
 *
 */
public class RuleChainNode extends ChainedRulesNode {

	@Child private Rule rule;
	@Child private ChainedRulesNode otherRules;

	public RuleChainNode(SourceSection source, Rule r, ChainedRulesNode rs) {
		super(source);
		this.rule = r;
		this.otherRules = rs;
	}

	private final BranchProfile altProfile = BranchProfile.create();

	public RuleResult execute(Object[] arguments) {
		try {
			return executePrimary(arguments);
		} catch (PatternMatchFailure pmfx) {
			altProfile.enter();
			return otherRules.execute(arguments);
		}
	}

	private RuleResult executePrimary(Object[] arguments) {
		// TODO: somehow avoid recreating a separate frame for each attempted rule
		return rule.execute(Truffle.getRuntime().createVirtualFrame(arguments, rule.getFrameDescriptor()));
	}

	@Override
	public int ruleCount() {
		return 1 + otherRules.ruleCount();
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return rule.getDispatchClass().getSimpleName() + " -" + rule.getArrowName() + "->";
	}

}
