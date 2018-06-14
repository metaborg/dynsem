package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.layouts.ScopeEntryLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

public abstract class InitProtoFrame extends DynSemNode {

	@Child private CreateProtoFrame protoFrameFactory;

	public InitProtoFrame(SourceSection source) {
		super(source);
		this.protoFrameFactory = new CreateProtoFrame(source);
	}

	public abstract void execute(VirtualFrame frame, Object scopeEntry);
	
	@Specialization(guards = { "isScopeEntry(scopeEntry)" })
	public void executeScopeEntry(VirtualFrame frame, DynamicObject scopeEntry) {
		DynSemContext ctx = getContext();
		DynamicObject protoFrame = protoFrameFactory.execute(frame, scopeEntry);
		assert FrameLayoutImpl.INSTANCE.isFrame(protoFrame);
		assert FrameLayoutImpl.INSTANCE.getScope(protoFrame) == ScopeEntryLayoutImpl.INSTANCE.getIdentifier(scopeEntry);
		ctx.addProtoFrame(ScopeEntryLayoutImpl.INSTANCE.getIdentifier(scopeEntry), protoFrame);
	}

	protected static boolean isScopeEntry(DynamicObject scopeEntry) {
		return ScopeEntryLayoutImpl.INSTANCE.isScopeEntry(scopeEntry);
	}

	public static InitProtoFrame create(SourceSection source) {
		return FrameNodeFactories.createInitProtoFrame(source);
	}

}
