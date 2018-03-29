package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
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
}
