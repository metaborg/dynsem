package org.metaborg.meta.interpreter.framework;

import org.spoofax.interpreter.terms.IStrategoTerm;

@Deprecated
public interface INodeSource {

	public int getLine();

	public int getColumn();

	public String getCodeFragment();

	public String getFilename();
	
	public void apply(IStrategoTerm term);

}
