package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleUnionNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DispatchNode extends DynSemNode {

	protected static final int INLINE_CACHE_SIZE = 5;

	private final String arrowName;

	public DispatchNode(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
	}

	public abstract RuleResult execute(Class<?> dispatchClass, Object[] args);

	@Specialization(limit = "INLINE_CACHE_SIZE", guards = "dispatchClass == cachedDispatchClass")
	public RuleResult doDirect(Class<?> dispatchClass, Object[] args,
			@Cached("dispatchClass") Class<?> cachedDispatchClass,
			@Cached("getRuleUnionNode(cachedDispatchClass)") RuleUnionNode dispatchNode) {
		return dispatchNode.execute(args);
	}

	@Specialization(contains = "doDirect")
	public RuleResult doIndirect(Class<?> dispatchClass, Object[] args,
			@Cached("createVariableUnionNode()") VariableUnionNode dispatchNode) {
		return dispatchNode.execute(args);
	}

	protected final RuleUnionNode getRuleUnionNode(Class<?> dispatchClass) {
		return NodeUtil.cloneNode(getContext().getRuleRegistry().lookupRules(arrowName, dispatchClass).getUnionNode());
	}

	protected final VariableUnionNode createVariableUnionNode() {
		return VariableUnionNode.create(getSourceSection(), arrowName);
	}

	public static DispatchNode create(IStrategoAppl source, IStrategoAppl arrow, FrameDescriptor fd) {
		assert Tools.hasConstructor(arrow, "NamedDynamicEmitted", 2);
		String arrowName = Tools.stringAt(arrow, 1).stringValue();

		assert Tools.hasConstructor(source, "Source", 2);
		return DispatchNodeGen.create(SourceSectionUtil.fromStrategoTerm(source), arrowName);
	}

}
