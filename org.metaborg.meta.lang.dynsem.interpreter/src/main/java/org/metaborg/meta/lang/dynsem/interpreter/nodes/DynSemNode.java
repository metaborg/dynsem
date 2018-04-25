package org.metaborg.meta.lang.dynsem.interpreter.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2Context;

import com.oracle.truffle.api.CompilerDirectives;
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

	protected final DynSemContext getContext() {
		if (ctx == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			ctx = DynSemLanguage.getContext(getRootNode());
		}
		return ctx;
	}

	@CompilationFinal private NaBL2Context context;

	protected final NaBL2Context nabl2Context() {
		if (context == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			context = (NaBL2Context) getContext().readProperty(NaBL2Context.class.getName(), null);
		}
		return context;
	}

}
