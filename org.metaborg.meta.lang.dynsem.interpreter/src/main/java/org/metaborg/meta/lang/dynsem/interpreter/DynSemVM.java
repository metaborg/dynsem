package org.metaborg.meta.lang.dynsem.interpreter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import mb.jsglr.shared.ImploderAttachment;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.TermTransformer;
import org.spoofax.terms.attachments.OriginAttachment;
import org.spoofax.terms.io.TAFTermReader;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.vm.PolyglotEngine;
import com.oracle.truffle.api.vm.PolyglotEngine.Builder;

public class DynSemVM {

	private final DynSemContext ctx;

	private final PolyglotEngine engine;

	@TruffleBoundary
	public DynSemVM(Map<String, Object> config) {
		this.ctx = new DynSemContext(config);

		engine = createPolyglotBuilder(config).build();
	}

	@TruffleBoundary
	public Callable<RuleResult> getCallable(String file, String workingDirectory, Map<String, Object> properties) {
		try {
			File f = new File(file);
			if (!f.isAbsolute() && !f.exists()) {
				f = new File(workingDirectory, file);
			}
			IStrategoTerm term = ctx.getTermTransformer().transform(ctx.getParser()
					.parse(Source.newBuilder(f).name("Program").mimeType(ctx.getMimeTypeObjLanguage()).build()));
			return getCallable(term, properties);
		} catch (IOException ioex) {
			throw new RuntimeException("Eval failed", ioex);
		}
	}

	@TruffleBoundary
	public Callable<RuleResult> getCallable(IStrategoTerm term, Map<String, Object> properties) {
		CompilerAsserts.neverPartOfCompilation();
		assert term != null;
		assert engine.getLanguages().containsKey(ctx.getMimeTypeObjLanguage());
		// FIXME: this is bad bad bad, because the properties are per-program but we are setting them per-VM
		ctx.writeProperties(properties);
		try {
			ITermFactory factory = new TermFactory();

			// convert origin attachments to annotations
			IStrategoTerm aTerm = new TermTransformer(factory, false) {

				@Override
				@TruffleBoundary
				public IStrategoTerm preTransform(IStrategoTerm term) {
					OriginAttachment orig = OriginAttachment.get(term);
					IStrategoList annos = null;
					if (orig != null) {
						IStrategoAppl attachmentTerm = ImploderAttachment.TYPE.toTerm(factory,
								ImploderAttachment.get(orig.getOrigin()));
						annos = factory.makeListCons(attachmentTerm, term.getAnnotations());
					}
					if (annos != null) {
						return factory.annotateTerm(term, annos);
					}
					return term;
				}
			}.transform(term);

			// massage the term into an input stream for the Source
			ByteArrayOutputStream termOut = new ByteArrayOutputStream();

			new TAFTermReader(factory).unparseToFile(aTerm, termOut);
			ByteArrayInputStream termIn = new ByteArrayInputStream(termOut.toByteArray());

			Source code = Source.newBuilder(new InputStreamReader(termIn)).name("Program")
					.mimeType(ctx.getMimeTypeObjLanguage()).build();

			return new Callable<RuleResult>() {
				@Override
				public RuleResult call() throws Exception {
					return engine.eval(code).as(RuleResult.class);
				}
			};
		} catch (IOException ioex) {
			throw new RuntimeException("Evaluation failed", ioex);
		}

	}

	@TruffleBoundary
	private Builder createPolyglotBuilder(Map<String, Object> config) {
		final Builder builder = PolyglotEngine.newBuilder();
		builder.setIn((InputStream) config.get(DynSemContext.CONFIG_STDIN));
		builder.setOut((OutputStream) config.get(DynSemContext.CONFIG_STDOUT));
		builder.setErr((OutputStream) config.get(DynSemContext.CONFIG_STDERR));

		String mimeType = ctx.getMimeTypeObjLanguage();

		for (Entry<String, Object> cfg : config.entrySet()) {
			builder.config(mimeType, cfg.getKey(), cfg.getValue());
		}

		builder.config(mimeType, DynSemLanguage.CONTEXT_OBJECT, ctx);

		return builder;
	}

	public DynSemContext getContext() {
		return ctx;
	}

}
