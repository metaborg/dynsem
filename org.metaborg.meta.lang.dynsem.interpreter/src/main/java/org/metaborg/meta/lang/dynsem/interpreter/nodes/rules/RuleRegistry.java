package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.TAFTermReader;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;

public class RuleRegistry implements IRuleRegistry {

	private final Map<String, Map<Class<?>, CallTarget[]>> targets = new HashMap<>();
	private final Map<String, Map<Class<?>, RuleRootNode[]>> roots = new HashMap<>();

	@CompilationFinal private boolean isInit;

	@CompilationFinal private DynSemLanguage language;

	protected void init() {
	}

	@Override
	public void setLanguage(DynSemLanguage language) {
		this.language = language;
	}

	@Override
	public DynSemLanguage getLanguage() {
		return language;
	}

	@Override
	@TruffleBoundary
	public void registerRule(String arrowName, Class<?> dispatchClass, RuleRootNode[] newRuleRoots) {
		CompilerAsserts.neverPartOfCompilation();
		Map<Class<?>, RuleRootNode[]> rootsForName = roots.get(arrowName);
		Map<Class<?>, CallTarget[]> targetsForName = targets.get(arrowName);

		if (rootsForName == null) {
			rootsForName = new HashMap<>();
			targetsForName = new HashMap<>();
			roots.put(arrowName, rootsForName);
			targets.put(arrowName, targetsForName);
		}
		rootsForName.put(dispatchClass, newRuleRoots);
		CallTarget[] newTargets = new CallTarget[newRuleRoots.length];
		for (int i = 0; i < newTargets.length; i++) {
			newTargets[i] = Truffle.getRuntime().createCallTarget(newRuleRoots[i]);
		}
		targetsForName.put(dispatchClass, newTargets);
	}

	@Override
	@TruffleBoundary
	public CallTarget[] lookupCallTargets(String arrowName, Class<?> dispatchClass) {
		if (!isInit) {
			init();
			isInit = true;
		}

		Map<Class<?>, CallTarget[]> rulesForName = targets.get(arrowName);

		if (rulesForName == null) {
			return new CallTarget[0];
		}

		CallTarget[] rules = rulesForName.get(dispatchClass);

		return rules != null ? rules : new CallTarget[0];
	}

	@Override
	@TruffleBoundary
	public RuleRootNode[] lookupRuleRoots(String arrowName, Class<?> dispatchClass) {
		if (!isInit) {
			init();
			isInit = true;
		}

		Map<Class<?>, RuleRootNode[]> rootsForName = roots.get(arrowName);

		if (rootsForName == null) {
			return new RuleRootNode[0];
		}

		RuleRootNode[] rules = rootsForName.get(dispatchClass);

		return rules != null ? rules : new RuleRootNode[0];
	}

	@TruffleBoundary
	public void populate(InputStream specStream) {
		CompilerAsserts.neverPartOfCompilation();
		try {
			TAFTermReader reader = new TAFTermReader(new TermFactory());

			IStrategoTerm topSpecTerm;
			topSpecTerm = reader.parseFromStream(specStream);
			specStream.close();

			IStrategoList rulesTerm = ruleListTerm(topSpecTerm);

			Map<String, Map<Class<?>, List<RuleRootNode>>> rules = new HashMap<>();

			for (IStrategoTerm ruleTerm : rulesTerm) {
				RuleRootNode r = RuleRootNode.create(language, (IStrategoAppl) ruleTerm);

				Map<Class<?>, List<RuleRootNode>> rulesForName = rules.get(r.getArrowName());
				if (rulesForName == null) {
					rulesForName = new HashMap<>();
					rules.put(r.getArrowName(), rulesForName);
				}

				List<RuleRootNode> rulesForClass = rulesForName.get(r.getDispatchClass());

				if (rulesForClass == null) {
					rulesForClass = new LinkedList<>();
					rulesForName.put(r.getDispatchClass(), rulesForClass);
				}

				rulesForClass.add(r);
			}

			for (Entry<String, Map<Class<?>, List<RuleRootNode>>> rulesForNameEntry : rules.entrySet()) {
				final String arrowName = rulesForNameEntry.getKey();
				for (Entry<Class<?>, List<RuleRootNode>> rulesForClass : rulesForNameEntry.getValue().entrySet()) {
					Class<?> dispatchClass = rulesForClass.getKey();
					registerRule(arrowName, dispatchClass, rulesForClass.getValue().toArray(new RuleRootNode[0]));
					// registerRule(arrowName, dispatchClass,
					// RuleFactory.createRuleTargets(language, SourceUtils.dynsemSourceSectionUnvailable(),
					// rulesForClass.getValue(), arrowName, dispatchClass));
				}
			}

		} catch (IOException ioex) {
			throw new RuntimeException("Could not load specification ATerm", ioex);
		}
	}

	private static IStrategoList ruleListTerm(IStrategoTerm topSpecTerm) {
		IStrategoList sections = Tools.listAt(topSpecTerm, 1);
		for (IStrategoTerm section : sections) {
			if (Tools.isTermAppl(section) && Tools.hasConstructor((IStrategoAppl) section, "Rules", 1)) {
				return Tools.listAt(section, 0);
			}
		}
		throw new InterpreterException("Malformed specification: could not find Rules section");
	}

}
