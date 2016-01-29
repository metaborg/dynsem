package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class MatchPattern extends Node {

	private Node createContext;

	public MatchPattern(SourceSection source) {
		super(source);
		this.createContext = DynSemLanguage.INSTANCE.createFindContextNode0();
	}

	protected DynSemContext getContext() {
		return DynSemLanguage.INSTANCE.findContext0(createContext);
	}

	public abstract boolean execute(ITerm term, VirtualFrame frame);
}
