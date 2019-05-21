package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import org.spoofax.interpreter.terms.ITermFactory;

import mb.nabl2.solver.ISolution;
import mb.nabl2.terms.stratego.StrategoTerms;

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