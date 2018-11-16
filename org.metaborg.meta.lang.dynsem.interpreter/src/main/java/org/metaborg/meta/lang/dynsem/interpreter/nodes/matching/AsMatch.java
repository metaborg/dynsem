package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class AsMatch extends MatchPattern {

	@Child private SlotBind varNode;
	@Child private MatchPattern patternNode;

	public AsMatch(SourceSection source, SlotBind varNode, MatchPattern patternNode) {
		super(source);
		this.varNode = varNode;
		this.patternNode = patternNode;
	}


	@Specialization
	public void doMatch(VirtualFrame frame, Object t) {
		patternNode.executeMatch(frame, t);
		varNode.executeMatch(frame, t);
	}

}
