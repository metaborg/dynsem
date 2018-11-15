package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining.WrappedRuleNode.BoundaryRuleWrap;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public final class ConstantClassDispatchNode extends InliningDispatchNode {

	private final Class<?> dispatchClass;

	public ConstantClassDispatchNode(SourceSection source, Class<?> dispatchClass, String arrowName) {
		super(source, arrowName);
		this.dispatchClass = dispatchClass;
	}

	@Override
	protected Class<?> dispatchClass() {
		return dispatchClass;
	}

	@Override
	protected BoundaryRuleWrap createRuleForInlining(DynSemLanguage language, IStrategoAppl ruleSourceTerm,
			FrameDescriptor frameDescriptor) {
		RuleNode wrappableRule = RuleNode.create(language, ruleSourceTerm, frameDescriptor);
		return new BoundaryRuleWrap(wrappableRule.getSourceSection(), wrappableRule);
	}

	public static ConstantClassDispatchNode create(Class<?> dispatchClass, String arrowName) {
		return new ConstantClassDispatchNode(SourceUtils.dynsemSourceSectionUnvailable(), dispatchClass, arrowName);
	}

}
