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
			CompilerDirectives.transferToInterpreter();
			ctx = DynSemLanguage.getContext(getRootNode());
		}
		return ctx;
	}

	@CompilationFinal private NaBL2Context context;

	protected final NaBL2Context nabl2Context() {
		if (context == null) {
			CompilerDirectives.transferToInterpreter();
			context = (NaBL2Context) getContext().readProperty(NaBL2Context.class.getName(), null);
			if (context == null) {
				throw new IllegalStateException("No NaBL2 context available. "
						+ "Does the language use NaBL2, and was the interpreter invoked using the correct runner?");
			}
		}
		return context;
	}

}
