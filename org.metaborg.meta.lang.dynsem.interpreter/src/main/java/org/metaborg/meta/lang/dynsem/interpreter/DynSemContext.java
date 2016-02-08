package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ITermBuildFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermMatchPatternFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;

public class DynSemContext {

	public static DynSemLanguage LANGUAGE;

	private final BufferedReader input;
	private final PrintWriter output;

	private final ITermRegistry termRegistry;
	private final IRuleRegistry ruleRegistry;

	public DynSemContext(ITermRegistry termRegistry, IRuleRegistry ruleRegistry) {
		this(termRegistry, ruleRegistry, new BufferedReader(
				new InputStreamReader(System.in)), new PrintWriter(System.out));
	}

	public DynSemContext(ITermRegistry termRegistry,
			IRuleRegistry ruleRegistry, BufferedReader input, PrintWriter output) {
		this.termRegistry = termRegistry;
		this.ruleRegistry = ruleRegistry;
		this.input = input;
		this.output = output;
	}

	public Rule lookupRule(String name, int arity) {
		return ruleRegistry.lookupRule(name, arity);
	}

	public ITermBuildFactory lookupTermBuilder(String name, int arity) {
		return termRegistry.lookupBuildFactory(name, arity);
	}

	public ITermBuildFactory lookupNativeOpBuilder(String name, int arity) {
		return termRegistry.lookupNativeOpBuildFactory(name, arity);
	}

	public ITermMatchPatternFactory lookupMatchPattern(String name, int arity) {
		return termRegistry.lookupMatchFactory(name, arity);
	}

	public BufferedReader getInput() {
		return input;
	}

	public PrintWriter getOutput() {
		return output;
	}
}
