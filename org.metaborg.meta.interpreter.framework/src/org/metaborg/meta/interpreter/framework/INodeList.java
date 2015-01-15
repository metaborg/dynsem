package org.metaborg.meta.interpreter.framework;


public interface INodeList<T> extends Iterable<T>, IConvertibleToStrategoTerm {

	public T head();

	public void replaceHead(T newHead);
	
	public INodeList<T> tail();

	public int size();

	public boolean isEmpty();
	
}