package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.action.EndNamedGoal;
import org.metaborg.core.action.ITransformGoal;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.project.IProject;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2Context;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.InitEvalNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITermTransformer;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.spoofax.core.context.scopegraph.ISpoofaxScopeGraphContext;
import org.metaborg.spoofax.core.shell.CLIUtils;
import org.metaborg.spoofax.core.unit.ISpoofaxAnalyzeUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxInputUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxParseUnit;
import org.metaborg.util.concurrent.IClosableLock;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.ImmutableMap;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.source.Source;

public abstract class DynSemLanguage extends TruffleLanguage<DynSemContext> {

	@CompilationFinal private DynSemContext ctx;

	public DynSemLanguage() {
	}

	@Override
	protected CallTarget parse(final ParsingRequest request) throws Exception {
		CompilerAsserts.neverPartOfCompilation();
		Source source = request.getSource();

		DynSemContext ctx = getContextReference().get();
		Spoofax S = ctx.getSpoofax();
		CLIUtils cli = ctx.getSpoofaxCLIUtils();
		ILanguageImpl spxLang = cli.getLanguage(getSpoofaxLanguageName());
		FileObject f = resolveFile(S, source.getPath());

		IStrategoTerm program;
		ImmutableMap<String, Object> props;
		try {
			IProject project = cli.getProject(f);

			String text = S.sourceTextService.text(f);
			ISpoofaxInputUnit input = S.unitService.inputUnit(f, text, spxLang, null);
			ISpoofaxParseUnit parsed = S.syntaxService.parse(input);
			if (!parsed.valid()) {
				throw new MetaborgException("Parsing failed.");
			}
			cli.printMessages(ctx.getErr(), parsed.messages());
			if (!parsed.success()) {
				throw new MetaborgException("Parsing returned errors.");
			}

			IContext context = S.contextService.get(f, project, spxLang);
			if (S.analysisService.available(spxLang)) {
				ISpoofaxAnalyzeUnit analyzed;
				try (IClosableLock lock = context.write()) {
					analyzed = S.analysisService.analyze(parsed, context).result();
				}
				if (!analyzed.valid()) {
					throw new MetaborgException("Analysis failed.");
				}
				cli.printMessages(ctx.getErr(), analyzed.messages());
				if (!analyzed.success()) {
					throw new MetaborgException("Analysis returned errors.");
				}
				ImmutableMap.Builder<String, Object> propBuilder = ImmutableMap.builder();
				if (context instanceof ISpoofaxScopeGraphContext) {
					ISpoofaxScopeGraphContext<?> scopeGraphContext = (ISpoofaxScopeGraphContext<?>) context;
					scopeGraphContext.unit(f.getName().getURI()).solution().ifPresent(solution -> {
						propBuilder.put(NaBL2Context.class.getName(),
								new NaBL2Context(solution.findAndLock(), S.termFactoryService.getGeneric()));
					});
				}
				props = propBuilder.build();
				if (analyzed.hasAst()) {
					ITransformGoal mkoccgoal = new EndNamedGoal("Make Occurrences");
					if (S.transformService.available(context, mkoccgoal)) {
						program = S.transformService.transform(analyzed, context, mkoccgoal).iterator().next().ast();
					} else {
						program = analyzed.ast();
					}
				} else {
					program = parsed.ast();
				}
			} else {
				program = parsed.ast();
				props = ImmutableMap.of();
			}
		} catch (IOException e) {
			throw new MetaborgException("Analysis failed.", e);
		}

		ctx.writeProperties(props);

		ITerm programTerm = ctx.getTermRegistry().parseProgramTerm(ctx.getTermTransformer().transform(program));

		return Truffle.getRuntime().createCallTarget(
				new InitEvalNode(this, SourceUtils.dynsemSourceSectionFromATerm(program), programTerm));
	}

	@TruffleBoundary
	private static FileObject resolveFile(Spoofax S, String path) {
		return S.resourceService.resolve(path);
	}

	@Override
	protected DynSemContext createContext(Env env) {
		return new DynSemContext(createParser(env), createTransformer(env), createTermRegistry(env),
				createRuleRegistry(env), env.in(), env.out(), env.err(), getSpecificationTerm(env),
				isWithNativeFrames(env));
	}

	@Override
	protected void initializeContext(DynSemContext context) throws Exception {
		context.initialize(this);
	}

	@Override
	protected Object findExportedSymbol(DynSemContext context, String globalName, boolean onlyExplicit) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected boolean isObjectOfLanguage(Object object) {
		return object instanceof RuleResult;
	}

	@Override
	protected DynSemContext getLanguageGlobal(DynSemContext context) {
		return context;
	}

	protected abstract IDynSemLanguageParser createParser(Env env);

	protected abstract ITermTransformer createTransformer(Env env);

	protected abstract ITermRegistry createTermRegistry(Env env);

	protected abstract RuleRegistry createRuleRegistry(Env env);

	protected abstract InputStream getSpecificationTerm(Env env);

	protected abstract boolean isWithNativeFrames(Env env);

	protected abstract String getSpoofaxLanguageName();
}