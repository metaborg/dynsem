package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;

//@TruffleLanguage.Registration(name = "DynSem", version = "0.1", mimeType = "application/x-dynsem")
public abstract class DynSemLanguage extends TruffleLanguage<DynSemContext> {

	@CompilationFinal public static DynSemLanguage INSTANCE;

	private ITermRegistry termRegistry;
	private File specFile;

	public DynSemLanguage(ITermRegistry termRegistry, File specFile) {
		this.termRegistry = termRegistry;
		this.specFile = specFile;
		INSTANCE = this;
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


	@Override
	protected CallTarget parse(Source code, Node context,
			String... argumentNames) throws IOException {
		
		
		// TODO Auto-generated method stub
		return null;
	}

}