package org.metaborg.meta.lang.dynsem.interpreter.terms;

public interface IApplTerm extends ITerm {

	@Deprecated
	public String constructor();

	public Class<?> getSortClass();
}
