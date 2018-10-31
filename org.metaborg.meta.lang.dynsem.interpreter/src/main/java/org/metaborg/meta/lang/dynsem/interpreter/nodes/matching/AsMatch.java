package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
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

	public static AsMatch create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "As", 2);
		return AsMatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t),
				SlotBind.create(Tools.applAt(t, 0), fd),
				MatchPattern.create(Tools.applAt(t, 1), fd));

	}
}
