package org.metaborg.meta.lang.dynsem.interpreter.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DynSemRootNode extends RootNode {

	private final SourceSection sourceSection;
	private final Assumption constantTermAssumption;

	protected DynSemRootNode(DynSemLanguage lang, SourceSection sourceSection, FrameDescriptor frameDescriptor,
			Assumption constantTermAssumption) {
		super(lang, frameDescriptor);
		this.sourceSection = sourceSection;
		this.constantTermAssumption = constantTermAssumption;
	}

	public Assumption getConstantTermAssumption() {
		return constantTermAssumption;
	}

	public DynSemContext getContext() {
		return DynSemLanguage.getContext(this);
	}

	@Override
	public SourceSection getSourceSection() {
		return sourceSection;
	}

	@Override
	public abstract boolean isCloningAllowed();

}
