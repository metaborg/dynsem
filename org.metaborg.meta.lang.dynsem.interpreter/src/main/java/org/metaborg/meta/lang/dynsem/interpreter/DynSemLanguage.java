package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.jsglr.client.imploder.ImploderOriginTermFactory;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.TermTransformer;
import org.spoofax.terms.io.TAFTermReader;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

public abstract class DynSemLanguage extends TruffleLanguage<DynSemContext> {

	public static final String CONTEXT_OBJECT = "dynsemctx-object";
	public static final String DYNSEM_MIME = "application/x-dynsem";

	@CompilationFinal private DynSemContext ctx;

	@Override
	protected CallTarget parse(final ParsingRequest request) throws Exception {
		// NOTE: the source is the AST of an object language program
		Source code = request.getSource();

		ITermFactory factory = new ImploderOriginTermFactory(new TermFactory());
		IStrategoTerm programAST = new TAFTermReader(factory).parseFromStream(code.getInputStream());

		// convert origin annotations back to origin attachments
		programAST = new TermTransformer(factory, true) {

			@Override
			public IStrategoTerm preTransform(IStrategoTerm term) {
				IStrategoList annos = term.getAnnotations();
				IStrategoList newAnnos = factory.makeList();
				for (IStrategoTerm anno : annos) {
					if (anno instanceof IStrategoAppl
							&& Tools.hasConstructor((IStrategoAppl) anno, "ImploderAttachment")) {
						ImploderAttachment attach = ImploderAttachment.TYPE.fromTerm((IStrategoAppl) anno);
						term.putAttachment(attach);
						continue;
					}
					newAnnos = factory.makeListCons(anno, newAnnos);
				}

				return factory.annotateTerm(term, newAnnos);
			}
		}.transform(programAST);

		ITerm programTerm = ctx.getTermRegistry().parseProgramTerm(programAST);

		RootNode startInterpretation = new RootNode(this) {

			@Child private DispatchNode rootDispatch = DispatchNodeGen.create(SourceUtils
					.getSyntheticSource("rootnote", "startinterpreter", ctx.getMimeTypeObjLanguage()).createSection(1),
					"init");

			@Override
			public Object execute(VirtualFrame frame) {
				return rootDispatch.execute(programTerm.getClass(), new Object[] { programTerm });
			}
		};
		return Truffle.getRuntime().createCallTarget(startInterpretation);
	}

	@Override
	protected DynSemContext createContext(Env env) {
		ctx = (DynSemContext) env.getConfig().get(CONTEXT_OBJECT);
		ctx.initialize(this);
		return ctx;
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
	}

	@Override
	protected boolean isObjectOfLanguage(Object obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected DynSemContext getLanguageGlobal(DynSemContext context) {
		return context;
	}
}