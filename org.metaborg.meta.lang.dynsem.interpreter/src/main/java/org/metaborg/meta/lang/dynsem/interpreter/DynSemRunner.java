package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgConstants;
import org.metaborg.core.MetaborgException;
import org.metaborg.core.MetaborgRuntimeException;
import org.metaborg.core.context.IContext;
import org.metaborg.core.language.ILanguage;
import org.metaborg.core.language.ILanguageImpl;
import org.metaborg.core.messages.IMessage;
import org.metaborg.core.messages.IMessagePrinter;
import org.metaborg.core.messages.StreamMessagePrinter;
import org.metaborg.core.project.IProject;
import org.metaborg.core.project.IProjectService;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2Context;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.spoofax.core.context.scopegraph.ISpoofaxScopeGraphContext;
import org.metaborg.spoofax.core.unit.ISpoofaxAnalyzeUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxInputUnit;
import org.metaborg.spoofax.core.unit.ISpoofaxParseUnit;
import org.metaborg.util.concurrent.IClosableLock;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.ImmutableMap;

public class DynSemRunner {
    private static final ILogger logger = LoggerUtils.logger(DynSemRunner.class);

    public static final String SPOOFAXPATH = "SPOOFAXPATH";

    private final Spoofax S;
    private final ILanguageImpl language;
    private final DynSemEntryPoint entryPoint;

    public DynSemRunner(Spoofax S, String languageName, DynSemEntryPoint interpreter) throws MetaborgException {
        this.S = S;
        this.language = loadLanguage(languageName);
        this.entryPoint = interpreter;
    }

    private ILanguageImpl loadLanguage(String languageName) throws MetaborgException {
        String spoofaxPath = System.getenv(SPOOFAXPATH);
        if (spoofaxPath != null) {
            for (String spoofaxDirName : spoofaxPath.split(":")) {
                if (spoofaxDirName.isEmpty()) {
                    continue;
                }
                FileObject spoofaxDir;
                try {
                    spoofaxDir = S.resourceService.resolve(spoofaxDirName);
                } catch (MetaborgRuntimeException ex) {
                    String message = logger.format("Invalid path {} in " + SPOOFAXPATH, spoofaxDirName);
                    logger.warn(message, ex);
                    continue;
                }
                try {
                    S.languageDiscoveryService.scanLanguagesInDirectory(spoofaxDir);
                } catch (MetaborgException ex) {
                    String message = logger.format("Failed to load languages at {}.", spoofaxDir);
                    logger.warn(message, ex);
                }
            }
        }
        ILanguage lang = S.languageService.getLanguage(languageName);
        if (lang == null) {
            throw new MetaborgException("Cannot find language " + languageName + ".");
        }
        ILanguageImpl langImpl = lang.activeImpl();
        if (langImpl == null) {
            throw new MetaborgException("Language " + languageName + " has no active implementation.");
        }
        return langImpl;
    }

    public Object run(FileObject file, InputStream in, OutputStream out, OutputStream err) throws MetaborgException {
        IStrategoTerm program;
        ImmutableMap<String, Object> props;
        try {
            IProjectService projectService = S.injector.getInstance(IProjectService.class);
            IProject project = projectService.get(file);
            if (project == null) {
                throw new MetaborgException(
                        "File is not part of a project. Missing " + MetaborgConstants.FILE_CONFIG + "?");
            }

            String text = S.sourceTextService.text(file);
            ISpoofaxInputUnit input = S.unitService.inputUnit(file, text, language, null);
            ISpoofaxParseUnit parsed = S.syntaxService.parse(input);
            if (!parsed.valid()) {
                throw new MetaborgException("Parsing failed.");
            }
            printMessages(err, parsed.messages());
            if (!parsed.success()) {
                throw new MetaborgException("Parsing returned errors.");
            }

            IContext context = S.contextService.get(file, project, language);
            ISpoofaxAnalyzeUnit analyzed;
            try (IClosableLock lock = context.write()) {
                analyzed = S.analysisService.analyze(parsed, context).result();
            }
            if (!analyzed.valid()) {
                throw new MetaborgException("Analysis failed.");
            }
            printMessages(err, analyzed.messages());
            if (!analyzed.success()) {
                throw new MetaborgException("Analysis returned errors.");
            }
            ImmutableMap.Builder<String, Object> propBuilder = ImmutableMap.builder();
            if (context instanceof ISpoofaxScopeGraphContext) {
                ISpoofaxScopeGraphContext<?> scopeGraphContext = (ISpoofaxScopeGraphContext<?>) context;
                scopeGraphContext.unit(file.getName().getURI()).solution().ifPresent(solution -> {
                    propBuilder.put(NaBL2Context.class.getName(),
                            new NaBL2Context(solution, S.termFactoryService.getGeneric()));
                });
            }
            props = propBuilder.build();
            program = analyzed.ast();
        } catch (IOException e) {
            throw new MetaborgException("Analysis failed.", e);
        }
        try {
            Callable<RuleResult> runner = entryPoint.getCallable(program, in, out, err, props);
            RuleResult result = runner.call();
            return result.result;
        } catch (Exception e) {
            throw new MetaborgException("Evaluation failed.", e);
        }
    }

    private void printMessages(OutputStream out, Iterable<IMessage> messages) {
        IMessagePrinter printer = new StreamMessagePrinter(S.sourceTextService, true, false, out, out, out);
        for (IMessage message : messages) {
            printer.print(message, false);
        }
    }

}