package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

public abstract class PathStep {

	private final ScopeIdentifier scope;

	public PathStep(ScopeIdentifier scope) {
		this.scope = scope;
	}

	public static PathStep create(IStrategoAppl stepTerm) {
		if (Tools.hasConstructor(stepTerm, "D")) {
			return D.create(stepTerm);
		}
		if (Tools.hasConstructor(stepTerm, "E")) {
			return E.create(stepTerm);
		}
		if (Tools.hasConstructor(stepTerm, "N")) {
			return N.create(stepTerm);
		}
		throw new IllegalStateException("Unsupported path step: " + stepTerm);
	}

	public static PathStep[] createPath(IStrategoList stepsTerm) {
		PathStep[] path = new PathStep[stepsTerm.size()];
		for (int i = 0; i < path.length; i++) {
			path[i] = create(Tools.applAt(stepsTerm, i));
		}
		return path;
	}

	public static class D extends PathStep {
		private final Occurrence decl;

		public D(ScopeIdentifier scope, Occurrence decl) {
			super(scope);
			this.decl = decl;
		}

		public static D create(IStrategoAppl dTerm) {
			assert Tools.hasConstructor(dTerm, "D", 2);
			return new D(ScopeIdentifier.create(Tools.applAt(dTerm, 0)), Occurrence.create(Tools.applAt(dTerm, 1)));
		}

	}

	public static class E extends PathStep {
		private final Label edgeLabel;

		public E(ScopeIdentifier scope, Label edgeLabel) {
			super(scope);
			this.edgeLabel = edgeLabel;
		}

		public static E create(IStrategoAppl eTerm) {
			assert Tools.hasConstructor(eTerm, "E", 2);
			return new E(ScopeIdentifier.create(Tools.applAt(eTerm, 0)), Label.create(Tools.applAt(eTerm, 1)));
		}

	}

	public static class N extends PathStep {
		private final Label importLabel;
		private final Occurrence importRef;
		private final Path path;

		public N(ScopeIdentifier scope, Label importLabel, Occurrence importRef, Path path) {
			super(scope);
			this.importLabel = importLabel;
			this.importRef = importRef;
			this.path = path;
		}

		public static N create(IStrategoAppl nTerm) {
			assert Tools.hasConstructor(nTerm, "N", 4);
			return new N(ScopeIdentifier.create(Tools.applAt(nTerm, 0)), Label.create(Tools.applAt(nTerm, 1)),
					Occurrence.create(Tools.applAt(nTerm, 2)), new Path(PathStep.createPath(Tools.listAt(nTerm, 3))));
		}

	}
}
