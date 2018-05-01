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

public class RuleRegistry implements IRuleRegistry {

	private final Map<String, Map<String, CallTarget[]>> rules = new HashMap<>();

	@CompilationFinal private boolean isInit;

	@CompilationFinal private DynSemLanguage language;

	protected void init() {
	}

	@Override
	public void setLanguage(DynSemLanguage language) {
		this.language = language;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.IRuleRegistry#getLanguage()
	 */
	@Override
	public DynSemLanguage getLanguage() {
		return language;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.IRuleRegistry#registerRule(java.lang.String,
	 * java.lang.Class, com.oracle.truffle.api.CallTarget)
	 */
	@Override
	public void registerRule(String arrowName, String dispatchKey, CallTarget[] targets) {
		System.out.println("Register " + dispatchKey + " " + targets.length);
		CompilerAsserts.neverPartOfCompilation();
		Map<String, CallTarget[]> rulesForName = rules.get(arrowName);

		if (rulesForName == null) {
			rulesForName = new HashMap<>();
			rules.put(arrowName, rulesForName);
		}
		rulesForName.put(dispatchKey, targets);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.IRuleRegistry#lookupRule(java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	@TruffleBoundary
	public CallTarget[] lookupRules(String arrowName, String dispatchKey) {
		if (!isInit) {
			init();
			isInit = true;
		}

		Map<String, CallTarget[]> rulesForName = rules.get(arrowName);

		if (rulesForName == null) {
			return new CallTarget[0];
		}

		CallTarget[] rules = rulesForName.get(dispatchKey);

		return rules != null ? rules : new CallTarget[0];
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

			Map<String, Map<String, List<ReductionRule>>> rules = new HashMap<>();

			for (IStrategoTerm ruleTerm : rulesTerm) {
				ReductionRule r = ReductionRule.create(language, (IStrategoAppl) ruleTerm);

				Map<String, List<ReductionRule>> rulesForName = rules.get(r.getArrowName());
				if (rulesForName == null) {
					rulesForName = new HashMap<>();
					rules.put(r.getArrowName(), rulesForName);
				}

				List<ReductionRule> rulesForClass = rulesForName.get(r.getDispatchKey());

				if (rulesForClass == null) {
					rulesForClass = new LinkedList<>();
					rulesForName.put(r.getDispatchKey(), rulesForClass);
				}

				rulesForClass.add(r);
			}

			for (Entry<String, Map<String, List<ReductionRule>>> rulesForNameEntry : rules.entrySet()) {
				final String arrowName = rulesForNameEntry.getKey();
				for (Entry<String, List<ReductionRule>> rulesForClass : rulesForNameEntry.getValue().entrySet()) {
					String dispatchKey = rulesForClass.getKey();
					registerRule(arrowName, dispatchKey,
							RuleFactory.createRuleTargets(language, SourceUtils.dynsemSourceSectionUnvailable(),
									rulesForClass.getValue()));
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
