package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.ParseError;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.TAFTermReader;

public class RuleRegistry implements IRuleRegistry {

	private final Map<String, Rule> rules = new HashMap<>();

	@Override
	public Rule lookupRule(String name, int arity) {
		String k = makeKey(name, arity);
		Rule r = rules.get(k);
		assert r.getConstructor().equals(name) && r.getArity() == arity;
		if (r != null) {
			return r;
		}
		throw new InterpreterException("No rule found for: " + k);
	}

	public void registerRule(Rule r) {
		String k = makeKey(r.getConstructor(), r.getArity());
		Rule or = rules.put(k, r);
		if (or != null) {
			throw new InterpreterException("Duplicate rule for: " + k);
		}
	}

	@Override
	public int ruleCount() {
		return rules.size();
	}

	private static String makeKey(String name, int arity) {
		return name + "/" + arity;
	}

	public static RuleRegistry create(File specificationFile)
			throws ParseError, FileNotFoundException, IOException {

		RuleRegistry reg = new RuleRegistry();

		TAFTermReader reader = new TAFTermReader(new TermFactory());
		IStrategoTerm topSpecTerm = reader.parseFromStream(new FileInputStream(
				specificationFile));

		IStrategoList rulesTerm = ruleListTerm(topSpecTerm);
		for (IStrategoTerm ruleTerm : rulesTerm) {
			reg.registerRule(Rule.create(ruleTerm));
		}
		return reg;
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
