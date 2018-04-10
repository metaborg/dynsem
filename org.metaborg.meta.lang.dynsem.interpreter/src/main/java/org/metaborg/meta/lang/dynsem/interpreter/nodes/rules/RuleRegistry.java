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
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
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

public class RuleRegistry {

	private final Map<String, Map<Class<?>, CallTarget>> rules = new HashMap<>();

	@CompilationFinal private boolean isInit;

	@CompilationFinal private DynSemLanguage language;

	protected void init() {
	}

	public void setLanguage(DynSemLanguage language) {
		this.language = language;
	}

	public DynSemLanguage getLanguage() {
		return language;
	}

	public void registerRule(String arrowName, Class<?> dispatchClass, CallTarget jointRuleRoot) {
		CompilerAsserts.neverPartOfCompilation();
		Map<Class<?>, CallTarget> rulesForName = rules.get(arrowName);

		if (rulesForName == null) {
			rulesForName = new HashMap<>();
			rules.put(arrowName, rulesForName);
		}
		rulesForName.put(dispatchClass, jointRuleRoot);
	}

	// FIXME: should this be a boundary? @TruffleBoundary
	public CallTarget lookupRule(String arrowName, Class<?> dispatchClass) {
		if (!isInit) {
			init();
			isInit = true;
		}
		CallTarget jointRuleForClass = null;

		Map<Class<?>, CallTarget> jointRulesForName = rules.get(arrowName);

		if (jointRulesForName != null) {
			jointRuleForClass = jointRulesForName.get(dispatchClass);
		}

		if (jointRuleForClass == null) {
			jointRuleForClass = new FallbackRule(language, SourceUtils.dynsemSourceSectionUnvailable(), arrowName,
					dispatchClass).getCallTarget();
			registerRule(arrowName, dispatchClass, jointRuleForClass);
		}
		return jointRuleForClass;
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

			Map<String, Map<Class<?>, List<ReductionRule>>> rules = new HashMap<>();

			for (IStrategoTerm ruleTerm : rulesTerm) {
				ReductionRule r = ReductionRule.create(language, (IStrategoAppl) ruleTerm);

				Map<Class<?>, List<ReductionRule>> rulesForName = rules.get(r.getArrowName());
				if (rulesForName == null) {
					rulesForName = new HashMap<>();
					rules.put(r.getArrowName(), rulesForName);
				}

				List<ReductionRule> rulesForClass = rulesForName.get(r.getDispatchClass());

				if (rulesForClass == null) {
					rulesForClass = new LinkedList<>();
					rulesForName.put(r.getDispatchClass(), rulesForClass);
				}

				rulesForClass.add(r);
			}

			for (Entry<String, Map<Class<?>, List<ReductionRule>>> rulesForNameEntry : rules.entrySet()) {
				final String arrowName = rulesForNameEntry.getKey();
				for (Entry<Class<?>, List<ReductionRule>> rulesForClass : rulesForNameEntry.getValue().entrySet()) {
					Class<?> dispatchClass = rulesForClass.getKey();
					registerRule(arrowName, dispatchClass,
							RuleFactory.createRule(language, SourceUtils.dynsemSourceSectionUnvailable(),
									rulesForClass.getValue(), arrowName, dispatchClass).getCallTarget());
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
