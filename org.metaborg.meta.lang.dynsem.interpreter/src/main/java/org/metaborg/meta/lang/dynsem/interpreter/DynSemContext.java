package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.language.ILanguageComponent;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ITermBuildFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermMatchPatternFactory;
import org.metaborg.spoofax.core.Spoofax;
import org.strategoxt.HybridInterpreter;

import com.google.common.collect.Iterables;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class DynSemContext {

	public static String DYNSEMPATH;

	public static DynSemLanguage LANGUAGE;

	private final InputStream input;
	private final PrintStream output;

	private final ITermRegistry termRegistry;
	private final RuleRegistry ruleRegistry;
	private final DynSemLanguageParser langParser;

	private DynSemPrimedRun primedRun;

	public DynSemContext(ITermRegistry termRegistry, RuleRegistry ruleRegistry, Path parseTable) {
		this(termRegistry, ruleRegistry, parseTable, System.in, System.out);
	}

	public DynSemContext(ITermRegistry termRegistry, RuleRegistry ruleRegistry, Path parseTable, InputStream input,
			PrintStream output) {
		this.termRegistry = termRegistry;
		this.ruleRegistry = ruleRegistry;
		this.input = input;
		this.output = output;
		this.langParser = new DynSemLanguageParser(parseTable);
	}

	private static HybridInterpreter dynsemStrategoRuntime;

	@TruffleBoundary
	public static HybridInterpreter getDynSemStrategoRuntime() {
		if (dynsemStrategoRuntime == null) {
			assert DYNSEMPATH != null;
			try {
				Spoofax spoofax = new Spoofax();
				FileObject dynsemResource = spoofax.resourceService.resolve(DYNSEMPATH);
				Iterable<ILanguageComponent> components = spoofax.discoverLanguages(dynsemResource);
				ILanguageComponent dynsem = Iterables.get(components, 0);

				dynsemStrategoRuntime = spoofax.strategoRuntimeService.runtime(dynsem,
						spoofax.resourceService.resolve(System.getProperty("user.dir")));
				dynsemStrategoRuntime.init();
			} catch (MetaborgException e) {
				throw new RuntimeException("Cannot initialize Spoofax", e);
			}
		}
		return dynsemStrategoRuntime;
	}

	public DynSemLanguageParser getParser() {
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
