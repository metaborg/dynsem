package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ReflectiveHandlerBuild extends DynSemNode {

	private static final String HANDLER_CTOR_NAME = "handler";
	private static final int HANDLER_CTOR_ARITY = 2;

	public ReflectiveHandlerBuild(SourceSection source) {
		super(source);
	}

	public abstract Object execute(VirtualFrame frame, Object thrown, Object catching);

	@Specialization
	public Object executeCached(VirtualFrame frame, Object thrown, Object catching,
			@Cached("getHandlerBuildNode()") TermBuild buildNode) {
		return buildNode.executeEvaluated(frame, thrown, catching);
	}

	protected TermBuild getHandlerBuildNode() {
		ITermRegistry registry = getContext().getTermRegistry();
		return registry.lookupBuildFactory(registry.getConstructorClass(HANDLER_CTOR_NAME, HANDLER_CTOR_ARITY))
				.apply(getSourceSection(), null, null);
	}

}
