package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.DispatchInteropNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeOperationNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public class HandleNode extends NativeOperationNode {

	@Child private TermBuild throwingTBNode;
	@Child private DispatchInteropNode throwingDispatchNode;

	@Child private TermBuild continuingTBNode;
	@Child private DispatchInteropNode continuingDispatchNode;

	@Child private InvokeHandlerNode handlingNode;

	public HandleNode(SourceSection source, TermBuild throwingTBNode, DispatchInteropNode throwingDispatchNode,
			TermBuild continuingTBNode, DispatchInteropNode continuingDispatchNode, InvokeHandlerNode handlingNode) {
		super(source);
		this.throwingTBNode = throwingTBNode;
		this.throwingDispatchNode = throwingDispatchNode;
		this.continuingTBNode = continuingTBNode;
		this.continuingDispatchNode = continuingDispatchNode;
		this.handlingNode = handlingNode;
	}

	private final BranchProfile catchEntered = BranchProfile.create();
	private final ConditionProfile continueExistsCondition = ConditionProfile.createBinaryProfile();

	@CompilationFinal(dimensions = 1) private FrameSlot[] componentSlots;

	@Override
	@ExplodeLoop
	public Object execute(VirtualFrame frame, VirtualFrame components) {
		if (componentSlots == null) {
			componentSlots = components.getFrameDescriptor().getSlots().toArray(new FrameSlot[0]);
		}
		CompilerAsserts.compilationConstant(componentSlots.length);
		Object throwingBranchResult = null;
		try {
			// not returning because we need for continue
			throwingBranchResult = throwingDispatchNode.executeInterop(frame, components,
					throwingTBNode.executeGeneric(frame));
		} catch (AbortedEvaluationException abort) {
			catchEntered.enter();
			VirtualFrame abortedComponents = abort.getComponents();
			Object handleResult = handlingNode.execute(frame, abortedComponents, abort.getThrown());
			// update the components frame with changed from the aborted computation
			for (FrameSlot frameSlot : componentSlots) {
				components.setObject(frameSlot, abortedComponents.getValue(frameSlot));
			}
			// branch must return
			return handleResult;
		}

		if (continueExistsCondition.profile(continuingTBNode == null)) {
			// no continue branch so return from the throwing branch
			return throwingBranchResult;
		} else {
			// execute the continue
			return continuingDispatchNode.executeInterop(frame, components, continuingTBNode.executeGeneric(frame));
		}
	}

	public static HandleNode create(IStrategoAppl t, FrameDescriptor ruleFD, FrameDescriptor componentsFD) {
		CompilerAsserts.neverPartOfCompilation();
		IStrategoTuple throwingPart = null;
		IStrategoTuple continuingPart = null;
		IStrategoTuple handlingPart = null;
		if (Tools.hasConstructor(t, "Handle2", 2)) {
			throwingPart = Tools.termAt(t, 0);
			handlingPart = Tools.termAt(t, 1);
		} else if (Tools.hasConstructor(t, "Handle3", 3)) {
			throwingPart = Tools.termAt(t, 0);
			continuingPart = Tools.termAt(t, 1);
			handlingPart = Tools.termAt(t, 2);
		} else {
			throw new NotImplementedException("Unknown handle term: " + t);
		}

		TermBuild throwingTBNode = TermBuild.create(Tools.applAt(throwingPart, 1), ruleFD);
		DispatchInteropNode throwingDispatchNode = DispatchInteropNode.create(Tools.listAt(throwingPart, 0),
				Tools.listAt(throwingPart, 2), componentsFD);

		TermBuild continuingTBNode = continuingPart != null ? TermBuild.create(Tools.applAt(continuingPart, 1), ruleFD)
				: null;
		DispatchInteropNode continuingDispatchNode = continuingPart != null
				? DispatchInteropNode.create(Tools.listAt(continuingPart, 0), Tools.listAt(continuingPart, 2),
						componentsFD)
				: null;
		InvokeHandlerNode handlingNode = InvokeHandlerNode.create(handlingPart, ruleFD, componentsFD);

		return new HandleNode(SourceUtils.dynsemSourceSectionFromATerm(t), throwingTBNode, throwingDispatchNode,
				continuingTBNode, continuingDispatchNode, handlingNode);
	}

}
