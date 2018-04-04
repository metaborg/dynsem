package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
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

public class ContinueNode extends Rule {

	@Child private TermBuild valBuildnode;

	@Children private final TermBuild[] rwCompNodes;

	public ContinueNode(DynSemLanguage lang, SourceSection source, TermBuild valBuildNode, TermBuild[] rwCompNodes) {
		super(lang, source);
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
		throw new LoopContinueException(val, components);
	}

	public static ContinueNode create(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		// ContinueNode: Term * List(Term) -> NativeRule
		assert Tools.hasConstructor(t, "ContinueNode", 2);

		TermBuild valBuildNode = TermBuild.create(Tools.applAt(t, 0), fd);

		IStrategoList rwCompsT = Tools.listAt(t, 1);
		TermBuild[] rwCompBuilds = new TermBuild[rwCompsT.size()];
		for (int i = 0; i < rwCompBuilds.length; i++) {
			rwCompBuilds[i] = TermBuild.create(Tools.applAt(rwCompsT, i), fd);
		}

		return new ContinueNode(lang, SourceUtils.dynsemSourceSectionFromATerm(t), valBuildNode, rwCompBuilds);
	}

}
