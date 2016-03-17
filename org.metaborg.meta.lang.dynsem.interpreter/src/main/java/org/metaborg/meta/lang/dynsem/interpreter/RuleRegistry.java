package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ChainedRule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionRule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.TAFTermReader;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public abstract class RuleRegistry {

	private final Map<String, Rule> rules = new HashMap<>();

	@TruffleBoundary
	public Rule lookupRule(String name, String constr, int arity) {
		String k = makeKey(name, constr, arity);
		Rule r = rules.get(k);
		if (r != null) {
			assert r.getName().equals(name)
					&& r.getConstructor().equals(constr)
					&& r.getArity() == arity;
			return r;
		}
		throw new InterpreterException("No rule found for: " + k);
	}

	public void registerRule(Rule r) {
		String k = makeKey(r.getName(), r.getConstructor(), r.getArity());

		Rule prevRule = rules.put(k, r);
		if (prevRule != null) {
			ChainedRule ruleChain = null;
			if (prevRule instanceof ChainedRule) {
				ruleChain = (ChainedRule) prevRule;
			} else {
				ruleChain = new ChainedRule(prevRule);
			}
			ruleChain.addNext(r);
			rules.put(k, ruleChain);
		}
	}

	public int ruleCount() {
		return rules.size();
	}

	private static String makeKey(String name, String constr, int arity) {
		return name + "/" + constr + "/" + arity;
	}

	public static void populate(RuleRegistry reg, File specificationFile) {

		TAFTermReader reader = new TAFTermReader(new TermFactory());

		IStrategoTerm topSpecTerm;
		try {
			topSpecTerm = reader.parseFromStream(new FileInputStream(
					specificationFile));
		} catch (IOException ioex) {
			throw new RuntimeException("Could not load specification ATerm",
					ioex);
		}

		IStrategoList rulesTerm = ruleListTerm(topSpecTerm);
		for (IStrategoTerm ruleTerm : rulesTerm) {
			reg.registerRule(ReductionRule.create(ruleTerm));
		}
	}

	private static IStrategoList ruleListTerm(IStrategoTerm topSpecTerm) {
		IStrategoList sections = Tools.listAt(topSpecTerm, 1);
		for (IStrategoTerm section : sections) {
			if (Tools.isTermAppl(section)
					&& Tools.hasConstructor((IStrategoAppl) section, "Rules", 1)) {
				return Tools.listAt(section, 0);
			}
		}
		throw new InterpreterException(
				"Malformed specification: could not find Rules section");
	}

}
