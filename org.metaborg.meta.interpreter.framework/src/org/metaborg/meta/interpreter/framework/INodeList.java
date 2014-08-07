package org.metaborg.meta.interpreter.framework;

public interface INodeList<T> extends INode, Iterable<T> {

	public T head();

	public void replaceHead(T newHead);
	
	public INodeList<T> tail();

	public int size();

	public boolean isEmpty();

}