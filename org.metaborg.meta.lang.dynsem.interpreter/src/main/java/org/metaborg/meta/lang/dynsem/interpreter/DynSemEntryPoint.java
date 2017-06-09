package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITermTransformer;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.vm.PolyglotEngine;
import com.oracle.truffle.api.vm.PolyglotEngine.Builder;
import com.oracle.truffle.api.vm.PolyglotEngine.Value;

/**
 * Abstract class of an entrypoint to a {@link DynSemLanguage}. This class is responsible for instantiating the VM
 * properly and provides an interface for evaluating source code in that language.
 */
public abstract class DynSemEntryPoint {
	private final IDynSemLanguageParser parser;
	private final ITermTransformer transformer;
	private final RuleRegistry ruleRegistry;
	private final ITermRegistry termRegistry;

	public DynSemEntryPoint(IDynSemLanguageParser parser, ITermTransformer transformer, ITermRegistry termRegistry,
			RuleRegistry ruleRegistry) {
		this.parser = parser;
		this.transformer = transformer;
		this.termRegistry = termRegistry;
		this.ruleRegistry = ruleRegistry;
	}

	public Callable<RuleResult> getCallable(String file, String workingDirectory, InputStream input,
			OutputStream output, OutputStream error) {
		try {
			File f = new File(file);
			if (!f.isAbsolute() && !f.exists()) {
				f = new File(workingDirectory, file);
			}
			IStrategoTerm term = getTransformer().transform(getParser()
					.parse(Source.newBuilder(f).name("Evaluate to interpreter").mimeType(getMimeType()).build()));
			return getCallable(term, input, output, error);
		} catch (IOException ioex) {
			throw new RuntimeException("Eval failed", ioex);
		}
	}

	public Callable<RuleResult> getCallable(IStrategoTerm term, InputStream input, OutputStream output,
			OutputStream error) {
		return getCallable(term, input, output, error, new HashMap<>());
	}

	public Callable<RuleResult> getCallable(IStrategoTerm term, InputStream input, OutputStream output,
			OutputStream error, Map<String, Object> config) {
		try {

			PolyglotEngine vm = buildPolyglotEngine(input, output, error, config);
			assert vm.getLanguages().containsKey(getMimeType());
			ITerm programTerm = getTermRegistry().parseProgramTerm(term);

			Value interpreter = vm.eval(Source.newBuilder(new InputStreamReader(getSpecificationTerm()))
					.name("Interpreter specification").mimeType(getMimeType()).build());

			return new Callable<RuleResult>() {
				@Override
				public RuleResult call() throws Exception {
					DynSemRule rule = interpreter.as(DynSemRule.class);
					return rule.getRuleTarget().execute(Truffle.getRuntime()
							.createVirtualFrame(new Object[] { programTerm }, new FrameDescriptor()));
				}
			};
		} catch (IOException e) {
			throw new RuntimeException("Eval failed", e);
		}
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
	 * @param config
	 *            Additional key-value map for configuration of the VM
	 * @return The configured {@link PolyglotEngine}.
	 */
	public PolyglotEngine buildPolyglotEngine(InputStream input, OutputStream output, OutputStream error,
			Map<String, Object> config) {
		assert DynSemContext.LANGUAGE != null : "DynSemContext.LANGUAGE must be set before creating the RuleRegistry";
		final Builder builder = PolyglotEngine.newBuilder();

		for (Entry<String, Object> cfgEntry : config.entrySet()) {
			builder.config(getMimeType(), cfgEntry.getKey(), cfgEntry.getValue());
		}

		return builder.setIn(input).setOut(output).setErr(error)
				.config(getMimeType(), DynSemLanguage.PARSER, getParser())
				.config(getMimeType(), DynSemLanguage.TERM_REGISTRY, getTermRegistry())
				.config(getMimeType(), DynSemLanguage.RULE_REGISTRY, getRuleRegistry()).build();

	}

	public IDynSemLanguageParser getParser() {
		return parser;
	}

	public ITermTransformer getTransformer() {
		return transformer;
	}

	public ITermRegistry getTermRegistry() {
		return termRegistry;
	}

	public RuleRegistry getRuleRegistry() {
		return ruleRegistry;
	}

	public abstract String getMimeType();

	public abstract InputStream getSpecificationTerm();
}
