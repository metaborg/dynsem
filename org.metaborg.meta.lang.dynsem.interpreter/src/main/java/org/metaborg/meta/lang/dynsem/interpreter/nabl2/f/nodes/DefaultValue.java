package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DefaultValue extends DynSemNode {
	private static final String DEFAULT_CTR_NAME = "default";
	private static final int DEFAULT_CTR_ARITY = 1;

	@Child private DispatchNode defaultValueDispatchNode;

	public DefaultValue(SourceSection source) {
		super(source);
		this.defaultValueDispatchNode = DispatchNode.create(source, "");
	}

	public abstract Object execute(VirtualFrame frame, Object type);

	@Specialization(guards = { "type == null" })
	public Object executeNull(VirtualFrame frame, Object type) {
		return null;
	}

	@Specialization(guards = { "type != null" })
	public Object executeDefault(VirtualFrame frame, Object type,
			@Cached("getDefaultBuilder()") TermBuild defaultBuilder) {
		Object defaultTerm = defaultBuilder.executeEvaluated(frame, type);
		return defaultValueDispatchNode.execute(defaultTerm.getClass(), new Object[] { defaultTerm }).result;
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
