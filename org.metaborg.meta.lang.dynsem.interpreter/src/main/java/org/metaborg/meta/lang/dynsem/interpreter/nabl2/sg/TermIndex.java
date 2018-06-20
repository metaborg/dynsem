package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.CompilerDirectives.ValueType;

@ValueType
public final class TermIndex {

	private final String resource;
	private final int offset;

	public TermIndex(String resource, int offset) {
		this.resource = resource.intern();
		this.offset = offset;
	}

	public static TermIndex create(IStrategoAppl termIndexT) {
		assert Tools.hasConstructor(termIndexT, "TermIndex", 2);
		return new TermIndex(Tools.javaStringAt(termIndexT, 0), Tools.javaIntAt(termIndexT, 1));
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
	private static int computeHashCode(TermIndex ti) {
		return new HashCodeBuilder().append(ti.resource).append(ti.offset).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj instanceof TermIndex && this.hashCode() == obj.hashCode());
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return new StringBuilder().append("TermIndex(").append(resource).append(", ").append(offset).append(")")
				.toString();
	}

}
