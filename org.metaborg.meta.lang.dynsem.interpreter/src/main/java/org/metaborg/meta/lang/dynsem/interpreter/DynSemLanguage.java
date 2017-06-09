package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.TAFTermReader;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

public abstract class DynSemLanguage extends TruffleLanguage<DynSemContext> {

	// Keys for configuration parameters for a DynSemContext.

	public static final String CONTEXT_OBJECT = "dynsemctx-object";
	
	public static final String DYNSEM_MIME = "application/x-dynsem";
	
	// public static final String PARSER = "PARSER";
	// public static final String TERM_REGISTRY = "TERM_REGISTRY";
	// public static final String RULE_REGISTRY = "RULE_REGISTRY";

	// private static final String DYNSEM_MIMETYPE = "application/x-dynsem";

	// private static final Source BUILTIN_DYNSEM_SOURCE = Source.newBuilder("Unavailable").name("noname")
	// .mimeType(DYNSEM_MIMETYPE).build();

	@CompilationFinal private DynSemContext ctx;

	public DynSemLanguage() {
	}

	// public abstract boolean isFullBacktrackingEnabled();
	//
	// public abstract boolean isSafeComponentsEnabled();
	//
	// public abstract boolean isTermCachingEnabled();
	//
	// public abstract boolean isDEBUG();

	@Override
	protected CallTarget parse(final ParsingRequest request) throws Exception {
		if (!ctx.isInitialized()) {
			ctx.initialize(this);
		}

		// NOTE: the source is the AST of an object language program
		Source code = request.getSource();
		IStrategoTerm programAST = new TAFTermReader(new TermFactory()).parseFromStream(code.getInputStream());
		ITerm programTerm = ctx.getTermRegistry().parseProgramTerm(programAST);

		RootNode startInterpretation = new RootNode(this) {

			@Child private DispatchNode rootDispatch = DispatchNodeGen.create(
					getSyntheticSource("rootnote", "startinterpreter", ctx.getMimeTypeObjLanguage()).createSection(1),
					"init");

			@Override
			public Object execute(VirtualFrame frame) {
				return rootDispatch.execute(frame, programTerm.getClass(), new Object[] { programTerm });
			}
		};

		return Truffle.getRuntime().createCallTarget(startInterpretation);
	}

	@Override
	protected DynSemContext createContext(Env env) {
		ctx = (DynSemContext) env.getConfig().get(CONTEXT_OBJECT);
		return ctx;
		//
		//
		//
		//
		//
		//
		// Map<String, Object> config = env.getConfig();
		//
		// IDynSemLanguageParser parser = (IDynSemLanguageParser) config.get(PARSER);
		// ITermRegistry termRegistry = (ITermRegistry) config.get(TERM_REGISTRY);
		// RuleRegistry ruleRegistry = (RuleRegistry) config.get(RULE_REGISTRY);
		// return new DynSemContext(parser, termRegistry, ruleRegistry, env.in(), new PrintStream(env.out()),
		// new PrintStream(env.err()), config);
	}

	public DynSemContext getContext() {
		return ctx;
	}

	public static DynSemContext getContext(final RootNode rootnode) {
		return rootnode.getLanguage(DynSemLanguage.class).getContext();
	}

	@Override
	protected Object findExportedSymbol(DynSemContext context, String globalName, boolean onlyExplicit) {
		throw new UnsupportedOperationException();
		// try {
		// String[] splitName = globalName.split("/", 3);
		// if (splitName.length != 3) {
		// return null;
		// }
		// String name = splitName[0];
		// String constr = splitName[1];
		// int arity = Integer.parseInt(splitName[2]);
		// Class<?> dispatchClass = context.getTermRegistry().getConstructorClass(constr, arity);
		// JointRuleRoot ruleUnionRoot = context.getRuleRegistry().lookupRules(name, dispatchClass);
		// return new DynSemRule(ruleUnionRoot);
		// } catch (Exception e) {
		// return null;
		// }
	}

	@Override
	protected boolean isObjectOfLanguage(Object obj) {
		return obj instanceof DynSemRule;
	}

	@Override
	protected Object getLanguageGlobal(DynSemContext context) {
		return context;
	}

	// public static SourceSection getSourceSectionNone() {
	// return BUILTIN_DYNSEM_SOURCE.createUnavailableSection();
	// }
	//
	// public static SourceSection getSourceSectionFromStrategoTerm(IStrategoTerm aterm) {
	// return BUILTIN_DYNSEM_SOURCE.createUnavailableSection();
	// }

	public static Source getSyntheticSource(final String text, final String name, final String mimetype) {
		return Source.newBuilder(text).internal().name(name).mimeType(mimetype).build();
	}
}