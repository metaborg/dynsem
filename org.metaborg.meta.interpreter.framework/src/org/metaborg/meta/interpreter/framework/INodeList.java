package org.metaborg.meta.interpreter.framework;

public interface INodeList<T> extends INode {

	public T head();

	public void replaceHead(T newHead);
	
	public INodeList<T> tail();

	public int size();

	public boolean isEmpty();

}