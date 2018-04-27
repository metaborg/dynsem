package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ITermBuildFactory;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface ITermRegistry {

	public ITermBuildFactory lookupNativeOpBuildFactory(Class<?> termClass);

	public ITermBuildFactory lookupNativeTypeAdapterBuildFactory(String sort, String function, int arity);

	public Class<?> getNativeOperatorClass(String constr, int arity);

	public ITerm parseProgramTerm(IStrategoTerm t);

}
