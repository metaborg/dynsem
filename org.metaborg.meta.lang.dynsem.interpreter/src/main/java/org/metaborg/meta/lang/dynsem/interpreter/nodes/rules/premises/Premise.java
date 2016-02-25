package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Premise extends Node {

	public Premise(SourceSection source) {
		super(source);
	}

	public abstract void execute(VirtualFrame frame);

	public static Premise create(IStrategoAppl t,
			FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "Formula", 1) || Tools.hasConstructor(t, "MergePoint", 3);
		if(Tools.hasConstructor(t, "MergePoint", 3)){
			return MergePointPremise.create(t, fd);
		}
		IStrategoAppl premT = Tools.applAt(t, 0);
		if(Tools.hasConstructor(premT, "Relation", 4)){
			return ReductionPremise.create(premT, fd);
		}
		if(Tools.hasConstructor(premT, "Match", 2)){
			return MatchPremise.create(premT, fd);
		}
		if(Tools.hasConstructor(premT, "TermEq", 2)){
			return TermEqPremise.create(premT, fd);
		}
		
		throw new NotImplementedException("Unsupported premise: " + t);
	}
}
