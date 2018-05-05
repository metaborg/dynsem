package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.StatefulControlFlowException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.LoopConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

import mb.flowspec.runtime.interpreter.TypesGen;

public class WhileRepeatingNode extends DynSemNode implements RepeatingNode {

	private final int numRwComponents;

	private final FrameSlot conditionTSlot;
	private final FrameSlot conditionClassSlot;
	private final FrameSlot bodyTSlot;
	private final FrameSlot bodyClassSlot;

	private final FrameSlot resultTSlot;

	@Child private DispatchNode conditionEvalNode;
	@Child private DispatchNode bodyEvalNode;

	public WhileRepeatingNode(SourceSection source, FrameSlot conditionTSlot, FrameSlot conditionClassSlot,
			FrameSlot bodyTSlot, FrameSlot bodyClassSlot, FrameSlot resultTSlot, int numRwComponents) {
		super(source);
		this.conditionTSlot = conditionTSlot;
		this.conditionClassSlot = conditionClassSlot;
		this.bodyTSlot = bodyTSlot;
		this.bodyClassSlot = bodyClassSlot;
		this.resultTSlot = resultTSlot;
		this.numRwComponents = numRwComponents;
		this.conditionEvalNode = DispatchNode.create(getSourceSection(), "");
		this.bodyEvalNode = DispatchNode.create(getSourceSection(), "");
		adoptChildren();
	}

	private final LoopConditionProfile conditionProfile = LoopConditionProfile.createCountingProfile();
	private final BranchProfile continueTaken = BranchProfile.create();
	private final BranchProfile breakTaken = BranchProfile.create();

	/**
	 * 
	 * @param frame
	 *            This is an unusual frame. It is NOT the surrounding rule frame. It is a frame created specifically for
	 *            this invocation. It contains the dispatch term, the RO components required, the RW components
	 *            required, the result term (initially the default result, in subsequent runs this will be updated with
	 *            the resulting value)
	 */
	@Override
	public boolean executeRepeating(VirtualFrame frame) {
		Object[] args = frame.getArguments();
		CompilerAsserts.compilationConstant(args.length);
		args[0] = frame.getValue(conditionTSlot);
		if (conditionProfile.profile(evaluateCondition(frame, args))) {
			try {
				args[0] = frame.getValue(bodyTSlot);
				evaluateBody(frame, args);
				return true;
			} catch (LoopContinueException cex) {
				continueTaken.enter();
				handleInterrupted(frame, args, cex);
				return true;
			} catch (LoopBreakException brex) {
				breakTaken.enter();
				handleInterrupted(frame, args, brex);
				return false;
			}
		} else {
			return false;
		}

	}

	@ExplodeLoop
	private boolean evaluateCondition(VirtualFrame frame, Object[] args) {
		CompilerAsserts.compilationConstant(args.length);
		final RuleResult conditionResult = conditionEvalNode.execute((Class<?>) frame.getValue(conditionClassSlot),
				args);
		final Object[] resultRwComponents = conditionResult.components;
		assert resultRwComponents.length == numRwComponents;

		for (int i = args.length - 1; i >= args.length - numRwComponents; i--) {
			args[i] = resultRwComponents[i - numRwComponents - 1];
		}

		return TypesGen.asBoolean(conditionResult.result);
	}

	@ExplodeLoop
	private void evaluateBody(VirtualFrame frame, Object[] args) {
		CompilerAsserts.compilationConstant(args.length);
		final RuleResult bodyResult = bodyEvalNode.execute((Class<?>) frame.getValue(bodyClassSlot), args);

		final Object[] resultRwComponents = bodyResult.components;
		assert resultRwComponents.length == numRwComponents;

		for (int i = args.length - 1; i >= args.length - numRwComponents; i--) {
			args[i] = resultRwComponents[i - numRwComponents - 1];
		}

		frame.setObject(resultTSlot, bodyResult.result);
	}

	@ExplodeLoop
	private void handleInterrupted(VirtualFrame frame, Object[] args, StatefulControlFlowException ex) {
		CompilerAsserts.compilationConstant(args.length);
		frame.setObject(resultTSlot, ex.getThrown());

		final Object[] thrownRwComponents = ex.getComponents();
		assert thrownRwComponents.length == numRwComponents;

		for (int i = args.length - 1; i >= args.length - numRwComponents; i--) {
			args[i] = thrownRwComponents[i - numRwComponents - 1];
		}
	}
}
