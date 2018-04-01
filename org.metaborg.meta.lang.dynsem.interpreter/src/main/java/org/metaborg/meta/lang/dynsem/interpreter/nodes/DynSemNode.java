package org.metaborg.meta.lang.dynsem.interpreter.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DynSemNode extends Node {
	private final SourceSection sourceSection;


	public DynSemNode(SourceSection source) {
		super();
		this.sourceSection = source;
	}

	@Override
	public SourceSection getSourceSection() {
		return sourceSection;
	}

	@CompilationFinal DynSemContext ctx;

	protected DynSemContext getContext() {
		if (ctx == null) {
			ctx = DynSemLanguage.getContext(getRootNode());
		}
		return ctx;
	}
	
}
