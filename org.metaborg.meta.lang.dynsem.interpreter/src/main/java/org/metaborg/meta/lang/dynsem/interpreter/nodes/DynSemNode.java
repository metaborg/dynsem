package org.metaborg.meta.lang.dynsem.interpreter.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DynSemNode extends Node {
	private final Node contextNode;
	@CompilationFinal private DynSemContext cachedContext;
	private final SourceSection sourceSection;

	public DynSemNode(SourceSection source) {
		super();
		this.sourceSection = source;
		this.contextNode = DynSemContext.LANGUAGE.createFindContextNode0();
	}

	@Override
	public SourceSection getSourceSection() {
		return sourceSection;
	}

	protected DynSemContext getContext() {
		if (cachedContext == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			cachedContext = DynSemContext.LANGUAGE.findContext0(contextNode);
		}
		return cachedContext;
	}
}
