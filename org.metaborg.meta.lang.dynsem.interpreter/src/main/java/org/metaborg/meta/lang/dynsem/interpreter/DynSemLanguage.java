package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;

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

	public Node createFindContextNode0() {
		return createFindContextNode();
	}

	public DynSemContext findContext0(Node n) {
		return findContext(n);
	}

}