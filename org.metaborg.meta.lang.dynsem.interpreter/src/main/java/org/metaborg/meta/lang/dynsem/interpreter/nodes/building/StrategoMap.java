package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import java.io.IOException;
import java.util.Iterator;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.StrategoTerm;
import org.spoofax.terms.util.NotImplementedException;

import com.github.krukow.clj_ds.PersistentMap;

public class StrategoMap<K, V> extends StrategoTerm implements IStrategoMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6666976545986918426L;

	public static <K, V> StrategoMap<K, V> create(PersistentMap<K, V> map,
			ITermFactory factory) {
		return new StrategoMap<K, V>(map, factory.getDefaultStorageType());
	}

	private final PersistentMap<K, V> map;

	protected StrategoMap(PersistentMap<K, V> map, int storageType) {
		super(storageType);
		this.map = map;
	}

	public PersistentMap<K, V> getMap() {
		return map;
	}

	@Override
	public IStrategoTerm[] getAllSubterms() {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public IStrategoTerm getSubterm(int arg0) {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public int getSubtermCount() {
		return 0;
	}

	@Override
	public int getTermType() {
		return IStrategoMap.MAP;
	}

	@Override
	public void prettyPrint(ITermPrinter arg0) {
		throw new NotImplementedException();
	}

	@Override
	public void writeAsString(Appendable arg0, int arg1) throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public Iterator<IStrategoTerm> iterator() {
		throw new NotImplementedException();
	}

	@Override
	protected boolean doSlowMatch(IStrategoTerm arg0, int arg1) {
		throw new NotImplementedException();
	}

	@Override
	protected int hashFunction() {
		throw new NotImplementedException();
	}

}
