package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.stratego.StrategoTerms;
import org.spoofax.interpreter.terms.ITermFactory;

public class NaBL2Context {

    private final ISolution solution;
    private final StrategoTerms strategoTerms;

    public NaBL2Context(ISolution solution, ITermFactory termFactory) {
        this.solution = solution;
        this.strategoTerms = new StrategoTerms(termFactory);
    }

    public StrategoTerms getStrategoTerms() {
        return strategoTerms;
    }

    public ISolution getSolution() {
        return solution;
    }

}