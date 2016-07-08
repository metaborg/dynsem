package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuildCacheNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

// TODO experiment with dissolving this node into its parent
public class RelationPremiseInputBuilder extends TermBuild {

	@Child protected TermBuild termNode;
	@Children protected final TermBuild[] componentNodes;

	public RelationPremiseInputBuilder(TermBuild termNode, TermBuild[] componentNodes, SourceSection source) {
		super(source);
		this.termNode = DynSemContext.LANGUAGE.isTermCachingEnabled() ? TermBuildCacheNode.create(termNode) : termNode;
		for (int i = 0; i < componentNodes.length; i++) {
			componentNodes[i] = DynSemContext.LANGUAGE.isTermCachingEnabled()
					? TermBuildCacheNode.create(componentNodes[i]) : componentNodes[i];
		}
		this.componentNodes = componentNodes;
	}

	public static RelationPremiseInputBuilder create(IStrategoAppl source, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(source, "Source", 2);
		TermBuild lhsNode = TermBuild.create(Tools.applAt(source, 0), fd);

		IStrategoList rws = Tools.listAt(source, 1);
		TermBuild[] rwNodes = new TermBuild[rws.getSubtermCount()];
		for (int i = 0; i < rwNodes.length; i++) {
			rwNodes[i] = TermBuild.createFromLabelComp(Tools.applAt(rws, i), fd);
		}

		return new RelationPremiseInputBuilder(lhsNode, rwNodes, SourceSectionUtil.fromStrategoTerm(source));
	}

	@ExplodeLoop
	public Object[] executeObjectArray(VirtualFrame frame) {
		Object term = termNode.executeGeneric(frame);

		Object[] args = new Object[componentNodes.length + 1];
		args[0] = term;

		CompilerAsserts.compilationConstant(componentNodes.length);

		for (int i = 0; i < componentNodes.length; i++) {
			InterpreterUtils.setComponent(args, i + 1, componentNodes[i].executeGeneric(frame));
		}

		return args;
	}

	@Override
	public Object[] executeGeneric(VirtualFrame frame) {
		return executeObjectArray(frame);
	}

}
