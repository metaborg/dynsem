package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public final class FrameImportIdentifier implements FrameLinkIdentifier {
	private final ALabel linkLabel;
	private final Occurrence viaOccurrence;

	public FrameImportIdentifier(ALabel linkLabel, Occurrence viaOccurrence) {
		this.linkLabel = linkLabel;
		this.viaOccurrence = viaOccurrence;
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
	private static int computeHashCode(FrameImportIdentifier fiid) {
		return new HashCodeBuilder().append(fiid.linkLabel).append(fiid.viaOccurrence).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return (this == obj) || (obj instanceof FrameImportIdentifier && this.hashCode() == obj.hashCode());
	}

	@Override
	public String toString() {
		return new StringBuilder().append("--").append(this.linkLabel).append("--(").append(this.viaOccurrence)
				.append(")-->").toString();
	}

}
