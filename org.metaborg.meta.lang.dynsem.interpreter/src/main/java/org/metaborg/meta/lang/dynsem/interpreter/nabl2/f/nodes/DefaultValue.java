package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchUtils;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ApplTerm;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DefaultValue extends DynSemNode {
	private static final String DEFAULT_CTR_NAME = "default";
	private static final int DEFAULT_CTR_ARITY = 1;
	private static final String DEFAULT_SORT = "SimpleSort(default_1_Meta)";

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
	public Object executeDefault(VirtualFrame frame, Object type) {
		ApplTerm defaultTerm = new ApplTerm(DEFAULT_SORT, DEFAULT_CTR_NAME, new Object[] { type });
		return defaultValueDispatchNode.execute(DispatchUtils.dispatchKeyOf(defaultTerm),
				new Object[] { defaultTerm }).result;
	}

}
