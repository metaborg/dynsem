package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.action.EndNamedGoal;
import org.metaborg.core.action.ITransformGoal;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.project.IProject;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2Context;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.spoofax.core.context.constraint.IConstraintContext;
import org.metaborg.spoofax.core.shell.CLIUtils;
import org.metaborg.spoofax.core.unit.ISpoofaxAnalyzeUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxInputUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxParseUnit;
import org.metaborg.util.concurrent.IClosableLock;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.ImmutableMap;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import mb.nabl2.spoofax.analysis.IResult;
import mb.nabl2.stratego.StrategoBlob;

public class DynSemRunner {
	private final Spoofax S;
	private final CLIUtils cli;
	private final ILanguageImpl language;
	private final DynSemVM vm;

	@TruffleBoundary
	public DynSemRunner(Spoofax S, String languageName, DynSemVM vm) throws MetaborgException {
		this.S = S;
		this.cli = new CLIUtils(S);
		cli.loadLanguagesFromPath();
		this.language = cli.getLanguage(languageName);
		this.vm = vm;
	}

	public Object run(FileObject file) throws MetaborgException {
		final RunConfig runCfg = prepareForEvaluation(file);
		try {
			Callable<RuleResult> runner = vm.getCallable(runCfg.program, runCfg.props);
			RuleResult result = runner.call();
			return result.result;
		} catch (Exception e) {
			throw new MetaborgException("Evaluation failed.", e);
		}
	}

	@TruffleBoundary
	private RunConfig prepareForEvaluation(FileObject file) throws MetaborgException {
		CompilerAsserts.neverPartOfCompilation();
		IStrategoTerm program;
		ImmutableMap<String, Object> props;
		try {
			IProject project = cli.getProject(file);

			String text = S.sourceTextService.text(file);
			ISpoofaxInputUnit input = S.unitService.inputUnit(file, text, language, null);
			ISpoofaxParseUnit parsed = S.syntaxService.parse(input);
			if (!parsed.valid()) {
				throw new MetaborgException("Parsing failed.");
			}
			cli.printMessages(vm.getContext().getErr(), parsed.messages());
			if (!parsed.success()) {
				throw new MetaborgException("Parsing returned errors.");
			}

			IContext context = S.contextService.get(file, project, language);
			if (S.analysisService.available(language)) {
				ISpoofaxAnalyzeUnit analyzed;
				try (IClosableLock lock = context.write()) {
					analyzed = S.analysisService.analyze(parsed, context).result();
				}
				if (!analyzed.valid()) {
					throw new MetaborgException("Analysis failed.");
				}
				cli.printMessages(vm.getContext().getErr(), analyzed.messages());
				if (!analyzed.success()) {
					throw new MetaborgException("Analysis returned errors.");
				}
				ImmutableMap.Builder<String, Object> propBuilder = ImmutableMap.builder();
				if (context instanceof IConstraintContext) {
					IConstraintContext constraintContext = (IConstraintContext) context;
					if (constraintContext.hasAnalysis(file)) {
						IStrategoTerm analysisTerm = constraintContext.getAnalysis(file);
						StrategoBlob.match(analysisTerm, IResult.class).ifPresent(r -> {
							propBuilder.put(NaBL2Context.class.getName(),
									new NaBL2Context(r.solution(), S.termFactoryService.getGeneric()));
						});
					}
				}
				props = propBuilder.build();
				if (analyzed.hasAst()) {
					ITransformGoal mkoccgoal = new EndNamedGoal("Make Occurrences");
					if (S.transformService.available(language, mkoccgoal)) {
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
		return new RunConfig(program, props);
	}

	private class RunConfig {
		protected final IStrategoTerm program;
		protected final ImmutableMap<String, Object> props;

		public RunConfig(IStrategoTerm program, ImmutableMap<String, Object> props) {
			this.program = program;
			this.props = props;
		}
	}

}