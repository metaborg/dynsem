package org.metaborg.meta.interpreter.framework;

public interface IList<T> extends IConvertibleToStrategoTerm {

	public T head();

	public void replaceHead(T newHead);

	public IList<T> tail();

	public int size();

	public boolean isEmpty();

}