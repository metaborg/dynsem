package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Premise extends DynSemNode {

	public Premise(SourceSection source) {
		super(source);
	}

	public abstract void execute(VirtualFrame frame);

	public static Premise create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		if (Tools.hasConstructor(t, "MergePoint", 3)) {
			return MergePointPremise.create(t, fd);
		}
		if (Tools.hasConstructor(t, "CaseMatch", 2)) {
			return CaseMatchPremise.create(t, fd);
		}
		IStrategoAppl premT = Tools.applAt(t, 0);
		if (Tools.hasConstructor(premT, "Relation", 3)) {
			return RelationPremise.create(premT, fd);
		}
		if (Tools.hasConstructor(premT, "RecRelation", 3)) {
			return RecursiveRelationPremise.create(premT, fd);
		}
		if (Tools.hasConstructor(premT, "Match", 2)) {
			return MatchPremise.create(premT, fd);
		}
		if (Tools.hasConstructor(premT, "TermEq", 2)) {
			return TermEqPremise.create(premT, fd);
		}
		if (Tools.hasConstructor(premT, "Fails", 1)) {
			return FailsPremise.create(premT, fd);
		}

		throw new NotImplementedException("Unsupported premise: " + t);
	}
}
