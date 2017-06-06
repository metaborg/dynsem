package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.JointRuleRoot;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRegistry;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DynSemLanguage extends TruffleLanguage<DynSemContext> {

	// Keys for configuration parameters for a DynSemContext.
	public static final String PARSER = "PARSER";
	public static final String TERM_REGISTRY = "TERM_REGISTRY";
	public static final String RULE_REGISTRY = "RULE_REGISTRY";

	public static final String DYNSEM_MIMETYPE = "application/x-dynsem";

	public static final Source BUILTIN_DYNSEM_SOURCE = Source.newBuilder("Unavailable").name("noname").mimeType(DYNSEM_MIMETYPE)
			.build();

	public DynSemLanguage() {
	}

	public abstract boolean isFullBacktrackingEnabled();

	public abstract boolean isSafeComponentsEnabled();

	public abstract boolean isTermCachingEnabled();

	public abstract boolean isDEBUG();

	@Override
	protected DynSemContext createContext(Env env) {
		Map<String, Object> config = env.getConfig();
		IDynSemLanguageParser parser = (IDynSemLanguageParser) config.get(PARSER);
		ITermRegistry termRegistry = (ITermRegistry) config.get(TERM_REGISTRY);
		RuleRegistry ruleRegistry = (RuleRegistry) config.get(RULE_REGISTRY);
		return new DynSemContext(parser, termRegistry, ruleRegistry, env.in(), new PrintStream(env.out()),
				new PrintStream(env.err()), config);
	}

	public Node createFindContextNode0() {
		return createFindContextNode();
	}

	public DynSemContext findContext0(Node n) {
		return findContext(n);
	}

	public DynSemContext getContext() {
		return findContext(createFindContextNode());
	}

	@Override
	protected Object evalInContext(Source source, Node node, MaterializedFrame mFrame) throws IOException {
		throw new IllegalStateException("evalInContext not supported");
	}

	@Override
	protected Object findExportedSymbol(DynSemContext context, String globalName, boolean onlyExplicit) {
		try {
			String[] splitName = globalName.split("/", 3);
			if (splitName.length != 3) {
				return null;
			}
			String name = splitName[0];
			String constr = splitName[1];
			int arity = Integer.parseInt(splitName[2]);
			Class<?> dispatchClass = context.getTermRegistry().getConstructorClass(constr, arity);
			JointRuleRoot ruleUnionRoot = context.getRuleRegistry().lookupRules(name, dispatchClass);
			return new DynSemRule(ruleUnionRoot);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected boolean isObjectOfLanguage(Object obj) {
		return obj instanceof DynSemRule;
	}

	@Override
	protected Object getLanguageGlobal(DynSemContext context) {
		return context;
	}

	public static SourceSection getSourceSectionNone() {
		return BUILTIN_DYNSEM_SOURCE.createUnavailableSection();
	}

	public static SourceSection getSourceSectionFromStrategoTerm(IStrategoTerm aterm) {
		return BUILTIN_DYNSEM_SOURCE.createUnavailableSection();
	}

}