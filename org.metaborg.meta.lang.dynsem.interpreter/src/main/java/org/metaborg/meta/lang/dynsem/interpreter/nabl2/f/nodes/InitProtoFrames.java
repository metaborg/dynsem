package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.NaBL2LayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeGraphLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopesLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Property;
import com.oracle.truffle.api.source.SourceSection;

public class InitProtoFrames extends DynSemNode {

	@Child private InitProtoFrame initProtoFrame;

	public InitProtoFrames(SourceSection source) {
		super(source);
		this.initProtoFrame = InitProtoFrameNodeGen.create(source);
	}

	public void execute(VirtualFrame frame) {
		DynSemContext ctx = getContext();
		DynamicObject scopes = ScopeGraphLayoutImpl.INSTANCE
				.getScopes(NaBL2LayoutImpl.INSTANCE.getScopeGraph(ctx.getNaBL2Solution()));
		assert ScopesLayoutImpl.INSTANCE.isScopes(scopes);
		for (Property scopeIdentProp : scopes.getShape().getProperties()) {
			initProtoFrame.execute(frame, scopeIdentProp.get(scopes, true));
		}

	}

}
