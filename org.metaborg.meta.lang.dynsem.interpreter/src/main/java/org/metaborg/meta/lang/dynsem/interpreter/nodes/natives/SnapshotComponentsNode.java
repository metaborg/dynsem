package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class SnapshotComponentsNode extends DynSemNode {

	private final FrameDescriptor componentsFD;

	@Children private final CaptureComponentNode[] captureNodes;

	public SnapshotComponentsNode(SourceSection source, FrameDescriptor componentsFD,
			CaptureComponentNode[] captureNodes) {
		super(source);
		this.componentsFD = componentsFD;
		this.captureNodes = captureNodes;
	}

	@ExplodeLoop
	public VirtualFrame execute(VirtualFrame frame) {
		VirtualFrame f = Truffle.getRuntime().createVirtualFrame(null, componentsFD);

		for (int i = 0; i < captureNodes.length; i++) {
			captureNodes[i].executeCapture(frame, f);
		}

		return f;
	}

	public static SnapshotComponentsNode create(IStrategoList labelComps, FrameDescriptor ruleFD,
			FrameDescriptor componentsFD) {
		CompilerAsserts.neverPartOfCompilation();
		CaptureComponentNode[] captureNodes = new CaptureComponentNode[labelComps.size()];
		int i = 0;
		for (IStrategoTerm labelComp : labelComps) {
			captureNodes[i] = CaptureComponentNode.create((IStrategoAppl) labelComp, ruleFD, componentsFD);
			i++;
		}
		return new SnapshotComponentsNode(SourceUtils.dynsemSourceSectionFromATerm(labelComps), componentsFD,
				captureNodes);
	}
	
	public static class CaptureComponentNode extends DynSemNode {

		@Child private TermBuild tb;
		
		private final FrameSlot componentSlot;

		public CaptureComponentNode(SourceSection source, FrameSlot compSlot, TermBuild tb) {
			super(source);
			this.componentSlot = compSlot;
			this.tb = tb;
		}

		public void executeCapture(VirtualFrame frame, VirtualFrame components) {
			components.setObject(componentSlot, tb.executeGeneric(frame));
		}
		
		public static CaptureComponentNode create(IStrategoAppl labelComp, FrameDescriptor ruleFD, FrameDescriptor componentsFD) {
			CompilerAsserts.neverPartOfCompilation();
			assert Tools.hasConstructor(labelComp, "LabelComp", 2);
			TermBuild tb = TermBuild.createFromLabelComp(labelComp, ruleFD);
			FrameSlot slot = componentsFD.findFrameSlot(Tools.javaStringAt(Tools.applAt(labelComp, 0), 0));
			assert slot != null;
			return new CaptureComponentNode(SourceUtils.dynsemSourceSectionFromATerm(labelComp), slot, tb);
		}

	}
}
