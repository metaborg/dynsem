package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ITermBuildFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermMatchPatternFactory;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface ITermRegistry {

	public ITermBuildFactory lookupBuildFactory(String constr, int arity);

	public ITermMatchPatternFactory lookupMatchFactory(String name, int arity);

	public ITermBuildFactory lookupNativeOpBuildFactory(String constr, int arity);

	public ITermBuildFactory lookupNativeTypeAdapterBuildFactory(String sort,
			String function, int arity);

	public ITerm parseProgramTerm(IStrategoTerm t);
}
