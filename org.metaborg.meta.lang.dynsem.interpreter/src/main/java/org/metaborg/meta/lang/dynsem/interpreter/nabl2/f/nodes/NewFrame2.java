package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "scope", type = TermBuild.class) })
public abstract class NewFrame2 extends NativeOpBuild {

	public NewFrame2(SourceSection source) {
		super(source);
	}

	@Override
	public abstract DynamicObject executeGeneric(VirtualFrame frame);

	@Specialization(guards = { "scopeId_cached.equals(scopeId)" }, limit = "20")
	public DynamicObject doNewFrameCached(ScopeIdentifier scopeId, @Cached("scopeId") ScopeIdentifier scopeId_cached,
			@Cached("createFrameCloner()") CloneFrame cloner,
			@Cached("getContext().getProtoFrame(scopeId_cached)") DynamicObject protoFrame_cached) {
		return cloner.executeWithEvaluatedFrame(protoFrame_cached);
	}

	@Specialization
	public DynamicObject doNewFrame(ScopeIdentifier scopeId, @Cached("createFrameCloner()") CloneFrame cloner) {
		return cloner.executeWithEvaluatedFrame(getContext().getProtoFrame(scopeId));
	}

	protected CloneFrame createFrameCloner() {
		CompilerAsserts.neverPartOfCompilation();
		return CloneFrameNodeGen.create(getSourceSection(), null);
	}

	public static NewFrame2 create(SourceSection source, TermBuild t) {
		return FrameNodeFactories.createNewFrame2(source, t);
	}

}
