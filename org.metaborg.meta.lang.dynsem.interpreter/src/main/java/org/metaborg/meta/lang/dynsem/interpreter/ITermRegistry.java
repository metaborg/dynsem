package org.metaborg.meta.lang.dynsem.interpreter;

import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ITermBuildFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermMatchPatternFactory;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface ITermRegistry {

	public ITermBuildFactory lookupBuildFactory(String constr, int arity);

	public ITermMatchPatternFactory lookupMatchFactory(String name, int arity);

	public ITerm parseProgramTerm(IStrategoTerm t);
}
