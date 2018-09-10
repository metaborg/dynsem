package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ITermBuildFactory;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermMatchPatternFactory;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITermInit;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface ITermRegistry {

	public ITermBuildFactory lookupBuildFactory(Class<?> termClass);

	public ITermMatchPatternFactory lookupMatchFactory(Class<?> termClass);

	public ITermBuildFactory lookupNativeOpBuildFactory(Class<?> termClass);

	public ITermBuildFactory lookupNativeTypeAdapterBuildFactory(String sort, String function, int arity);

	public ITermInit lookupClassConstructorWrapper(Class<?> termClass);

	public Class<?> getConstructorClass(String constr, int arity);

	public Class<?> getNativeOperatorClass(String constr, int arity);

	public <T> Class<? extends IListTerm<T>> getListClass(Class<T> elemClass);

	public Class<?> getMapClass(String keySortName, String mapSortName);


	public ITerm parseProgramTerm(IStrategoTerm t);
}
