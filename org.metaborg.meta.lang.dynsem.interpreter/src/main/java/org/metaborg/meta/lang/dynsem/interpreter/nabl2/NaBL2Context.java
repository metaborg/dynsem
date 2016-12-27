package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import org.metaborg.meta.nabl2.interpreter.InterpreterTerms;
import org.metaborg.meta.nabl2.solver.ISolution;
import org.metaborg.meta.nabl2.stratego.StrategoTerms;
import org.spoofax.interpreter.terms.ITermFactory;

public class NaBL2Context {

    private final ISolution solution;
    private final StrategoTerms strategoTerms;
    private final InterpreterTerms interpreterTerms;

    public NaBL2Context(ISolution solution, ITermFactory termFactory) {
        this.solution = solution;
        this.interpreterTerms = new InterpreterTerms(termFactory);
        this.strategoTerms = new StrategoTerms(termFactory);
    }

    public StrategoTerms getStrategoTerms() {
        return strategoTerms;
    }

    public InterpreterTerms getInterpreterTerms() {
            return interpreterTerms;
    }
 
    public ISolution getSolution() {
        return solution;
    }

}