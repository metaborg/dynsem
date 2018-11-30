package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.graalvm.collections.EconomicMap;
import org.graalvm.collections.Equivalence;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
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

	private final EconomicMap<String, EconomicMap<Class<?>, CallTarget>> targets = EconomicMap.create();
	private final EconomicMap<String, EconomicMap<Class<?>, RuleRootNode>> roots = EconomicMap.create();
	private final EconomicMap<CallTarget, RuleRootNode> targets2roots = EconomicMap.create(Equivalence.IDENTITY);

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
	public void registerRule(String arrowName, Class<?> dispatchClass, RuleRootNode rootNode) {

		EconomicMap<Class<?>, RuleRootNode> rootsForArrow = roots.get(arrowName);
		EconomicMap<Class<?>, CallTarget> targetsForArrow = targets.get(arrowName);

		if (rootsForArrow == null) {
			rootsForArrow = EconomicMap.create(Equivalence.IDENTITY);
			targetsForArrow = EconomicMap.create(Equivalence.IDENTITY);
			roots.put(arrowName, rootsForArrow);
			targets.put(arrowName, targetsForArrow);
		}
		rootsForArrow.put(dispatchClass, rootNode);
		CallTarget callTarget = Truffle.getRuntime().createCallTarget(rootNode);
		targetsForArrow.put(dispatchClass, callTarget);
		targets2roots.put(callTarget, rootNode);
	}

	@Override
	public CallTarget lookupCallTarget(String arrowName, Class<?> dispatchClass) {
		if (!isInit) {
			init();
			isInit = true;
		}

		EconomicMap<Class<?>, CallTarget> rulesForName = targets.get(arrowName);

		return rulesForName != null ? rulesForName.get(dispatchClass) : null;
	}

	@Override
	public RuleRootNode lookupRuleRoot(String arrowName, Class<?> dispatchClass) {
		if (!isInit) {
			init();
			isInit = true;
		}

		EconomicMap<Class<?>, RuleRootNode> rootsForName = roots.get(arrowName);

		return rootsForName != null ? rootsForName.get(dispatchClass) : null;
	}

	@Override
	public RuleRootNode lookupRuleRoot(CallTarget target) {
		if (!isInit) {
			init();
			isInit = true;
		}
		return targets2roots.get(target);
	}

	@TruffleBoundary
	public void populate(InputStream specStream, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		try {
			TAFTermReader reader = new TAFTermReader(new TermFactory());

			IStrategoTerm topSpecTerm;
			topSpecTerm = reader.parseFromStream(specStream);
			specStream.close();

			IStrategoList rulesTerm = ruleListTerm(topSpecTerm);

			Map<String, Map<Class<?>, List<IStrategoAppl>>> groupedRules = new HashMap<>();

			for (IStrategoTerm term : rulesTerm) {
				IStrategoAppl ruleTerm = (IStrategoAppl) term;
				Class<?> dispatchClass = RuleRootNode.readDispatchClassFromATerm(ruleTerm);
				String arrowName = RuleRootNode.readArrowNameFromATerm(ruleTerm);

				Map<Class<?>, List<IStrategoAppl>> rulesForArr = groupedRules.get(arrowName);
				if (rulesForArr == null) {
					rulesForArr = new HashMap<>();
					groupedRules.put(arrowName, rulesForArr);
				}

				List<IStrategoAppl> rulesForClass = rulesForArr.get(dispatchClass);
				if (rulesForClass == null) {
					rulesForClass = new LinkedList<>();
					rulesForArr.put(dispatchClass, rulesForClass);
				}

				rulesForClass.add(ruleTerm);
			}

			for (Entry<String, Map<Class<?>, List<IStrategoAppl>>> rulesForNameEntry : groupedRules.entrySet()) {
				final String arrowName = rulesForNameEntry.getKey();
				for (Entry<Class<?>, List<IStrategoAppl>> rulesForClass : rulesForNameEntry.getValue().entrySet()) {
					Class<?> dispatchClass = rulesForClass.getKey();
					RuleRootNode root = RuleRootNode.createFromATerms(language,
							rulesForClass.getValue().toArray(new IStrategoAppl[0]), termReg);
					registerRule(arrowName, dispatchClass, root);
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
