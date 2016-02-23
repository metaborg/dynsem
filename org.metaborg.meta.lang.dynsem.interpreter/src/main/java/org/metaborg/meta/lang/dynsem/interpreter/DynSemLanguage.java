package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.instrument.Visualizer;
import com.oracle.truffle.api.instrument.WrapperNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.vm.PolyglotEngine.Value;

public abstract class DynSemLanguage extends TruffleLanguage<DynSemContext> {

	private final ITermRegistry termRegistry;
	private final RuleRegistry ruleRegistry;
	private final DynSemLanguageParser parser;

	public DynSemLanguage(ITermRegistry termRegistry,
			RuleRegistry ruleRegistry, Path parseTablePath) {
		this.termRegistry = termRegistry;
		this.ruleRegistry = ruleRegistry;
		this.parser = new DynSemLanguageParser(parseTablePath);
		DynSemContext.LANGUAGE = this;
	}

	public Callable<Value> getCallable(String filePath, InputStream stdin,
			OutputStream stdout, OutputStream stderr) {
		// TODO implement
		throw new RuntimeException("Not implemented");
	}

	@Override
	protected DynSemContext createContext(Env env) {
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				env.in()));
		final PrintWriter out = new PrintWriter(env.out(), true);

		DynSemContext context = new DynSemContext(termRegistry, ruleRegistry,
				in, out);

		return context;
	}

	public ITermRegistry getTermRegistry() {
		return termRegistry;
	}

	public RuleRegistry getRuleRegistry() {
		return ruleRegistry;
	}

	public DynSemLanguageParser getParser() {
		return parser;
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
		if (globalName.equals("INIT")) {
			return context.getRun();
		}
		return null;
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