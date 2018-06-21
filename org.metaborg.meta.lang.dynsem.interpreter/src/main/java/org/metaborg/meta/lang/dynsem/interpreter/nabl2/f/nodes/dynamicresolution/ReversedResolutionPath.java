package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.dynamicresolution;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameEdgeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;

abstract class ReversedResolutionPath {
	public final ScopeIdentifier scopeIdent;
	public final ReversedResolutionPath previous;

	public ReversedResolutionPath(ScopeIdentifier scopeIdent, ReversedResolutionPath previous) {
		this.scopeIdent = scopeIdent;
		this.previous = previous;
	}

	public abstract ALabel label();

	public int size() {
		return 1 + (previous != null ? previous.size() : 0);
	}

	static class D extends ReversedResolutionPath {
		public final Occurrence dec;

		public D(ScopeIdentifier scopeId, Occurrence dec, ReversedResolutionPath previous) {
			super(scopeId, previous);
			this.dec = dec;
		}

		@Override
		public ALabel label() {
			return org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.D.SINGLETON;
		}

	}

	static class E extends ReversedResolutionPath {
		public final ALabel edgeLabel;
		public final FrameEdgeIdentifier linkIdent;

		public E(ScopeIdentifier scopeIdent, ALabel edgeLabel, ScopeIdentifier toScopeIdent,
				ReversedResolutionPath previous) {
			super(scopeIdent, previous);
			this.edgeLabel = edgeLabel;
			this.linkIdent = new FrameEdgeIdentifier(edgeLabel, toScopeIdent);
		}

		@Override
		public ALabel label() {
			return edgeLabel;
		}

	}

	static class N extends ReversedResolutionPath {
		public final ALabel importLabel;
		public final Occurrence importRef;
		// protected final FrameImportIdentifier linkIdent;

		public N(ScopeIdentifier scopeIdent, ALabel importLabel, Occurrence importRef,
				ReversedResolutionPath previous) {
			super(scopeIdent, previous);
			this.importLabel = importLabel;
			this.importRef = importRef;
			// this.linkIdent = new FrameImportIdentifier(importLabel, importRef);
		}

		@Override
		public ALabel label() {
			return importLabel;
		}
	}
}
