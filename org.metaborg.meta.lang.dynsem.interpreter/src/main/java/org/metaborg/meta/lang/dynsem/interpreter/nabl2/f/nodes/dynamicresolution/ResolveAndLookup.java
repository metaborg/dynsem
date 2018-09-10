package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.dynamicresolution;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameAddr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.DNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.ENodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.NNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.Path;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.lookup.PathStep;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class), @NodeChild(value = "ref", type = TermBuild.class) })
public abstract class ResolveAndLookup extends NativeOpBuild {

	@Child private ResolverNode resolveNode;


	public ResolveAndLookup(SourceSection source) {
		super(source);
		this.resolveNode = ResolverNodeGen.create(source);
	}

	@Specialization(guards = { "scopeOf(frm) == scope_cached", "ref == ref_cached" }, limit = "1000")
	public FrameAddr doCached(DynamicObject frm, Occurrence ref,
			@Cached("scopeOf(frm)") ScopeIdentifier scope_cached, @Cached("ref") Occurrence ref_cached,
			@Cached("doResolve(frm, ref)") ReversedResolutionPath resolutionPath,
			@Cached("mkExecutablePath(resolutionPath)") DirectCallNode pathLookup) {
		return (FrameAddr) pathLookup.call(new Object[] { frm });
	}

	protected ReversedResolutionPath doResolve(DynamicObject frm, Occurrence ref) {
		return resolveNode.execute(frm, ref);
	}

	protected ScopeIdentifier scopeOf(DynamicObject frm) {
		return FrameLayoutImpl.INSTANCE.getScope(frm);
	}

	protected DirectCallNode mkExecutablePath(ReversedResolutionPath drp) {
		assert drp != null;
		PathStep step = null;
		while (drp != null) {
			if (drp instanceof ReversedResolutionPath.D) {
				assert step == null;
				step = DNodeGen.create(drp.scopeIdent, ((ReversedResolutionPath.D) drp).dec);
			} else if (drp instanceof ReversedResolutionPath.E) {
				assert step != null;
				step = ENodeGen.create(drp.scopeIdent, ((ReversedResolutionPath.E) drp).edgeLabel, step);
			} else if (drp instanceof ReversedResolutionPath.N) {
				assert step != null;
				step = NNodeGen.create(drp.scopeIdent, ((ReversedResolutionPath.N) drp).importLabel,
						((ReversedResolutionPath.N) drp).importRef, step);
			} else {
				throw new IllegalStateException("Unknown label path part " + drp);
			}
			drp = drp.previous;
		}

		assert step != null;

		return DirectCallNode.create(new Path(getRootNode().getLanguage(DynSemLanguage.class), step).getCallTarget());
	}

	public static ResolveAndLookup create(SourceSection source, TermBuild frm, TermBuild ref) {
		return ResolveAndLookupNodeGen.create(source, frm, ref);
	}
}
