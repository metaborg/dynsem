package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class NativeRelationPremise extends Premise {

	@Child private NativeOperationNode nativeCallNode;

	@Child private SnapshotComponentsNode snapshotComponents;

	@Child private MatchPattern rhsNode;

	@Child private RestoreComponentsFromSnapshotNode restoreComponents;

	public NativeRelationPremise(SourceSection source, NativeOperationNode nativeCallNode, MatchPattern rhsNode,
			SnapshotComponentsNode snapshotComponents, RestoreComponentsFromSnapshotNode restoreComponents) {
		super(source);
		this.nativeCallNode = nativeCallNode;
		this.rhsNode = rhsNode;
		this.snapshotComponents = snapshotComponents;
		this.restoreComponents = restoreComponents;
	}

	public void execute(VirtualFrame frame) {
		final VirtualFrame components = snapshotComponents.execute(frame);
		// the call will probably update the components in the components frame above
		final Object callResult = nativeCallNode.execute(frame, components);

		// evaluate the RHS pattern match
		rhsNode.executeMatch(frame, callResult);

		// update component variables from the component frame
		restoreComponents.executeRestore(frame, components);
	}

	// NativeRelation([LabelComp(Label("NaBL2"),VarRef("NaBL21")),LabelComp(Label("F"),VarRef("F1")),LabelComp(Label("H"),VarRef("H1"))],Raise(Con("BR",[])),VarRef("u1"),[LabelComp(Label("H"),VarRef("H2"))])
	public static NativeRelationPremise create(IStrategoAppl t, FrameDescriptor ruleFD) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "NativeRelation", 4);
		IStrategoList labelComps = Tools.listAt(t, 0);

		FrameDescriptor componentsFD = createComponentDescriptor(labelComps);

		SnapshotComponentsNode snapshotComponents = SnapshotComponentsNode.create(labelComps, ruleFD, componentsFD);
		NativeOperationNode nativeCallNode = NativeOperationNode.create(Tools.applAt(t, 1), ruleFD, componentsFD);
		MatchPattern rhsNode = MatchPattern.create(Tools.applAt(t, 2), ruleFD);
		RestoreComponentsFromSnapshotNode restoreComponents = RestoreComponentsFromSnapshotNode
				.create(Tools.listAt(t, 3), ruleFD, componentsFD);
		return new NativeRelationPremise(SourceUtils.dynsemSourceSectionFromATerm(t), nativeCallNode, rhsNode,
				snapshotComponents, restoreComponents);
	}

	private static FrameDescriptor createComponentDescriptor(IStrategoList labelComps) {
		FrameDescriptor fd = new FrameDescriptor();
		for (IStrategoTerm labelCompTerm : labelComps) {
			assert Tools.isTermAppl(labelCompTerm);
			IStrategoAppl labelComp = (IStrategoAppl) labelCompTerm;
			assert Tools.hasConstructor(labelComp, "LabelComp", 2);
			String compName = Tools.javaStringAt(Tools.applAt(labelComp, 0), 0);
			fd.addFrameSlot(compName);
		}
		return fd;
	}

}
