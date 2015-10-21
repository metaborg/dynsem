package org.metaborg.meta.interpreter.framework;

import java.util.NoSuchElementException;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.ITermFactory;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public class NIL extends Node implements IList<Object> {

	public NIL(SourceSection src) {
		super(src);
	}

	@Override
	public IStrategoList toStrategoTerm(ITermFactory factory) {
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
	public IList<Object> tail() {
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
	public String toString() {
		return "[]";
	}

}
