package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public final class FrameEdgeIdentifier extends FrameLinkIdentifier {

	private final ScopeIdentifier toScope;

	public FrameEdgeIdentifier(ALabel linkLabel, ScopeIdentifier toScope) {
		super(linkLabel);
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
