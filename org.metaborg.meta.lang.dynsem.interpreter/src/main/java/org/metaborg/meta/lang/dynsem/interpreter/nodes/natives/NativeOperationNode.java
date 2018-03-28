package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NativeOperationNode extends DynSemNode {

	public NativeOperationNode(SourceSection source) {
		super(source);
	}

	public abstract RuleResult execute(VirtualFrame frame);

}
