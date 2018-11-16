package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public final class ConstantTermDispatchNode extends InliningDispatchNode {

	private final Class<?> dispatchClass;

	public ConstantTermDispatchNode(SourceSection source, Class<?> dispatchClass, String arrowName) {
		super(source, arrowName);
		this.dispatchClass = dispatchClass;
	}

	@Override
	protected Class<?> dispatchClass() {
		return dispatchClass;
	}

	@Override
	protected RuleNode createRuleForInlining(DynSemLanguage language, IStrategoAppl ruleSourceTerm,
			FrameDescriptor frameDescriptor, ITermRegistry termReg) {
		RuleNode wrappableRule = RuleNode.create(language, ruleSourceTerm, frameDescriptor, termReg);
		// return new PassthroughWrappedRule(wrappableRule.getSourceSection(), wrappableRule);
		return wrappableRule;
	}

	public static ConstantTermDispatchNode create(Class<?> dispatchClass, String arrowName) {
		return new ConstantTermDispatchNode(SourceUtils.dynsemSourceSectionUnvailable(), dispatchClass, arrowName);
	}

}
