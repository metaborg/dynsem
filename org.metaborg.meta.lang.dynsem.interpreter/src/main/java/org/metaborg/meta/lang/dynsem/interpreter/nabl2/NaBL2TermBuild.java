package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemRunner;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.stratego.StrategoTerms;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.source.SourceSection;

public abstract class NaBL2TermBuild<T extends ITerm,R extends ITerm> extends TermBuild { 

    public NaBL2TermBuild (SourceSection source) { 
        super(source);
    }

    public R doGet(T term) {
        NaBL2Context context = (NaBL2Context) getContext().readProperty(DynSemRunner.NABL2_CONTEXT_PROP, null);
        if (context == null) {
            throw new IllegalStateException("No NaBL2 context available. Does the language use NaBL2, and was the interpreter invoked using the correct runner?");
        }
        StrategoTerms st = context.getStrategoTerms();
        org.metaborg.meta.nabl2.terms.ITerm t = st.fromStratego(term.getStrategoTerm());
        org.metaborg.meta.nabl2.terms.ITerm r = eval(t, context.getSolution());
        return build(st.toStratego(r));
    }

    protected abstract org.metaborg.meta.nabl2.terms.ITerm eval(org.metaborg.meta.nabl2.terms.ITerm term, ISolution solution);

    protected abstract R build(IStrategoTerm term);

}