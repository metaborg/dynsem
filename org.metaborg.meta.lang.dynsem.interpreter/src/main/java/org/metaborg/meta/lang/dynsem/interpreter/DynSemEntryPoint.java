package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.vm.PolyglotEngine;
import com.oracle.truffle.api.vm.PolyglotEngine.Builder;
import com.oracle.truffle.api.vm.PolyglotEngine.Value;

/**
 * Abstract class of an entrypoint to a {@link DynSemLanguage}. This class is responsible for instantiating the VM
 * properly and provides an interface for evaluating source code in that language.
 */
public abstract class DynSemEntryPoint {

	public RuleResult evaluate(String file, InputStream input, OutputStream output, OutputStream error)
			throws Exception {
		return getCallable(file, input, output, error).call();
	}

	public Callable<RuleResult> getCallable(String file, InputStream input, OutputStream output, OutputStream error) {
		PolyglotEngine vm = buildPolyglotEngine(input, output, error);
		assert vm.getLanguages().containsKey(getMimeType());
		try {
			vm.eval(Source.fromFileName(file).withMimeType(getMimeType()));
		} catch (IOException ioex) {
			throw new RuntimeException("Eval failed", ioex);
		}
		Value prog = vm.findGlobalSymbol("INIT");
		return new Callable<RuleResult>() {
			@Override
			public RuleResult call() throws Exception {
				return prog.execute().as(RuleResult.class);
			}
		};
	}

	/**
	 * Build and configure the {@link PolyglotEngine}. Uses {@link Builder#config(String, String, Object)} for injecting
	 * dependencies: the {@link IDynSemLanguageParser parser} to be used, the {@link ITermRegistry term registry} and
	 * the {@link InputStream} of the DynSem specification term.
	 * 
	 * @param input
	 *            The {@link InputStream} of the VM.
	 * @param output
	 *            The {@link OutputStream} of the VM for standard output.
	 * @param error
	 *            The {@link OutputStream} of the VM for errors.
	 * @return The configured {@link PolyglotEngine}.
	 */
	public PolyglotEngine buildPolyglotEngine(InputStream input, OutputStream output, OutputStream error) {
		IDynSemLanguageParser parser = getParser();
		return PolyglotEngine.newBuilder().setIn(input).setOut(output).setErr(error)
				.config(getMimeType(), DynSemLanguage.PARSER, parser)
				.config(getMimeType(), DynSemLanguage.SPECIFICATION_TERM, getSpecificationTerm())
				.config(getMimeType(), DynSemLanguage.TERM_REGISTRY, getTermRegistry()).build();
	}

	protected abstract InputStream getSpecificationTerm();

	protected abstract String getMimeType();

	protected abstract IDynSemLanguageParser getParser();

	protected abstract ITermRegistry getTermRegistry();
}
