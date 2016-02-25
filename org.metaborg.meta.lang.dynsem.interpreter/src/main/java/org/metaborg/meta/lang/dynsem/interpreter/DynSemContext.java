package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ITermBuildFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermMatchPatternFactory;

public class DynSemContext {

	public static DynSemLanguage LANGUAGE;

	private final BufferedReader input;
	private final PrintWriter output;

	private final ITermRegistry termRegistry;
	private final RuleRegistry ruleRegistry;

	private DynSemPrimedRun primedRun;

	public DynSemContext(ITermRegistry termRegistry, RuleRegistry ruleRegistry) {
		this(termRegistry, ruleRegistry, new BufferedReader(
				new InputStreamReader(System.in)), new PrintWriter(System.out));
	}

	public DynSemContext(ITermRegistry termRegistry, RuleRegistry ruleRegistry,
			BufferedReader input, PrintWriter output) {
		this.termRegistry = termRegistry;
		this.ruleRegistry = ruleRegistry;
		this.input = input;
		this.output = output;
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
		ITermBuildFactory f = termRegistry.lookupNativeOpBuildFactory(name,
				arity);
		assert f != null;
		return f;
	}

	public ITermMatchPatternFactory lookupMatchPattern(String name, int arity) {
		ITermMatchPatternFactory f = termRegistry.lookupMatchFactory(name,
				arity);
		assert f != null;
		return f;
	}

	public ITermBuildFactory lookupNativeTypeAdapterBuildFactory(String sort,
			String function, int arity) {
		ITermBuildFactory f = termRegistry.lookupNativeTypeAdapterBuildFactory(
				sort, function, arity);
		assert f != null;
		return f;
	}

	public BufferedReader getInput() {
		return input;
	}

	public PrintWriter getOutput() {
		return output;
	}

}
