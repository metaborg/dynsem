package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RecurException;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class RecursiveRelationPremise extends Premise {

	@Child protected TermBuild inputNode;
	@Children protected final TermBuild[] componentNodes;

	public RecursiveRelationPremise(SourceSection source, TermBuild inputNode, TermBuild[] componentNodes) {
		super(source);
		this.inputNode = inputNode;
		this.componentNodes = componentNodes;
	}

	@Override
	@ExplodeLoop
	public void execute(VirtualFrame frame) {
		Object[] args = frame.getArguments();
		args[0] = inputNode.executeGeneric(frame);
		for (int i = 0; i < componentNodes.length; i++) {
			args[i + 1] = componentNodes[i].executeGeneric(frame);
		}

		throw RecurException.INSTANCE;
	}

	public static RecursiveRelationPremise create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "RecRelation", 3);

		IStrategoAppl source = Tools.applAt(t, 0);
		assert Tools.hasConstructor(source, "Source", 2);
		TermBuild lhsNode = TermBuild.create(Tools.applAt(source, 0), fd);

		IStrategoList rws = Tools.listAt(source, 1);
		TermBuild[] rwNodes = new TermBuild[rws.getSubtermCount()];
		for (int i = 0; i < rwNodes.length; i++) {
			rwNodes[i] = TermBuild.createFromLabelComp(Tools.applAt(rws, i), fd);
		}

		return new RecursiveRelationPremise(SourceSectionUtil.fromStrategoTerm(t), lhsNode, rwNodes);

	}
}
