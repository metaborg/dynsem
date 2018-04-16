package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObjectFactory;
import com.oracle.truffle.api.source.SourceSection;

public abstract class GetFrameFactoryNode extends DynSemNode {

	public GetFrameFactoryNode(SourceSection source) {
		super(source);
	}

	public abstract DynamicObjectFactory execute(VirtualFrame frame, ScopeIdentifier scopeIdent);

	@Specialization(limit = "3", guards = { "scopeIdent == scopeIdent_cached" })
	public DynamicObjectFactory executeCached(ScopeIdentifier scopeIdent,
			@Cached("scopeIdent") ScopeIdentifier scopeIdent_cached,
			@Cached("getFactory(scopeIdent_cached)") DynamicObjectFactory factory_cached) {
		return factory_cached;
	}

	@Specialization(replaces = "executeCached")
	public DynamicObjectFactory executeDirect(ScopeIdentifier scopeIdent) {
		return getFactory(scopeIdent);
	}

	protected DynamicObjectFactory getFactory(ScopeIdentifier scopeIdent) {
		return getContext().getFrameFactory(scopeIdent);
	}

}
