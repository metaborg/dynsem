package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Rule extends DynSemRootNode {

	private final DynSemLanguage lang;

	public Rule(DynSemLanguage lang, SourceSection source, FrameDescriptor fd) {
		super(lang, source, fd);
		this.lang = lang;
	}

	public Rule(DynSemLanguage lang, SourceSection source) {
		this(lang, source, null);
	}

	protected DynSemLanguage language() {
		return lang;
	}

	@Override
	public abstract RuleResult execute(VirtualFrame frame);

	@Override
	public boolean isCloningAllowed() {
		return true;
	}

	@Override
	protected boolean isCloneUninitializedSupported() {
		return true;
	}

	@Override
	protected abstract Rule cloneUninitialized();

	public Rule makeUninitializedClone() {
		return cloneUninitialized();
	}
}
