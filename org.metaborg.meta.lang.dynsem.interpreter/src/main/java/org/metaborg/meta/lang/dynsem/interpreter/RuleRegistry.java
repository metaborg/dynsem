package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.InlinedRuleAdapter;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.OverloadedRule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRoot;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.TAFTermReader;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public abstract class RuleRegistry {

	private final Map<String, RuleRoot> rules = new HashMap<>();

	@TruffleBoundary
	public RuleRoot lookupRule(String name, String constr, int arity) {
		String k = makeKey(name, constr, arity);
		RuleRoot rr = rules.get(k);
		if (rr != null) {
			// Rule r = rr.getRule();
			// assert r.getName().equals(name)
			// && r.getConstructor().equals(constr)
			// && r.getArity() == arity;
			return rr;
		}
		throw new InterpreterException("No rule found for: " + k);
	}

	public void registerRule(RuleRoot rr) {
		Rule r = rr.getRule();
		String k = makeKey(r.getName(), r.getConstructor(), r.getArity());

		RuleRoot prevRR = rules.put(k, rr);
		if (prevRR != null) {
			Rule prevRule = prevRR.getRule();
			OverloadedRule ruleChain = null;
			if (prevRule instanceof OverloadedRule) {
				ruleChain = (OverloadedRule) prevRule;
			} else {
				ruleChain = new OverloadedRule(new InlinedRuleAdapter(prevRule, prevRR.getFrameDescriptor()));
			}
			ruleChain.addNext(new InlinedRuleAdapter(r, rr.getFrameDescriptor()));
			rules.put(k, new RuleRoot(ruleChain, rr.getFrameDescriptor()));
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
			topSpecTerm = reader.parseFromStream(new FileInputStream(specificationFile));
		} catch (IOException ioex) {
			throw new RuntimeException("Could not load specification ATerm", ioex);
		}

		IStrategoList rulesTerm = ruleListTerm(topSpecTerm);
		for (IStrategoTerm ruleTerm : rulesTerm) {
			reg.registerRule(RuleRoot.create(ruleTerm));
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
