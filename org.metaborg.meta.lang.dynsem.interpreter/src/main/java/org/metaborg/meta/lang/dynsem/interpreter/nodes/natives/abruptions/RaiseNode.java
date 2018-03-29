package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeOperationNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class RaiseNode extends NativeOperationNode {

	@Child private TermBuild thrownBuildNode;

	public RaiseNode(SourceSection source, TermBuild thrownBuildNode) {
		super(source);
		this.thrownBuildNode = thrownBuildNode;
	}

	@Override
	public RuleResult execute(VirtualFrame frame, VirtualFrame components) {
		throw new AbortedEvaluationException(thrownBuildNode.executeGeneric(frame), components.materialize());
	}

	public static RaiseNode create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Raise", 1);
		return new RaiseNode(SourceUtils.dynsemSourceSectionFromATerm(t), TermBuild.create(Tools.applAt(t, 0), fd));
	}

}
