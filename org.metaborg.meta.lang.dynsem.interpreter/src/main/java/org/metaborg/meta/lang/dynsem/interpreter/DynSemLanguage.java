package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.instrument.Visualizer;
import com.oracle.truffle.api.instrument.WrapperNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;

public abstract class DynSemLanguage extends TruffleLanguage<DynSemContext> {

	private ITermRegistry termRegistry;
	private File specFile;

	public DynSemLanguage(ITermRegistry termRegistry, File specFile) {
		this.termRegistry = termRegistry;
		this.specFile = specFile;
		DynSemContext.LANGUAGE = this;
	}

	@Override
	protected DynSemContext createContext(Env env) {
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				env.in()));
		final PrintWriter out = new PrintWriter(env.out(), true);

		DynSemContext context = new DynSemContext(termRegistry,
				RuleRegistry.create(specFile), in, out);

		return context;
	}

	public ITermRegistry getTermRegistry() {
		return termRegistry;
	}

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
	protected Object evalInContext(Source source, Node node,
			MaterializedFrame mFrame) throws IOException {
		throw new IllegalStateException("evalInContext not supported");
	}

	@Override
	protected Object findExportedSymbol(DynSemContext context,
			String globalName, boolean onlyExplicit) {
		throw new NotImplementedException();
	}

	@Override
	protected boolean isObjectOfLanguage(Object object) {
		throw new NotImplementedException();
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