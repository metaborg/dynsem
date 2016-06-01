package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRegistry;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.instrument.Visualizer;
import com.oracle.truffle.api.instrument.WrapperNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;

public abstract class DynSemLanguage extends TruffleLanguage<DynSemContext> {
	// Keys for configuration parameters for a DynSemContext.
	public static final String TERM_REGISTRY = "TERM_REGISTRY";
	public static final String RULE_REGISTRY = "RULE_REGISTRY";

	public DynSemLanguage() {
	}

	@Override
	protected DynSemContext createContext(Env env) {
		Map<String, Object> config = env.getConfig();
		ITermRegistry termRegistry = (ITermRegistry) config.get(TERM_REGISTRY);
		RuleRegistry ruleRegistry = (RuleRegistry) config.get(RULE_REGISTRY);
		return new DynSemContext(termRegistry, ruleRegistry, env.in(), new PrintStream(env.out()));
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
	protected WrapperNode createWrapperNode(Node node) {
		throw new NotImplementedException();
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
			return context.getRuleRegistry().lookupRule(name, constr, arity);
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

	@Override
	protected Visualizer getVisualizer() {
		return null;
	}

	@Override
	protected boolean isInstrumentable(Node node) {
		return false;
	}
}