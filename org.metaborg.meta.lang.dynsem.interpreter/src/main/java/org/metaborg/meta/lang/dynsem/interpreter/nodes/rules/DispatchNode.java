package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DispatchNode extends DynSemNode {

	protected static final int INLINE_CACHE_SIZE = 5;

	private final String arrowName;

	public DispatchNode(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
	}

	public abstract RuleResult execute(VirtualFrame frame, Class<?> dispatchClass, Object[] args);

	@Specialization(limit = "INLINE_CACHE_SIZE", guards = "dispatchClass == cachedDispatchClass")
	public RuleResult doDirect(VirtualFrame frame, Class<?> dispatchClass, Object[] args,
			@Cached("dispatchClass") Class<?> cachedDispatchClass,
			@Cached("create(getUnionRootNode(cachedDispatchClass).getCallTarget())") DirectCallNode callNode) {
		return (RuleResult) callNode.call(frame, args);
	}

	@Specialization(contains = "doDirect")
	public RuleResult doIndirect(Class<?> dispatchClass, Object[] args,
			@Cached("createVariableUnionNode()") PolymorphicUnionNode dispatchNode) {
		return dispatchNode.execute(args);
	}

	protected final JointRuleRoot getUnionRootNode(Class<?> dispatchClass) {
		return getContext().getRuleRegistry().lookupRules(arrowName, dispatchClass);
	}

	protected final PolymorphicUnionNode createVariableUnionNode() {
		return new PolymorphicUnionNode(getSourceSection(), arrowName);
	}

	public static DispatchNode create(IStrategoAppl source, IStrategoAppl arrow, FrameDescriptor fd) {
		assert Tools.hasConstructor(arrow, "NamedDynamicEmitted", 3);
		String arrowName = Tools.stringAt(arrow, 1).stringValue();

		assert Tools.hasConstructor(source, "Source", 2);
		return DispatchNodeGen.create(SourceSectionUtil.fromStrategoTerm(source), arrowName);
	}

}
