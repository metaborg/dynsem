package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ApplTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public final class HandlerBuild extends DynSemNode {

	private static final String HANDLER_CTOR_NAME = "handler";
	private static final int HANDLER_CTOR_ARITY = 2;
	private static final String HANDLER_SORT_NAME = "SimpleSort(handler_2_Meta)";

	public HandlerBuild(SourceSection source) {
		super(source);
	}

	public Object execute(VirtualFrame frame, Object thrown, Object catching) {
		return new ApplTerm(HANDLER_SORT_NAME, HANDLER_CTOR_NAME, new Object[] { thrown, catching });
	}

}
