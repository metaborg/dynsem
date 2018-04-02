package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class RestoreComponentsFromSnapshotNode extends DynSemNode {

	@Children private final RestoreComponentFromSnapshotNode[] restoreNodes;

	public RestoreComponentsFromSnapshotNode(SourceSection source, RestoreComponentFromSnapshotNode[] restoreNodes) {
		super(source);
		this.restoreNodes = restoreNodes;
	}

	@ExplodeLoop
	public void executeRestore(VirtualFrame frame, VirtualFrame components) {
		CompilerAsserts.compilationConstant(restoreNodes.length);
		for (int i = 0; i < restoreNodes.length; i++) {
			restoreNodes[i].executeRestore(frame, components);
		}
	}

	public static RestoreComponentsFromSnapshotNode create(IStrategoList labelComps, FrameDescriptor ruleFD,
			FrameDescriptor componentsFD) {
		CompilerAsserts.neverPartOfCompilation();
		RestoreComponentFromSnapshotNode[] captureNodes = new RestoreComponentFromSnapshotNode[labelComps.size()];
		int i = 0;
		for (IStrategoTerm labelComp : labelComps) {
			captureNodes[i] = RestoreComponentFromSnapshotNode.create((IStrategoAppl) labelComp, ruleFD, componentsFD);
			i++;
		}
		return new RestoreComponentsFromSnapshotNode(SourceUtils.dynsemSourceSectionFromATerm(labelComps),
				captureNodes);
	}
	
	public static class RestoreComponentFromSnapshotNode extends DynSemNode {

		private final FrameSlot componentSlot;
		@Child private MatchPattern patt;

		public RestoreComponentFromSnapshotNode(SourceSection source, FrameSlot compSlot, MatchPattern patt) {
			super(source);
			this.componentSlot = compSlot;
			this.patt = patt;
		}

		public void executeRestore(VirtualFrame frame, VirtualFrame components) {
			patt.executeMatch(frame, components.getValue(componentSlot));
		}

		
		public static RestoreComponentFromSnapshotNode create(IStrategoAppl labelComp, FrameDescriptor ruleFD, FrameDescriptor componentsFD) {
			CompilerAsserts.neverPartOfCompilation();
			assert Tools.hasConstructor(labelComp, "LabelComp", 2);
			MatchPattern patt = MatchPattern.createFromLabelComp(labelComp, ruleFD);
			FrameSlot slot = componentsFD.findFrameSlot(Tools.javaStringAt(Tools.applAt(labelComp, 0), 0));
			assert slot != null;
			return new RestoreComponentFromSnapshotNode(SourceUtils.dynsemSourceSectionFromATerm(labelComp), slot, patt);
		}
	}
}
