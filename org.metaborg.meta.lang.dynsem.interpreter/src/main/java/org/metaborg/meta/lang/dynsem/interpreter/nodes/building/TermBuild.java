package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import metaborg.meta.lang.dynsem.interpreter.terms.IConTerm;
import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
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

	public abstract Object executeGeneric(VirtualFrame frame);

	public String executeString(VirtualFrame frame)
			throws UnexpectedResultException {
		return BuiltinTypesGen.expectString(executeGeneric(frame));
	}

	public int executeInteger(VirtualFrame frame)
			throws UnexpectedResultException {
		return BuiltinTypesGen.expectInteger(executeGeneric(frame));
	}

	public ITerm executeITerm(VirtualFrame frame)
			throws UnexpectedResultException {
		return BuiltinTypesGen.expectITerm(executeGeneric(frame));
	}

	public IConTerm executeIConTerm(VirtualFrame frame)
			throws UnexpectedResultException {
		return BuiltinTypesGen.expectIConTerm(executeGeneric(frame));
	}

}