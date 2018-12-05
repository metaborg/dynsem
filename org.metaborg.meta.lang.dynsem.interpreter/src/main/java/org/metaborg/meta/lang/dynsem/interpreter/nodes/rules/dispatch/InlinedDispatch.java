package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.IRuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.MultiRule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.SingleRule;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class InlinedDispatch extends AbstractDispatch {

	public InlinedDispatch(SourceSection source, String arrowName) {
		super(source, arrowName);
	}

	@Specialization
	public RuleResult doCached(Object[] callArgs,
			@Cached("createInlineableRule(lookup(termClass(callArgs)))") InlinedRuleWrap inlinedCall) {
		return inlinedCall.evaluate(callArgs);
	}

	protected static Class<?> termClass(Object[] args) {
		return args[0].getClass();
	}

	protected CallTarget lookup(Class<?> termClass) {
		return getContext().getRuleRegistry().lookupCallTarget(arrowName, termClass);
	}

	public static InlinedDispatch create(SourceSection source, String arrowName) {
		return InlinedDispatchNodeGen.create(source, arrowName);
	}

	protected final InlinedRuleWrap createInlineableRule(CallTarget target) {
		CompilerAsserts.neverPartOfCompilation("May not inline rules from compiled code");
		DynSemContext ctx = getContext();
		IRuleRegistry rr = ctx.getRuleRegistry();
		RuleRootNode root = rr.lookupRuleRoot(target);
		FrameDescriptor fd = root.getFrameDescriptor();
		IStrategoAppl[] ruleTs = root.getSourceATerms();
		SingleRule[] rules = new SingleRule[ruleTs.length];
		for (int i = 0; i < rules.length; i++) {
			rules[i] = SingleRule.createFromATerm(rr.getLanguage(), ruleTs[i], fd, getContext().getTermRegistry());
		}
		Rule inlineableRule;
		if (rules.length == 1) {
			inlineableRule = rules[0];
		} else {
			inlineableRule = new MultiRule(rules[0].getSourceSection(), rules);
		}
		return new InlinedRuleWrap(fd, inlineableRule);
	}

	public static class InlinedRuleWrap extends Node {

		private final FrameDescriptor fd;
		@Child private Rule rule;

		public InlinedRuleWrap(FrameDescriptor fd, Rule rule) {
			this.fd = fd;
			this.rule = rule;
		}

		public RuleResult evaluate(Object[] callArgs) {
			return rule.evaluateRule(Truffle.getRuntime().createVirtualFrame(callArgs, fd));
		}

	}
}
