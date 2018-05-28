package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.CompilerDirectives.ValueType;

@ValueType
public final class ScopeIdentifier {

	public final String name;
	public final String resource;

	public ScopeIdentifier(String resource, String name) {
		this.name = name;
		this.resource = resource;
	}

	public static final ScopeIdentifier create(IStrategoTerm t) {
		assert Tools.isTermAppl(t);
		IStrategoAppl identTerm = (IStrategoAppl) t;
		assert Tools.hasConstructor(identTerm, "Scope", 2);
		return new ScopeIdentifier(Tools.javaStringAt(identTerm, 0), Tools.javaStringAt(identTerm, 1));
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
	private static int computeHashCode(ScopeIdentifier si) {
		return new HashCodeBuilder().append(si.name).append(si.resource).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj instanceof ScopeIdentifier && this.hashCode() == obj.hashCode());
	}

	@Override
	public String toString() {
		return new StringBuilder().append("Scope(").append(resource).append(", ").append(name).append(")").toString();
	}

}
