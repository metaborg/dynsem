package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleInvokeNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleInvokeNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.LoopConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

import mb.flowspec.runtime.interpreter.TypesGen;

public class WhileRepeatingNode extends DynSemNode implements RepeatingNode {

	private final FrameSlot componentsFrameSlot;
	private final FrameSlot resultFrameSlot;

	@Child private RuleInvokeNode conditionInvokeNode;
	@Child private RuleInvokeNode bodyInvokeNode;

	public WhileRepeatingNode(SourceSection source, TermBuild conditionBuildNode, TermBuild bodyBuildNode,
			FrameSlot componentsFrameSlot, FrameSlot resultFrameSlot) {
		super(source);
		this.conditionInvokeNode = RuleInvokeNodeGen.create(source, "", conditionBuildNode);
		this.bodyInvokeNode = RuleInvokeNodeGen.create(source, "", bodyBuildNode);
		this.componentsFrameSlot = componentsFrameSlot;
		this.resultFrameSlot = resultFrameSlot;
	}

	private final LoopConditionProfile conditionProfile = LoopConditionProfile.createCountingProfile();
	private final BranchProfile continueTaken = BranchProfile.create();
	private final BranchProfile breakTaken = BranchProfile.create();

	@Override
	public boolean executeRepeating(VirtualFrame frame) {
		// FIXME: for now we're ignoring updates to RW components

		// get the input components
		Object[] callArgs = (Object[]) frame.getValue(componentsFrameSlot);

		if (conditionProfile.profile(evaluateCondition(frame, callArgs))) {
			try {
				RuleResult bodyResult = this.bodyInvokeNode.executeGeneric(frame, callArgs);
				// TODO propagate sem comps
				frame.setObject(resultFrameSlot, bodyResult.result);
				return true;
			} catch (LoopContinueException cex) {
				continueTaken.enter();
				// TODO propagate sem comps
				frame.setObject(resultFrameSlot, cex.getThrown());
				return true;
			} catch (LoopBreakException brex) {
				breakTaken.enter();
				// TODO propagate sem comps
				frame.setObject(resultFrameSlot, brex.getThrown());
				return false;
			}
		} else {
			return false;
		}

		// Object[] args = frame.getArguments();
		// CompilerAsserts.compilationConstant(args.length);
		// args[0] = frame.getValue(conditionTSlot);
		// if (conditionProfile.profile(evaluateCondition(frame, args))) {
		// try {
		// args[0] = frame.getValue(bodyTSlot);
		// evaluateBody(frame, args);
		// return true;
		// } catch (LoopContinueException cex) {
		// continueTaken.enter();
		// handleInterrupted(frame, args, cex);
		// return true;
		// } catch (LoopBreakException brex) {
		// breakTaken.enter();
		// handleInterrupted(frame, args, brex);
		// return false;
		// }
		// } else {
		// return false;
		// }

	}

	private boolean evaluateCondition(VirtualFrame frame, Object[] callArgs) {
		// FIXME: for now we're ignoring updates to RW components
		RuleResult conditionResult = this.conditionInvokeNode.executeGeneric(frame, callArgs);
		return TypesGen.asBoolean(conditionResult.result);
	}

	// @ExplodeLoop
	// private boolean evaluateCondition(VirtualFrame frame, Object[] args) {
	// CompilerAsserts.compilationConstant(args.length);
	// final RuleResult conditionResult = conditionEvalNode.execute((Class<?>) frame.getValue(conditionClassSlot),
	// args);
	// final Object[] resultRwComponents = conditionResult.components;
	// assert resultRwComponents.length == numRwComponents;
	//
	// final int numRoComponents = args.length - numRwComponents - 1;
	// final int args_base_index = numRoComponents + 1;
	//
	// for (int i = 0; i < numRwComponents; i++) {
	// args[args_base_index + i] = resultRwComponents[i];
	// }
	//
	// return TypesGen.asBoolean(conditionResult.result);
	// }
	//
	// @ExplodeLoop
	// private void evaluateBody(VirtualFrame frame, Object[] args) {
	// CompilerAsserts.compilationConstant(args.length);
	// final RuleResult bodyResult = bodyEvalNode.execute((Class<?>) frame.getValue(bodyClassSlot), args);
	//
	// final Object[] resultRwComponents = bodyResult.components;
	// assert resultRwComponents.length == numRwComponents;
	//
	// final int numRoComponents = args.length - numRwComponents - 1;
	// final int args_base_index = numRoComponents + 1;
	//
	// for (int i = 0; i < numRwComponents; i++) {
	// args[args_base_index + i] = resultRwComponents[i];
	// }
	//
	// frame.setObject(resultTSlot, bodyResult.result);
	// }
	//
	// @ExplodeLoop
	// private void handleInterrupted(VirtualFrame frame, Object[] args, StatefulControlFlowException ex) {
	// CompilerAsserts.compilationConstant(args.length);
	//
	// final Object[] resultRwComponents = ex.getComponents();
	// assert resultRwComponents.length == numRwComponents;
	//
	// final int numRoComponents = args.length - numRwComponents - 1;
	// final int args_base_index = numRoComponents + 1;
	//
	// for (int i = 0; i < numRwComponents; i++) {
	// args[args_base_index + i] = resultRwComponents[i];
	// }
	// frame.setObject(resultTSlot, ex.getThrown());
	// }
}
