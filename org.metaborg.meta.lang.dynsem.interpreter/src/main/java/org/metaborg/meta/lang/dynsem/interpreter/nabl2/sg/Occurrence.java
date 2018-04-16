package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public final class Occurrence {

	private final String namespace;
	private final String name;
	private final TermIndex index;

	public Occurrence(String namespace, String name, TermIndex index) {
		this.namespace = namespace;
		this.name = name;
		this.index = index;
	}

	public static Occurrence create(IStrategoTerm t) {
		assert Tools.isTermAppl(t);
		IStrategoAppl occT = (IStrategoAppl) t;
		assert Tools.hasConstructor(occT, "Occurrence", 3);
		String namespace = Tools.javaStringAt(Tools.applAt(occT, 0), 0);
		String name = Tools.javaStringAt(occT, 1);
		TermIndex index = TermIndex.create(Tools.applAt(occT, 2));
		return new Occurrence(namespace, name, index);
	}

	@CompilationFinal private int hashcode = -1;

	@Override
	public int hashCode() {
		if (hashcode == -1) {
			CompilerDirectives.transferToInterpreter();
			hashcode = computeHashCode(this);
		}
		return hashcode;
	}

	@TruffleBoundary
	private static int computeHashCode(Occurrence ident) {
		return new HashCodeBuilder().append(ident.namespace).append(ident.name).append(ident.index).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Occurrence && this.hashCode() == obj.hashCode();
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return new StringBuilder().append("Occurrence(Namespace(").append(namespace).append("), ").append(name)
				.append(", ").append(index).append(")").toString();
	}

}
