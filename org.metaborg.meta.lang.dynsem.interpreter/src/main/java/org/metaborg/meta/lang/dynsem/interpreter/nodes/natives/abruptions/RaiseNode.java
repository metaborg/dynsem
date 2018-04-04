package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DynSemRuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class RaiseNode extends DynSemRuleNode {

	@Children private final TermBuild[] rwCompBuildNodes;

	@Child private TermBuild thrownBuildNode;

	public RaiseNode(SourceSection source, TermBuild[] rwCompBuildNodes, TermBuild thrownBuildNode) {
		super(source);
		this.rwCompBuildNodes = rwCompBuildNodes;
		this.thrownBuildNode = thrownBuildNode;
	}

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		Object thrownT = thrownBuildNode.executeGeneric(frame);
		Object[] rwCompsT = new Object[rwCompBuildNodes.length];
		for (int i = 0; i < rwCompBuildNodes.length; i++) {
			rwCompsT[i] = rwCompBuildNodes[i].executeGeneric(frame);
		}
		throw new AbortedEvaluationException(thrownT, rwCompsT);
	}

	public static RaiseNode create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Raise", 2);
		IStrategoList rwCompTerms = Tools.listAt(t, 0);
		TermBuild[] rwCompBuildNodes = new TermBuild[rwCompTerms.size()];
		for (int i = 0; i < rwCompBuildNodes.length; i++) {
			rwCompBuildNodes[i] = TermBuild.create(Tools.applAt(rwCompTerms, i), fd);
		}
		TermBuild thrownBuldNode = TermBuild.create(Tools.applAt(t, 1), fd);
		return new RaiseNode(SourceUtils.dynsemSourceSectionFromATerm(t), rwCompBuildNodes, thrownBuldNode);
	}

}
