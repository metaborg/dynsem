package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public final class FrameEdgeIdentifier implements FrameLinkIdentifier {
	private final Label linkLabel;
	private final ScopeIdentifier toScope;

	public FrameEdgeIdentifier(Label linkLabel, ScopeIdentifier toScope) {
		this.linkLabel = linkLabel;
		this.toScope = toScope;
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
	private static int computeHashCode(FrameEdgeIdentifier flid) {
		return new HashCodeBuilder().append(flid.linkLabel).append(flid.toScope).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj instanceof FrameEdgeIdentifier && this.hashCode() == obj.hashCode());
	}

	@Override
	public String toString() {
		return new StringBuilder().append("--").append(this.linkLabel).append("--(").append(this.toScope).append(")-->")
				.toString();
	}

}
