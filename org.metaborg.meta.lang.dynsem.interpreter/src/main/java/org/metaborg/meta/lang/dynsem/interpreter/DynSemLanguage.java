package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.IOException;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.instrument.Visualizer;
import com.oracle.truffle.api.instrument.WrapperNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;

public class DynSemLanguage extends TruffleLanguage<DynSemContext> {

	public static final DynSemLanguage INSTANCE = new DynSemLanguage();

	@Override
	protected DynSemContext createContext(
			com.oracle.truffle.api.TruffleLanguage.Env env) {
		// TODO Auto-generated method stub
		return null;
	}

	public Node createFindContextNode0() {
		return createFindContextNode();
	}

	public DynSemContext findContext0(Node n) {
		return findContext(n);
	}

	@Override
	protected CallTarget parse(Source code, Node context,
			String... argumentNames) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object findExportedSymbol(DynSemContext context,
			String globalName, boolean onlyExplicit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getLanguageGlobal(DynSemContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isObjectOfLanguage(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Visualizer getVisualizer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isInstrumentable(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected WrapperNode createWrapperNode(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object evalInContext(Source source, Node node,
			MaterializedFrame mFrame) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}