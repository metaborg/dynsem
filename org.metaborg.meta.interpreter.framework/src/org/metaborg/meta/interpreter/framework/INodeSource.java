package org.metaborg.meta.interpreter.framework;

public interface INodeSource {

	public int getLine();

	public int getColumn();

	public String getCodeFragment();

	public String getFilename();
}
