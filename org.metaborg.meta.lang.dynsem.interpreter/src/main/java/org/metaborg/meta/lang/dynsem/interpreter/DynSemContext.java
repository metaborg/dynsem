package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.InputStream;
import java.io.PrintStream;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ITermBuildFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermMatchPatternFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRegistry;

public class DynSemContext {
	public static DynSemLanguage LANGUAGE;

	private final InputStream input;
	private final PrintStream output;

	private final ITermRegistry termRegistry;
	private final RuleRegistry ruleRegistry;
	private final IDynSemLanguageParser langParser;

	private DynSemPrimedRun primedRun;

	public DynSemContext(ITermRegistry termRegistry, RuleRegistry ruleRegistry, IDynSemLanguageParser langParser) {
		this(termRegistry, ruleRegistry, langParser, System.in, System.out);
	}

	public DynSemContext(ITermRegistry termRegistry, RuleRegistry ruleRegistry, IDynSemLanguageParser langParser,
			InputStream input, PrintStream output) {
		this.termRegistry = termRegistry;
		this.ruleRegistry = ruleRegistry;
		this.input = input;
		this.output = output;
		this.langParser = langParser;
	}

	public IDynSemLanguageParser getParser() {
		return langParser;
	}

	public RuleRegistry getRuleRegistry() {
		return ruleRegistry;
	}

	public ITermRegistry getTermRegistry() {
		return termRegistry;
	}

	public DynSemPrimedRun getRun() {
		return primedRun;
	}

	public void setRun(DynSemPrimedRun run) {
		this.primedRun = run;
	}

	public ITermBuildFactory lookupTermBuilder(String name, int arity) {
		ITermBuildFactory f = termRegistry.lookupBuildFactory(name, arity);
		assert f != null;
		return f;
	}

	public ITermBuildFactory lookupNativeOpBuilder(String name, int arity) {
		ITermBuildFactory f = termRegistry.lookupNativeOpBuildFactory(name, arity);
		assert f != null;
		return f;
	}

	public ITermMatchPatternFactory lookupMatchPattern(String name, int arity) {
		ITermMatchPatternFactory f = termRegistry.lookupMatchFactory(name, arity);
		assert f != null;
		return f;
	}

	public ITermBuildFactory lookupNativeTypeAdapterBuildFactory(String sort, String function, int arity) {
		ITermBuildFactory f = termRegistry.lookupNativeTypeAdapterBuildFactory(sort, function, arity);
		assert f != null;
		return f;
	}

	public InputStream getInput() {
		return input;
	}

	public PrintStream getOutput() {
		return output;
	}

}
