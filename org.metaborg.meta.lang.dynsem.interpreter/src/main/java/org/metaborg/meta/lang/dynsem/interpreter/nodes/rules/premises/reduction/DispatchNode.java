package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
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
			@Cached("createDirectDispatchNode(cachedDispatchClass)") StableDispatchNode dispatchNode) {

		return dispatchNode.execute(frame, args);
	}

	@Specialization(contains = "doDirect")
	public RuleResult doIndirect(VirtualFrame frame, Class<?> dispatchClass, Object[] args,
			@Cached("createUnstableDispatchNode()") UnstableDispatchNode dispatchNode) {
		return dispatchNode.execute(frame, dispatchClass, args);
	}

	protected final StableDispatchNode createDirectDispatchNode(Class<?> dispatchClass) {
		return new StableDispatchNode(getSourceSection(), arrowName,
				getContext().getRuleRegistry().lookupRules(arrowName, dispatchClass));
	}

	protected final UnstableDispatchNode createUnstableDispatchNode() {
		return new UnstableDispatchNode(getSourceSection(), arrowName);
	}

	public static DispatchNode create(IStrategoAppl source, IStrategoAppl arrow, FrameDescriptor fd) {

		assert Tools.hasConstructor(arrow, "NamedDynamicEmitted", 2);
		String arrowName = Tools.stringAt(arrow, 1).stringValue();

		assert Tools.hasConstructor(source, "Source", 2);
		IStrategoAppl lhsT = Tools.applAt(source, 0);
		IStrategoConstructor lhsC = lhsT.getConstructor();
		return DispatchNodeGen.create(SourceSectionUtil.fromStrategoTerm(source), arrowName);
	}

}
