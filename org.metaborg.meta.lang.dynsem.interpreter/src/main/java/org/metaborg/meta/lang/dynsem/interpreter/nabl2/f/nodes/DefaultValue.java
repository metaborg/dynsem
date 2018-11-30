package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.calls.DynamicDispatch;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DefaultValue extends DynSemNode {
	protected static final String EMPTY = "";
	private static final String DEFAULT_CTR_NAME = "default";
	private static final int DEFAULT_CTR_ARITY = 1;

	public DefaultValue(SourceSection source) {
		super(source);
	}

	public abstract Object execute(VirtualFrame frame, Object type);

	@Specialization(guards = "type == null")
	public Object doNull(VirtualFrame frame, Object type) {
		return null;
	}

	@Specialization(guards = "type != null")
	public Object doDefault(VirtualFrame frame, Object type, @Cached("getDefaultBuilder()") TermBuild defaultBuilder,
			@Cached("create(getSourceSection(), EMPTY)") DynamicDispatch dispatch) {
		Object defaultTerm = defaultBuilder.executeEvaluated(frame, type);
		return dispatch.execute(new Object[] { defaultTerm }).result;
	}

	protected TermBuild getDefaultBuilder() {
		ITermRegistry termRegistry = getContext().getTermRegistry();
		return termRegistry.lookupBuildFactory(termRegistry.getConstructorClass(DEFAULT_CTR_NAME, DEFAULT_CTR_ARITY))
				.apply(getSourceSection(), new TermBuild[] { null });
	}

	public static DefaultValue create(SourceSection source) {
		return FrameNodeFactories.createDefaultValue(source);
	}
}
