package org.metaborg.meta.lang.dynsem.interpreter.terms;

public interface IConTerm extends ITerm {

	public String constructor();

	public int arity();
	
	public Object[] allSubterms();
}
