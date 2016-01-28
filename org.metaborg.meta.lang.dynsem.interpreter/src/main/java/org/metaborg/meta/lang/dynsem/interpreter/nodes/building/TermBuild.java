package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.dynsem.interpreter.DynSemLanguage;
import org.spoofax.interpreter.terms.IStrategoTerm;

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

	public abstract IStrategoTerm execute(VirtualFrame frame);
}
