package org.metaborg.meta.lang.dynsem.interpreter.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DynSemRootNode extends RootNode {

	private final SourceSection sourceSection;

	protected DynSemRootNode(DynSemLanguage lang, SourceSection sourceSection, FrameDescriptor frameDescriptor) {
		super(lang, frameDescriptor);
		this.sourceSection = sourceSection;
	}

	protected DynSemRootNode(DynSemLanguage lang, SourceSection sourceSection) {
		this(lang, sourceSection, null);
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

	@Override
	protected abstract boolean isCloneUninitializedSupported();

	@Override
	protected abstract DynSemRootNode cloneUninitialized();

	public DynSemRootNode makeUninitializedClone() {
		return cloneUninitialized();
	}

}
