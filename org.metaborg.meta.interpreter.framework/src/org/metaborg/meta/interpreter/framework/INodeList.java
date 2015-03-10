package org.metaborg.meta.interpreter.framework;

public interface INodeList extends IConvertibleToStrategoTerm {

	public Object head();

	public void replaceHead(Object newHead);

	public INodeList tail();

	public int size();

	public boolean isEmpty();

}