package org.metaborg.meta.interpreter.framework;

import java.util.NoSuchElementException;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class NIL implements INodeList {

	public INodeSource source;

	public NIL(INodeSource source) {
		this.source = source;
	}

	@Override
	public IStrategoTerm toStrategoTerm(ITermFactory factory) {
		return factory.makeList();
	}

	@Override
	public Object head() {
		throw new NoSuchElementException();
	}

	@Override
	public void replaceHead(Object newHead) {
		throw new NoSuchElementException();
	}

	@Override
	public INodeList tail() {
		throw new NoSuchElementException();
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public NIL fromStrategoTerm(IStrategoTerm list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSourceInfo(INodeSource source) {
		this.source = source;
	}

	@Override
	public INodeSource getSourceInfo() {
		return source;
	}

}
