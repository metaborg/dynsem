package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.DispatchInteropNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeOperationNode;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public class HandleNode extends NativeOperationNode {

	@Child private TermBuild throwingTBNode;
	@Child private DispatchInteropNode throwingDispatchNode;

	@Child private TermBuild continuingTBNode;
	@Child private DispatchInteropNode continuingDispatchNode;
	
	@Child private InvokeHandlerNode handlingNode;

	public HandleNode(SourceSection source) {
		super(source);
	}

	private final BranchProfile catchEntered = BranchProfile.create();
	private final ConditionProfile continueExistsCondition = ConditionProfile.createBinaryProfile();

	@Override
	public Object execute(VirtualFrame frame, VirtualFrame components) {
		Object throwingBranchResult = null;
		try {
			// not returning because we need for continue
			throwingBranchResult = throwingDispatchNode.executeInterop(frame, components, throwingTBNode.executeGeneric(frame));
		} catch (AbortedEvaluationException abort) {
			catchEntered.enter();
			// branch must return
			return handlingNode.execute(frame, abort.getComponents(), abort.getThrown());
		}

		if (continueExistsCondition.profile(continuingTBNode == null)) {
			// no continue branch so return from the throwing branch
			return throwingBranchResult;
		}else {
			// execute the continue
			return continuingDispatchNode.executeInterop(frame, components, continuingTBNode.executeGeneric(frame));
		}
	}

}
