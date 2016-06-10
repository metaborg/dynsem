package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.metaborg.meta.lang.dynsem.interpreter.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.TAFTermReader;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class RuleRegistry {

	private final Map<String, Map<Class<?>, RuleRoot[]>> rules = new HashMap<>();

	public RuleRegistry() {
		init();
	}

	protected void init() {

	}

	public void registerRules(String arrowName, Class<?> dispatchClass, RuleRoot[] rs) {
		Map<Class<?>, RuleRoot[]> rulesForName = rules.get(arrowName);

		if (rulesForName == null) {
			rulesForName = new HashMap<>();
			rules.put(arrowName, rulesForName);
		}

		rulesForName.put(dispatchClass, rs);
	}

	@TruffleBoundary
	public RuleRoot[] lookupRules(String arrowName, Class<?> dispatchClass) {
		RuleRoot[] rulesForClass = null;

		Map<Class<?>, RuleRoot[]> rulesForName = rules.get(arrowName);

		if (rulesForName != null) {
			rulesForClass = rulesForName.get(dispatchClass);
		}

		if (rulesForClass == null) {
			throw new InterpreterException(
					"No rules found for arrow <" + arrowName + "> on <" + dispatchClass.getName() + ">");
		}

		return rulesForClass;
	}

	public final static void populate(RuleRegistry registry, InputStream specStream) {
		try {
			TAFTermReader reader = new TAFTermReader(new TermFactory());

			IStrategoTerm topSpecTerm;
			topSpecTerm = reader.parseFromStream(specStream);
			specStream.close();

			IStrategoList rulesTerm = ruleListTerm(topSpecTerm);

			Map<String, Map<Class<?>, List<RuleRoot>>> rules = new HashMap<>();

			for (IStrategoTerm ruleTerm : rulesTerm) {
				Rule r = Rule.create(ruleTerm);

				Map<Class<?>, List<RuleRoot>> rulesForName = rules.get(r.getArrowName());
				if (rulesForName == null) {
					rulesForName = new HashMap<>();
					rules.put(r.getArrowName(), rulesForName);
				}

				List<RuleRoot> rulesForClass = rulesForName.get(r.getDispatchClass());

				if (rulesForClass == null) {
					rulesForClass = new LinkedList<>();
					rulesForName.put(r.getDispatchClass(), rulesForClass);
				}

				rulesForClass.add(new RuleRoot(r));
			}

			for (Entry<String, Map<Class<?>, List<RuleRoot>>> rulesForNameEntry : rules.entrySet()) {
				final String arrowName = rulesForNameEntry.getKey();
				for (Entry<Class<?>, List<RuleRoot>> rulesForClass : rulesForNameEntry.getValue().entrySet()) {
					registry.registerRules(arrowName, rulesForClass.getKey(),
							rulesForClass.getValue().toArray(new RuleRoot[] {}));
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
