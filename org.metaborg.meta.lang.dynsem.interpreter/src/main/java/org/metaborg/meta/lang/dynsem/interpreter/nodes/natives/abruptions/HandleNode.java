package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import java.util.Arrays;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeExecutableNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchChainRoot;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public class HandleNode extends NativeExecutableNode {

	@Child private TermBuild evalBuildNode;
	@Child private DispatchNode evalDispatchNode;

	@Child private TermBuild catchBuildNode;
	@Child private TermBuild continueBuildNode;
	@Child private DispatchNode continueDispatchNode;

	@Child private ReflectiveHandlerBuild handlerBuildNode;

	@Child private DispatchChainRoot handlerDispatch;

	// @CompilationFinal private CallTarget handlerCallTarget;

	public HandleNode(SourceSection source, TermBuild evalBuildNode, TermBuild catchBuildNode,
			TermBuild continueBuildNode) {
		super(source);
		this.evalBuildNode = evalBuildNode;
		this.catchBuildNode = catchBuildNode;
		this.continueBuildNode = continueBuildNode;
		this.handlerBuildNode = ReflectiveHandlerBuildNodeGen.create(source);
		this.evalDispatchNode = DispatchNode.create(source, "");
		this.continueDispatchNode = DispatchNode.create(source, "");
	}

	private final BranchProfile catchTaken = BranchProfile.create();
	private final BranchProfile handlerFails = BranchProfile.create();
	private final ConditionProfile continueExistsCondition = ConditionProfile.createBinaryProfile();

	// FIXME: @ExplodeLoop
	@Override
	public RuleResult execute(VirtualFrame frame) {
		final Object[] handleArgs = frame.getArguments();
		Object evalT = evalBuildNode.executeGeneric(frame);
		RuleResult result = null;
		try {
			// try to evaluate the wrapped body
			Object[] args = Arrays.copyOf(handleArgs, handleArgs.length);
			args[0] = evalT;
			result = evalDispatchNode.execute(evalT.getClass(), args);
		} catch (AbortedEvaluationException abex) {
			catchTaken.enter();
			Object catchingT = catchBuildNode.executeGeneric(frame);

			Object handlerT = handlerBuildNode.execute(frame, abex.getThrown(), catchingT);
			// handler gets the ROs from the handle
			// and the RW from the exception
			Object[] rwComps = abex.getComponents();

			Object[] args = new Object[handleArgs.length];
			args[0] = handlerT;
			final int numRoComps = handleArgs.length - rwComps.length - 1;
			final int numRwComps = rwComps.length;

			// copy the RO components
			for (int i = 0; i < numRoComps; i++) {
				args[i + 1] = handleArgs[i + 1];
			}
			// copy the RW components
			for (int i = 0; i < numRwComps; i++) {
				args[i + numRoComps + 1] = rwComps[i];
			}

			if (handlerDispatch == null) {
				 CompilerDirectives.transferToInterpreterAndInvalidate();
				handlerDispatch = insert(
						DispatchChainRoot.createUninitialized(getSourceSection(), "", handlerT.getClass(), true));
			}
			try {
				return handlerDispatch.execute(args);
			} catch (PremiseFailureException rafx) {
				handlerFails.enter();
				throw abex;
			}
		}

		if (continueExistsCondition.profile(continueBuildNode == null)) {
			return result;
		} else {
			Object continueT = continueBuildNode.executeGeneric(frame);
			// continue gets the ROs from the handle
			// and the RW from the eval result
			Object[] rwComps = result.components;
			Object[] args = new Object[handleArgs.length];
			args[0] = continueT;
			final int numRoComps = handleArgs.length - rwComps.length - 1;
			final int numRwComps = rwComps.length;

			// copy the RO components
			for (int i = 0; i < numRoComps; i++) {
				args[i + 1] = handleArgs[i + 1];
			}
			// copy the RW components
			for (int i = 0; i < numRwComps; i++) {
				args[i + numRoComps + 1] = rwComps[i];
			}

			return continueDispatchNode.execute(continueT.getClass(), args);
		}
	}



}
