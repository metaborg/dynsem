package org.metaborg.meta.lang.dynsem.interpreter.terms;

/**
 * Interface that all application terms must implement.
 * 
 * @author vladvergu
 *
 */
public interface IApplTerm extends ITerm {

	/**
	 * @return the {@link Class} representing the sort of this application term.
	 */
	public Class<?> getSortClass();
}
