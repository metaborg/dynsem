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

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.SourceSection;

public class RuleRegistry {

	private final Map<String, Map<Class<?>, JointRuleRoot>> rules = new HashMap<>();

	private boolean isInit;

	public RuleRegistry() {
	}

	protected void init() {

	}

	@TruffleBoundary
	public int ruleCount() {
		int i = 0;
		for (Map<?, JointRuleRoot> val : rules.values()) {
			for (JointRuleRoot root : val.values()) {
				i += root.getJointNode().getUnionNode().getRules().size();
			}
		}
		return i;
	}

	@TruffleBoundary
	public void registerJointRule(String arrowName, Class<?> dispatchClass, JointRuleRoot jointRuleRoot) {
		Map<Class<?>, JointRuleRoot> rulesForName = rules.get(arrowName);

		if (rulesForName == null) {
			rulesForName = new HashMap<>();
			rules.put(arrowName, rulesForName);
		}
		rulesForName.put(dispatchClass, jointRuleRoot);
	}

	@TruffleBoundary
	public JointRuleRoot lookupRules(String arrowName, Class<?> dispatchClass) {
		if (!isInit) {
			init();
			isInit = true;
		}
		JointRuleRoot jointRuleForClass = null;

		Map<Class<?>, JointRuleRoot> jointRulesForName = rules.get(arrowName);

		if (jointRulesForName != null) {
			jointRuleForClass = jointRulesForName.get(dispatchClass);
		}

		if (jointRuleForClass == null) {
			jointRuleForClass = new JointRuleRoot(SourceSection.createUnavailable("rule", "adhoc"), RuleKind.ADHOC,
					arrowName, dispatchClass, new Rule[0]);
			registerJointRule(arrowName, dispatchClass, jointRuleForClass);
		}
		return jointRuleForClass;
	}

	public final static void populate(RuleRegistry registry, InputStream specStream) {
		CompilerAsserts.neverPartOfCompilation();
		try {
			TAFTermReader reader = new TAFTermReader(new TermFactory());

			IStrategoTerm topSpecTerm;
			topSpecTerm = reader.parseFromStream(specStream);
			specStream.close();

			IStrategoList rulesTerm = ruleListTerm(topSpecTerm);

			Map<String, Map<Class<?>, List<Rule>>> rules = new HashMap<>();

			for (IStrategoTerm ruleTerm : rulesTerm) {
				Rule r = Rule.create(ruleTerm);

				Map<Class<?>, List<Rule>> rulesForName = rules.get(r.getArrowName());
				if (rulesForName == null) {
					rulesForName = new HashMap<>();
					rules.put(r.getArrowName(), rulesForName);
				}

				List<Rule> rulesForClass = rulesForName.get(r.getDispatchClass());

				if (rulesForClass == null) {
					rulesForClass = new LinkedList<>();
					rulesForName.put(r.getDispatchClass(), rulesForClass);
				}

				rulesForClass.add(r);
			}

			for (Entry<String, Map<Class<?>, List<Rule>>> rulesForNameEntry : rules.entrySet()) {
				final String arrowName = rulesForNameEntry.getKey();
				for (Entry<Class<?>, List<Rule>> rulesForClass : rulesForNameEntry.getValue().entrySet()) {
					Class<?> dispatchClass = rulesForClass.getKey();
					RuleKind kind = rulesForClass.getValue().get(0).getKind();
					registry.registerJointRule(arrowName, dispatchClass,
							new JointRuleRoot(SourceSection.createUnavailable("rule", "multiple locations"), kind,
									arrowName, dispatchClass, rulesForClass.getValue().toArray(new Rule[] {})));
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
