package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class TermBuild extends Node {

	private Node createContext;

	public TermBuild(SourceSection source) {
		super(source);
		this.createContext = DynSemLanguage.INSTANCE.createFindContextNode0();
	}
	
	protected DynSemContext getContext() {
		return DynSemLanguage.INSTANCE.findContext0(createContext);
	}

	public abstract ITerm execute(VirtualFrame frame);
}
