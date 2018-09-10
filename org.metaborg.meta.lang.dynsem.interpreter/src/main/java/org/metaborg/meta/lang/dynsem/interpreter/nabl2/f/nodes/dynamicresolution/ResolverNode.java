package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.dynamicresolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameEdgeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameImportIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeGraphLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ResolverNode extends DynSemNode {

	@Child private IsReversedOrder isReversedOrder;
	@Child private IsWellFormedPathNode isWF;

	public ResolverNode(SourceSection source) {
		super(source);
		this.isReversedOrder = IsReversedOrderNodeGen.create(source);
		this.isWF = IsWellFormedPathNodeGen.create(source);
	}

	public abstract ReversedResolutionPath execute(DynamicObject frm, Occurrence ref);

	private final Comparator<Candidate> candidateComparator = new Comparator<ResolverNode.Candidate>() {

		@Override
		public int compare(Candidate o1, Candidate o2) {
			ALabel l1 = o1.pathToHere.label();
			ALabel l2 = o2.pathToHere.label();
			if(l1 == l2) {
				return 0;
			}
			return isReversedOrder.execute(l1, l2) ? 1 : -1;
		}

	};

	@Specialization
	public ReversedResolutionPath doResolve(DynamicObject frm, Occurrence ref) {
		final FrameLayout frameLayout = FrameLayoutImpl.INSTANCE;
		final ScopeGraphLayout sgLayout = ScopeGraphLayoutImpl.INSTANCE;
		final ScopeEntryLayout scopeLayout = ScopeEntryLayoutImpl.INSTANCE;

		final DynamicObject SG = NaBL2LayoutImpl.INSTANCE.getScopeGraph(getContext().getNaBL2Solution());
		final DynamicObject SCOPES = sgLayout.getScopes(SG);

		final List<Candidate> candidateQueue = new ArrayList<>();
		candidateQueue.add(new Candidate(frm, null));
		while (!candidateQueue.isEmpty()) {
			Candidate candidate = candidateQueue.remove(0);
			ScopeIdentifier scopeId = frameLayout.getScope(candidate.frm);
			DynamicObject scope = (DynamicObject) SCOPES.get(scopeId);

			Occurrence dec = getMatchingDeclaration(scopeLayout.getDeclarations(scope), ref);
			if (dec != null) {
				return new ReversedResolutionPath.D(scopeId, dec, candidate.pathToHere);
			} else {
				// target declaration is not in this scope
				// we're going to add linked scopes to queue
				boolean grownQueue = false;

				// queue scopes behind direct edges
				DynamicObject outEdges = scopeLayout.getEdges(scope);
				for (Property edgeProp : outEdges.getShape().getProperties()) {
					ALabel label = (ALabel) edgeProp.getKey();
					if (isWF.execute(candidate.pathToHere, label)) {
						ScopeIdentifier[] nextScopeIds = (ScopeIdentifier[]) edgeProp.get(outEdges, true);
						for (ScopeIdentifier nextScopeId : nextScopeIds) {
							DynamicObject nextFrame = (DynamicObject) candidate.frm
									.get(new FrameEdgeIdentifier(label, nextScopeId));
							candidateQueue.add(new Candidate(nextFrame,
									new ReversedResolutionPath.E(scopeId, label, nextScopeId, candidate.pathToHere)));
							grownQueue = true;
						}
					}
				}

				// queue scopes behind occurrences
				DynamicObject importEdges = scopeLayout.getImports(scope);
				for (Property importProp : importEdges.getShape().getProperties()) {
					ALabel label = (ALabel) importProp.getKey();
					if (isWF.execute(candidate.pathToHere, label)) {
						Occurrence[] viaRefs = (Occurrence[]) importProp.get(importEdges, true);
						for (Occurrence viaRef : viaRefs) {
							DynamicObject nextFrame = (DynamicObject) candidate.frm
									.get(new FrameImportIdentifier(label, viaRef));
							candidateQueue.add(new Candidate(nextFrame,
									new ReversedResolutionPath.N(scopeId, label, viaRef, candidate.pathToHere)));
							grownQueue = true;
						}
					}
				}

				if (grownQueue) {
					candidateQueue.sort(candidateComparator);
				}
			}

		}
		throw new ReductionFailure("Unresolved reference " + ref + " in scope " + frameLayout.getScope(frm),
				InterpreterUtils.createStacktrace(), this);
	}

	private static Occurrence getMatchingDeclaration(Occurrence[] decs, Occurrence ref) {
		for (Occurrence dec : decs) {
			if (dec.namespace() == ref.namespace() && dec.name() == ref.name()) {
				return dec;
			}
		}
		return null;
	}

	private class Candidate {
		public final DynamicObject frm;
		public final ReversedResolutionPath pathToHere;

		public Candidate(DynamicObject frm, ReversedResolutionPath pathToHere) {
			this.frm = frm;
			this.pathToHere = pathToHere;
		}
	}
}
