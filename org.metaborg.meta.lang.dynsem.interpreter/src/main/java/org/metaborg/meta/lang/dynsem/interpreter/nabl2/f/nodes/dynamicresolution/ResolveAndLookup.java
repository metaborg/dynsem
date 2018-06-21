package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameEdgeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameImportIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayout;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.DNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.ENodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.NNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.Path;
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
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITermInit;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.google.common.collect.ImmutableList;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class), @NodeChild(value = "ref", type = TermBuild.class) })
public abstract class ResolveAndLookup extends NativeOpBuild {

	@Child private DispatchNode labelOrderDispatch;
	@Child private DispatchNode wfDispatch;

	public ResolveAndLookup(SourceSection source) {
		super(source);
		this.labelOrderDispatch = DispatchNode.create(source, "");
		this.wfDispatch = DispatchNode.create(source, "");
	}

	@Specialization(guards = { "ref == ref_cached", "scopeCheck(frm, scope_cached)" }, limit = "1000")
	public FrameAddr resolveAndLookup(DynamicObject frm, Occurrence ref,
			@Cached("scopeOfFrame(frm)") ScopeIdentifier scope_cached,
			@Cached("ref") Occurrence ref_cached,
			@Cached("getOrderTermClass()") Class<?> orderClass,
			@Cached("getWellFormednessTermClass()") Class<?> wfClass,
			@Cached("createLabelListConstructor()") ITermInit labelListInit,
			@Cached("createOrderConConstructor(orderClass)") ITermInit orderConInit,
			@Cached("createWellFormednessConConstructor(wfClass)") ITermInit wfConInit,
			@Cached("resolve(frm, ref, orderClass, wfClass, labelListInit, orderConInit,wfConInit)") DirectCallNode lookupNode) {

		return (FrameAddr) lookupNode.call(new Object[] { frm });

	}

	protected ScopeIdentifier scopeOfFrame(DynamicObject frm) {
		return FrameLayoutImpl.INSTANCE.getScope(frm);
	}

	protected boolean scopeCheck(DynamicObject frm, ScopeIdentifier scope) {
		return scopeOfFrame(frm).equals(scope);
	}

	private DirectCallNode initLookupNode(ImmutableList<PathData> pathData) {
		assert pathData != null;
		PathStep steps = null;
		for (int i = pathData.size() - 1; i >= 0; i--) {
			PathData pd = pathData.get(i);
			if (pd instanceof D) {
				assert steps == null;
				steps = DNodeGen.create(pd.scopeIdent, ((D) pd).dec);
			} else if (pd instanceof E) {
				assert steps != null;
				steps = ENodeGen.create(pd.scopeIdent, ((E) pd).edgeLabel, steps);
			} else if (pd instanceof N) {
				assert steps != null;
				steps = NNodeGen.create(pd.scopeIdent, ((N) pd).importLabel, ((N) pd).importRef, steps);
			}
		}
		assert steps != null;
		return DirectCallNode.create(new Path(getRootNode().getLanguage(DynSemLanguage.class), steps).getCallTarget());
	}

	protected DirectCallNode resolve(DynamicObject frm, Occurrence ref, Class<?> orderClass, Class<?> wfClass,
			ITermInit labelListInit, ITermInit orderConInit, ITermInit wfConInit) {
		FrameLayout frameLayout = FrameLayoutImpl.INSTANCE;
		ScopeGraphLayout sgLayout = ScopeGraphLayoutImpl.INSTANCE;
		ScopeEntryLayout scopeEntryLayout = ScopeEntryLayoutImpl.INSTANCE;

		DynamicObject SG = NaBL2LayoutImpl.INSTANCE.getScopeGraph(getContext().getNaBL2Solution());
		DynamicObject SCOPES = sgLayout.getScopes(SG);

		Stack<CandidateFrame> nextScopes = new Stack<>();
		nextScopes.push(new CandidateFrame(frm, ImmutableList.of()));
		while (!nextScopes.isEmpty()) {
			CandidateFrame currentCandidate = nextScopes.pop();
			ImmutableList<PathData> pathToCandidate = currentCandidate.pathToHere;
			DynamicObject currentFrame = currentCandidate.frm;
			ScopeIdentifier currentScope = frameLayout.getScope(currentFrame);
			DynamicObject currentScopeEntry = (DynamicObject) SCOPES.get(currentScope);

			// search for declaration in current scope
			Occurrence dec = findMatchingDeclaration(scopeEntryLayout.getDeclarations(currentScopeEntry), ref);
			if (dec != null) {
				return initLookupNode(new ImmutableList.Builder<PathData>().addAll(pathToCandidate)
						.add(new D(currentScope, dec)).build());
			} else {
				List<CandidateFrame> moreCandidates = new ArrayList<>();
				// declaration is not in this scope, we have to determine new candidates and push them onto the stack
				DynamicObject outEdges = scopeEntryLayout.getEdges(currentScopeEntry);
				for (Property edgeProp : outEdges.getShape().getProperties()) {
					ALabel edgeLabel = (ALabel) edgeProp.getKey();
					if (mayFollowEdge(pathToCandidate, edgeLabel, wfConInit, labelListInit, wfClass)) {
						// we may follow this edge
						ScopeIdentifier[] targetScopes = (ScopeIdentifier[]) edgeProp.get(outEdges,
								outEdges.getShape());
						for (ScopeIdentifier targetScope : targetScopes) {
						DynamicObject nextFrame = (DynamicObject) currentFrame
								.get(new FrameEdgeIdentifier(edgeLabel, targetScope));
						moreCandidates.add(new CandidateFrame(nextFrame, new ImmutableList.Builder<PathData>()
									.addAll(pathToCandidate).add(new E(currentScope, edgeLabel, targetScope)).build()));
						}
					} else {
						System.out.println("May not follow: " + edgeLabel + " after "
								+ pathDatasToString(pathToCandidate));
					}
				}

				DynamicObject outImports = scopeEntryLayout.getImports(currentScopeEntry);
				for (Property importProp : outImports.getShape().getProperties()) {
					ALabel importLabel = (ALabel) importProp.getKey();
					if (mayFollowEdge(pathToCandidate, importLabel, wfConInit, labelListInit, wfClass)) {
						Occurrence[] viaRefs = (Occurrence[]) importProp.get(outImports, outImports.getShape());
						for (Occurrence viaRef : viaRefs) {
						DynamicObject nextFrame = (DynamicObject) currentFrame
								.get(new FrameImportIdentifier(importLabel, viaRef));
						moreCandidates.add(new CandidateFrame(nextFrame, new ImmutableList.Builder<PathData>()
									.addAll(pathToCandidate).add(new N(currentScope, importLabel, viaRef)).build()));
						}
					} else {
						System.out.println("May not follow: " + importLabel + " after "
								+ pathDatasToString(pathToCandidate));
					}
				}

				// sort all of the candidates (including the existing ones)
				moreCandidates.addAll(nextScopes);
				Collections.sort(moreCandidates, new CandidateScopeComparator(orderConInit, orderClass));
				nextScopes.clear();
				nextScopes.addAll(moreCandidates);
			}
		}

		throw new ReductionFailure("Cannot resolve " + ref + " in scope " + frameLayout.getScope(frm),
				InterpreterUtils.createStacktrace(), this);
	}

	private static String pathDatasToString(Collection<PathData> pds) {
		StringBuilder str = new StringBuilder();
		for (PathData pd : pds) {
			str.append(pd.label());
		}
		return str.toString();
	}

	private boolean mayFollowEdge(ImmutableList<PathData> pathToHere, ALabel nextLabel, ITermInit wfConInit,
			ITermInit labelListInit, Class<?> wfClass) {
		ALabel[] pathLabels = new ALabel[pathToHere.size() + 1];
		for (int i = 0; i < pathToHere.size(); i++) {
			pathLabels[i] = pathToHere.get(i).label();
		}
		pathLabels[pathLabels.length - 1] = nextLabel;
		Object[] args = new Object[] { wfConInit.apply(new Object[] { labelListInit.apply((Object[]) pathLabels) }) };
		RuleResult res = this.wfDispatch.execute(wfClass, args);
		return (boolean) res.result;
	}

	private static Occurrence findMatchingDeclaration(Occurrence[] decs, Occurrence ref) {
		for (Occurrence dec : decs) {
			if (dec.namespace() == ref.namespace() && dec.name() == ref.name()) {
				return dec;
			}
		}
		return null;
	}

	private class CandidateScopeComparator implements Comparator<CandidateFrame> {

		private final ITermInit orderConInit;
		private final Class<?> orderClass;

		public CandidateScopeComparator(ITermInit orderConInit, Class<?> orderClass) {
			super();
			this.orderConInit = orderConInit;
			this.orderClass = orderClass;
		}

		@Override
		public int compare(CandidateFrame o1, CandidateFrame o2) {
			ALabel l1 = o1.pathToHere.get(o1.pathToHere.size() - 1).label();
			ALabel l2 = o2.pathToHere.get(o2.pathToHere.size() - 1).label();
			Object[] args = new Object[] { orderConInit.apply(new Object[] { l1, l2 }) };
			RuleResult res = labelOrderDispatch.execute(orderClass, args);
			boolean swap = (boolean) res.result;
			return swap ? 1 : -1;
		}

	}

	private class CandidateFrame {
		protected final DynamicObject frm;
		protected final ImmutableList<PathData> pathToHere;

		public CandidateFrame(DynamicObject frm, ImmutableList<PathData> pathToHere) {
			this.frm = frm;
			this.pathToHere = pathToHere;
		}
	}

	private abstract class PathData {
		protected final ScopeIdentifier scopeIdent;

		public PathData(ScopeIdentifier scopeIdent) {
			this.scopeIdent = scopeIdent;
		}

		public abstract ALabel label();
	}

	private class D extends PathData {
		protected final Occurrence dec;

		public D(ScopeIdentifier scopeId, Occurrence dec) {
			super(scopeId);
			this.dec = dec;
		}

		@Override
		public ALabel label() {
			return org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.D.SINGLETON;
		}

		@Override
		public String toString() {
			return "D(" + scopeIdent + ", " + dec + ")";
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

		@Override
		public ALabel label() {
			return edgeLabel;
		}

		@TruffleBoundary
		@Override
		public String toString() {
			return "E(" + edgeLabel + ", " + scopeIdent + ")";
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

		@Override
		public ALabel label() {
			return importLabel;
		}

		@TruffleBoundary
		@Override
		public String toString() {
			return "N(" + scopeIdent + ", " + importLabel + ", " + importRef + ")";
		}
	}

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

	protected ITermInit createOrderConConstructor(Class<?> orderClass) {
		CompilerAsserts.neverPartOfCompilation();
		ITermRegistry registry = getContext().getTermRegistry();
		return registry.lookupClassConstructorWrapper(orderClass);
	}

	protected ITermInit createWellFormednessConConstructor(Class<?> wfClass) {
		CompilerAsserts.neverPartOfCompilation();
		ITermRegistry registry = getContext().getTermRegistry();
		return registry.lookupClassConstructorWrapper(wfClass);
	}

	public static ResolveAndLookup create(SourceSection source, TermBuild frm, TermBuild ref) {
		return ResolveAndLookupNodeGen.create(source, frm, ref);
	}

}
