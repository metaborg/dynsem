package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.LayoutUtils;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@Deprecated
@NodeChildren({ @NodeChild(value = "scopeId", type = ScopeIdentifierOfTerm.class) })
public abstract class GetBodyScopeOfTermNode extends DynSemNode {

	public GetBodyScopeOfTermNode(SourceSection source) {
		super(source);
	}

	public abstract DynamicObject execute(VirtualFrame frame);

	@Specialization(guards = { "sid == sid_cached" })
	public DynamicObject executeCached(ScopeIdentifier sid, @Cached("sid") ScopeIdentifier sid_cached,
			@Cached("getContext().getNaBL2()") DynamicObject nabl2_cached,
			@Cached("getScopeEntry(nabl2_cached, sid_cached)") DynamicObject scope_cached) {
		return scope_cached;
	}

	@Specialization(replaces = "executeCached")
	public DynamicObject executeUncached(ScopeIdentifier sid) {
		return getScopeEntry(getContext().getNaBL2Solution(), sid);
	}

	protected DynamicObject getScopeEntry(DynamicObject nabl2, ScopeIdentifier scopeIdent) {
		DynamicObject sg = NaBL2LayoutImpl.INSTANCE.getScopeGraph(nabl2);
		DynamicObject scopes = ScopeGraphLayoutImpl.INSTANCE.getScopes(sg);
		Object scope = scopes.get(scopeIdent);
		assert ScopeEntryLayoutImpl.INSTANCE.isScopeEntry(scope);
		return LayoutUtils.getScopeEntryLayout().getType().cast(scope);
	}

}
