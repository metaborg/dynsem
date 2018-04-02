package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops;

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

public class BreakNode extends DynSemRuleNode {

	@Child private TermBuild valBuildnode;

	@Children private final TermBuild[] rwCompNodes;

	public BreakNode(SourceSection source, TermBuild valBuildNode, TermBuild[] rwCompNodes) {
		super(source);
		this.valBuildnode = valBuildNode;
		this.rwCompNodes = rwCompNodes;
	}

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		Object val = valBuildnode.executeGeneric(frame);

		Object[] components = new Object[rwCompNodes.length];
		for (int i = 0; i < rwCompNodes.length; i++) {
			components[i] = rwCompNodes[i].executeGeneric(frame);
		}
		throw new LoopBreakException(val, components);
	}

	public static BreakNode create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		// BreakNode: Term * List(Term) -> NativeRule
		assert Tools.hasConstructor(t, "BreakNode", 2);

		TermBuild valBuildNode = TermBuild.create(Tools.applAt(t, 0), fd);

		IStrategoList rwCompsT = Tools.listAt(t, 1);
		TermBuild[] rwCompBuilds = new TermBuild[rwCompsT.size()];
		for (int i = 0; i < rwCompBuilds.length; i++) {
			rwCompBuilds[i] = TermBuild.create(Tools.applAt(rwCompsT, i), fd);
		}

		return new BreakNode(SourceUtils.dynsemSourceSectionFromATerm(t), valBuildNode, rwCompBuilds);
	}

}
