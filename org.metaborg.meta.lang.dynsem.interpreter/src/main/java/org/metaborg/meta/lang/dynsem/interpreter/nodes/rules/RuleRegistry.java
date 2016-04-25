package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.metaborg.meta.lang.dynsem.interpreter.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.TAFTermReader;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.Source;

public class RuleRegistry {

	private final Map<String, RuleRoot> rules = new HashMap<>();

	public RuleRegistry(File specFile) {
		populate(this, specFile);
		init();
	}

	protected void init() {

	}

	@TruffleBoundary
	public RuleRoot lookupRule(String name, String constr, int arity) {
		String k = makeKey(name, constr, arity);
		RuleRoot rr = rules.get(k);
		if (rr != null) {
			return rr;
		}
		throw new InterpreterException("No rule found for: " + k);
	}

	public void registerRule(RuleRoot rr) {
		CompilerAsserts.neverPartOfCompilation();
		Rule r = rr.getRule();
		String k = r.getKey();

		RuleRoot prevRR = rules.put(k, rr);
		if (prevRR != null) {
			Rule prevRule = prevRR.getRule();
			OverloadedRule ruleChain = null;
			if (prevRule instanceof OverloadedRule) {
				ruleChain = (OverloadedRule) prevRule;
			} else {
				ruleChain = new OverloadedRule(new InlinedRuleAdapter(prevRule));
			}
			ruleChain.addNext(new InlinedRuleAdapter(r));
			rules.put(k, new RuleRoot(ruleChain));
		}
	}

	@TruffleBoundary
	public int ruleCount() {
		return rules.size();
	}

	public static String makeKey(String name, String constr, int arity) {
		return name + "/" + constr + "/" + arity;
	}

	private static void populate(RuleRegistry reg, File specificationFile) {
		try {
			Source source = Source.fromFileName(specificationFile.getAbsolutePath().toString());
			TAFTermReader reader = new TAFTermReader(new TermFactory());

			IStrategoTerm topSpecTerm;
			topSpecTerm = reader.parseFromStream(source.getInputStream());

			IStrategoList rulesTerm = ruleListTerm(topSpecTerm);
			for (IStrategoTerm ruleTerm : rulesTerm) {
				reg.registerRule(new RuleRoot(Rule.create(ruleTerm)));
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
