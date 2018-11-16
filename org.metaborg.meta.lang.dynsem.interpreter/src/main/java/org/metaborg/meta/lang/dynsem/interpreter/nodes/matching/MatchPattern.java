package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class MatchPattern extends DynSemNode {

	public MatchPattern(SourceSection source) {
		super(source);
	}

	public abstract void executeMatch(VirtualFrame frame, Object term);



	public static MatchPattern[] cloneNodes(MatchPattern[] nodes) {
		final MatchPattern[] clone = new MatchPattern[nodes.length];
		for (int i = 0; i < clone.length; i++) {
			clone[i] = cloneNode(nodes[i]);
		}
		return clone;
	}

	public static MatchPattern cloneNode(MatchPattern node) {
		return null == node ? null : NodeUtil.cloneNode(node);
	}


}
