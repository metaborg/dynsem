package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

// FIXME implement
public class RecursiveRelationPremise extends RelationPremise {

	public RecursiveRelationPremise(RelationPremiseInputBuilder inputBuilderNode, RelationDispatch dispatchNode,
			MatchPattern rhsNode, MatchPattern[] rhsComponentNodes, SourceSection source) {
		super(inputBuilderNode, dispatchNode, rhsNode, rhsComponentNodes, source);
	}

	public static RelationPremise create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "RecRelation", 3);

		IStrategoAppl targetT = Tools.applAt(t, 2);
		assert Tools.hasConstructor(targetT, "Target", 2);
		MatchPattern rhsNode = MatchPattern.create(Tools.applAt(targetT, 0), fd);

		IStrategoList rhsRwsT = Tools.listAt(targetT, 1);
		MatchPattern[] rhsRwNodes = new MatchPattern[rhsRwsT.size()];
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i] = MatchPattern.createFromLabelComp(Tools.applAt(rhsRwsT, i), fd);
		}
		;
		return new RecursiveRelationPremise(RelationPremiseInputBuilder.create(Tools.applAt(t, 0), fd),
				RelationDispatch.create(Tools.applAt(t, 0), Tools.applAt(t, 1), fd), rhsNode, rhsRwNodes,
				SourceSectionUtil.fromStrategoTerm(t));
	}

}
