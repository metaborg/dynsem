package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DispatchNode extends DynSemNode {

	private final String arrowName;

	public DispatchNode(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
	}

	public abstract RuleResult execute(Class<?> dispatchClass, Object[] args);

	@Specialization(limit = "4", guards = "dispatchClass == cachedDispatchClass")
	public RuleResult doDirect(Class<?> dispatchClass, Object[] args,
			@Cached("dispatchClass") Class<?> cachedDispatchClass,
			@Cached("create(getUnionRootNode(cachedDispatchClass).getCallTarget())") DirectCallNode callNode) {
		return (RuleResult) callNode.call(args);
	}

	@Specialization(replaces = "doDirect")
	public RuleResult doIndirect(Class<?> dispatchClass, Object[] args, @Cached("create()") IndirectCallNode callNode) {
		// printmiss(dispatchClass);
		return (RuleResult) callNode.call(getUnionRootNode(dispatchClass).getCallTarget(), args);
	}

	@TruffleBoundary
	private void printmiss(Class<?> dispatchClass) {
		System.out.println("Cache miss dispatching on " + dispatchClass.getSimpleName() + " from " + getRootNode());
	}
	protected final Rule getUnionRootNode(Class<?> dispatchClass) {
		return getContext().getRuleRegistry().lookupRule(arrowName, dispatchClass);
	}

	public static DispatchNode create(IStrategoAppl source, IStrategoAppl arrow, FrameDescriptor fd) {
		assert Tools.hasConstructor(arrow, "NamedDynamicEmitted", 3);
		String arrowName = Tools.stringAt(arrow, 1).stringValue();

		assert Tools.hasConstructor(source, "Source", 2);
		return DispatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(source), arrowName);
	}

}
