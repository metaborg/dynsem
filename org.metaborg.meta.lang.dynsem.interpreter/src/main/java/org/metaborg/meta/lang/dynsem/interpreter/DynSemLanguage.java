package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.instrument.Visualizer;
import com.oracle.truffle.api.instrument.WrapperNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;

public abstract class DynSemLanguage extends TruffleLanguage<DynSemContext> {

	public DynSemLanguage() {
	}

	@Override
	protected DynSemContext createContext(Env env) {
		return createDynSemContext(env.in(), new PrintStream(env.out()));
	}

	public abstract DynSemContext createDynSemContext(InputStream input, PrintStream output);

	public Node createFindContextNode0() {
		return createFindContextNode();
	}

	public DynSemContext findContext0(Node n) {
		return findContext(n);
	}

	public DynSemContext getContext() {
		return findContext(createFindContextNode());
	}

	@Override
	protected WrapperNode createWrapperNode(Node node) {
		throw new NotImplementedException();
	}

	@Override
	protected Object evalInContext(Source source, Node node, MaterializedFrame mFrame) throws IOException {
		throw new IllegalStateException("evalInContext not supported");
	}

	@Override
	protected Object findExportedSymbol(DynSemContext context, String globalName, boolean onlyExplicit) {
		if (globalName.equals("INIT")) {
			return context.getRun();
		}
		return null;
	}

	@Override
	protected boolean isObjectOfLanguage(Object obj) {
		return obj instanceof DynSemPrimedRun;
	}

	@Override
	protected Object getLanguageGlobal(DynSemContext context) {
		return context;
	}

	@Override
	protected Visualizer getVisualizer() {
		return null;
	}

	@Override
	protected boolean isInstrumentable(Node node) {
		return false;
	}
}