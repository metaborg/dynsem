package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DispatchNode extends DynSemNode {

	protected final String arrowName;

	public DispatchNode(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
	}

	public abstract RuleResult execute(Class<?> dispatchClass, Object[] args);

	public static DispatchNode create(IStrategoAppl source, IStrategoAppl arrow, FrameDescriptor fd) {
		assert Tools.hasConstructor(arrow, "NamedDynamicEmitted", 3);
		String arrowName = Tools.stringAt(arrow, 1).stringValue();

		assert Tools.hasConstructor(source, "Source", 2);
		return PrimaryCachingDispatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(source), arrowName);
	}

	public static DispatchNode create(SourceSection source, String arrowName) {
		return PrimaryCachingDispatchNodeGen.create(source, arrowName);
	}
}
