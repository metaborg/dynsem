package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameEdgeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameImportIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.DNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.PathStep;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ALabel;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeGraphLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITermInit;

import com.google.common.collect.ImmutableList;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class), @NodeChild(value = "ref", type = TermBuild.class) })
public abstract class ResolveAndLookup extends NativeOpBuild {

	// @Child private TermBuild declsOfScopeNode;

	public ResolveAndLookup(SourceSection source) {
		super(source);
		// this.declsOfScopeNode = DeclsOfScope.create(source, null);
	}

	protected static final String arrowName = "";

	@Specialization
	public FrameAddr resolveAndLookup(DynamicObject frm, Occurrence ref,
			@Cached("create(getSourceSection(), arrowName)") DispatchNode labelOrderDispatch,
			@Cached("create(getSourceSection(), arrowName)") DispatchNode wfDispatch,
			@Cached("getOrderTermClass()") Class<?> orderClass,
			@Cached("getWellFormednessTermClass()") Class<?> wfClass,
			@Cached("createLabelListConstructor()") ITermInit labelListInit) {
		FrameLayout frameLayout = FrameLayoutImpl.INSTANCE;
		ScopeGraphLayout sgLayout = ScopeGraphLayoutImpl.INSTANCE;
		ScopeEntryLayout scopeEntryLayout = ScopeEntryLayoutImpl.INSTANCE;

		DynamicObject SG = NaBL2LayoutImpl.INSTANCE.getScopeGraph(getContext().getNaBL2Solution());
		DynamicObject SCOPES = sgLayout.getScopes(SG);
		DynamicObject REFS = sgLayout.getReferences(SG);

		/* we're going to do a bit of assertion checking */

		ScopeIdentifier[] ref_scopes = (ScopeIdentifier[]) REFS.get(ref);
		assert ref_scopes != null && ref_scopes.length == 1 : "Unknown reference or reference lives in multiple scopes";
		assert frameLayout.getScope(frm).equals(ref_scopes[0]) : "Reference does not live in the current frame's scope";

		/* let's begin resolution. the point it to build a path from the current frame's scope to the declaration */
		PathStep path = null;
		List<ALabel> labelsFollowed = new ArrayList<>();

		Stack<ScopeIdentifier> nextScopes = new Stack<>();
		nextScopes.push(frameLayout.getScope(frm));
		boolean found = false;
		while (!found && !nextScopes.isEmpty()) {
			ScopeIdentifier currentScope = nextScopes.pop();
			DynamicObject currentScopeEntry = (DynamicObject) SCOPES.get(currentScope);

			// search for declaration in current scope
			Occurrence dec = findMatchingDeclaration(scopeEntryLayout.getDeclarations(currentScopeEntry), ref);
			if (dec != null) {
				// we have found the matching declaration
				path = growPath(path, DNodeGen.create(currentScope, dec));
				found = true;
			} else {
				// declaration is not in this scope,
			}
		}

		return null;
	}

	private ImmutableList<PathData> resolve(DynamicObject frm, Occurrence ref) {
		FrameLayout frameLayout = FrameLayoutImpl.INSTANCE;
		ScopeGraphLayout sgLayout = ScopeGraphLayoutImpl.INSTANCE;
		ScopeEntryLayout scopeEntryLayout = ScopeEntryLayoutImpl.INSTANCE;

		DynamicObject SG = NaBL2LayoutImpl.INSTANCE.getScopeGraph(getContext().getNaBL2Solution());
		DynamicObject SCOPES = sgLayout.getScopes(SG);
		DynamicObject REFS = sgLayout.getReferences(SG);

		/* we're going to do a bit of assertion checking */

		ScopeIdentifier[] ref_scopes = (ScopeIdentifier[]) REFS.get(ref);
		assert ref_scopes != null && ref_scopes.length == 1 : "Unknown reference or reference lives in multiple scopes";
		assert frameLayout.getScope(frm).equals(ref_scopes[0]) : "Reference does not live in the current frame's scope";

		Stack<CandidateScope> nextScopes = new Stack<>();
		nextScopes.push(new CandidateScope(frameLayout.getScope(frm), ImmutableList.of()));
		while (!nextScopes.isEmpty()) {
			CandidateScope currentCandidate = nextScopes.pop();
			ImmutableList<PathData> pathToCandidate = currentCandidate.pathToHere;
			ScopeIdentifier currentScope = currentCandidate.scopeIdent;
			DynamicObject currentScopeEntry = (DynamicObject) SCOPES.get(currentScope);

			// search for declaration in current scope
			Occurrence dec = findMatchingDeclaration(scopeEntryLayout.getDeclarations(currentScopeEntry), ref);
			if (dec != null) {
				return new ImmutableList.Builder<PathData>().addAll(pathToCandidate).add(new D(currentScope, dec))
						.build();
			} else {
				List<CandidateScope> moreCandidates = new ArrayList<>();
				// declaration is not in this scope, we have to determine new candidates and push them onto the stack
				DynamicObject outEdges = scopeEntryLayout.getEdges(currentScopeEntry);
				for (Property edgeProp : outEdges.getShape().getProperties()) {
					ALabel edgeLabel = (ALabel) edgeProp.getKey();
					if (mayFollowEdge(pathToCandidate, edgeLabel)) {
						// we may follow this edge
						ScopeIdentifier[] targetScopes = (ScopeIdentifier[]) edgeProp.get(outEdges,
								outEdges.getShape());
						for (ScopeIdentifier targetScope : targetScopes) {
							moreCandidates.add(new CandidateScope(targetScope, new ImmutableList.Builder<PathData>()
									.addAll(pathToCandidate).add(new E(currentScope, edgeLabel, targetScope)).build()));
						}
					}
				}

				DynamicObject outImports = scopeEntryLayout.getImports(currentScopeEntry);
				for (Property importProp : outImports.getShape().getProperties()) {
					ALabel importLabel = (ALabel) importProp.getKey();
					if (mayFollowEdge(pathToCandidate, importLabel)) {
						Occurrence[] viaRefs = (Occurrence[]) importProp.get(outImports, outImports.getShape());
						for (Occurrence viaRef : viaRefs) {
							moreCandidates.add(new CandidateScope(null, new ImmutableList.Builder<PathData>()
									.addAll(pathToCandidate).add(new N(currentScope, importLabel, viaRef)).build()));
						}
					}
				}
				
				// sort the new candidates
				/*
				 * FIXME: i think the resolution should prefer paths with a global view. So that if now we add a I()
				 * candidate and there was a P() candidate on the stack, that should be preferred. In that case simply
				 * sorting just the new candidates is incorrect. Below we do this with sorting globally
				 */
				moreCandidates.addAll(nextScopes);
				Collections.sort(moreCandidates, new CandidateScopeComparator());
				nextScopes.clear();
				nextScopes.addAll(moreCandidates);
			}
		}

		return null;
	}

	private static boolean mayFollowEdge(ImmutableList<PathData> pathToHere, ALabel nextLabel) {
		// TODO
		return false;
	}

	private static Occurrence findMatchingDeclaration(Occurrence[] decs, Occurrence ref) {
		for (Occurrence dec : decs) {
			if (dec.namespace() == ref.namespace() && dec.name() == ref.name()) {
				return dec;
			}
		}
		return null;
	}

	private static PathStep growPath(PathStep path, PathStep newStep) {
		if (path == null) {
			return newStep;
		} else {
			path.setNext(newStep);
			return path;
		}
	}

	private class CandidateScopeComparator implements Comparator<CandidateScope> {

		@Override
		public int compare(CandidateScope o1, CandidateScope o2) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	private class CandidateScope {
		protected final ScopeIdentifier scopeIdent;
		protected final ImmutableList<PathData> pathToHere;

		public CandidateScope(ScopeIdentifier scopeIdent, ImmutableList<PathData> pathToHere) {
			this.scopeIdent = scopeIdent;
			this.pathToHere = pathToHere;
		}
	}

	private abstract class PathData {
		protected final ScopeIdentifier scopeIdent;

		public PathData(ScopeIdentifier scopeIdent) {
			this.scopeIdent = scopeIdent;
		}
	}

	private class D extends PathData {
		protected final Occurrence dec;

		public D(ScopeIdentifier scopeId, Occurrence dec) {
			super(scopeId);
			this.dec = dec;
		}
	}

	private class E extends PathData {
		protected final ALabel edgeLabel;
		protected final FrameEdgeIdentifier linkIdent;

		public E(ScopeIdentifier scopeIdent, ALabel edgeLabel, ScopeIdentifier toScopeIdent) {
			super(scopeIdent);
			this.edgeLabel = edgeLabel;
			this.linkIdent = new FrameEdgeIdentifier(edgeLabel, toScopeIdent);
		}
	}

	private class N extends PathData {
		protected final ALabel importLabel;
		protected final Occurrence importRef;
		protected final FrameImportIdentifier linkIdent;

		public N(ScopeIdentifier scopeIdent, ALabel importLabel, Occurrence importRef) {
			super(scopeIdent);
			this.importLabel = importLabel;
			this.importRef = importRef;
			this.linkIdent = new FrameImportIdentifier(importLabel, importRef);
		}
	}

	// private static Occurrence[] filterByNamespace(Occurrence[] occurrences, String namespace) {
	// List<Occurrence> matching = new LinkedList<>();
	// for (Occurrence occ : occurrences) {
	// if (occ.namespace() == namespace) { // ref comparison is ok due to interning
	// matching.add(occ);
	// }
	// }
	//
	// return matching.toArray(new Occurrence[matching.size()]);
	// }

	// private static Occurrence[] filter

	protected Class<?> getOrderTermClass() {
		return getContext().getTermRegistry().getConstructorClass("order", 2);
	}

	protected Class<?> getWellFormednessTermClass() {
		return getContext().getTermRegistry().getConstructorClass("wf", 1);
	}

	protected ITermInit createLabelListConstructor() {
		CompilerAsserts.neverPartOfCompilation();
		ITermRegistry registry = getContext().getTermRegistry();
		Class<?> listClass = registry.getListClass(ALabel.class);
		return registry.lookupClassConstructorWrapper(listClass);
	}

	public static ResolveAndLookup create(SourceSection source, TermBuild frm, TermBuild ref) {
		return ResolveAndLookupNodeGen.create(source, frm, ref);
	}

}
