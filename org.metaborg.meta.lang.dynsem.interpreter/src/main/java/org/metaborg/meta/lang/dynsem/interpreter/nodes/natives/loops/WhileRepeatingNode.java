package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.LoopConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

import mb.flowspec.runtime.interpreter.TypesGen;

public class WhileRepeatingNode extends DynSemNode implements RepeatingNode {

	private final FrameSlot conditionTSlot;
	private final FrameSlot bodyTSlot;
	private final FrameSlot resultTSlot;
	private final FrameSlot[] roCompSlots;
	private final FrameSlot[] rwCompSlots;

	@Child private DispatchNode conditionEvalNode;
	@Child private DispatchNode bodyEvalNode;

	public WhileRepeatingNode(SourceSection source, FrameSlot conditionTSlot, FrameSlot bodyTSlot,
			FrameSlot resultTSlot, FrameSlot[] roCompSlots, FrameSlot[] rwCompSlots) {
		super(source);
		this.conditionTSlot = conditionTSlot;
		this.bodyTSlot = bodyTSlot;
		this.resultTSlot = resultTSlot;
		this.roCompSlots = roCompSlots;
		this.rwCompSlots = rwCompSlots;
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
		if (conditionProfile.profile(evaluateCondition(frame))) {
			try {
				final Object bodyTerm = frame.getValue(bodyTSlot);
				final Object[] bodyArgs = mkArgs(frame);
				bodyArgs[0] = bodyTerm;
				final RuleResult bodyResult = bodyEvalNode.execute(bodyTerm.getClass(), bodyArgs);
				updateRwComponents(frame, bodyResult.components);
				updateResult(frame, bodyResult.result);
				return true;
			} catch (LoopContinueException cex) {
				continueTaken.enter();
				updateRwComponents(frame, cex.getComponents());
				updateResult(frame, cex.getThrown());
				return true;
			} catch (LoopBreakException brex) {
				breakTaken.enter();
				updateRwComponents(frame, brex.getComponents());
				updateResult(frame, brex.getThrown());
				return false;
			}
		} else {
			return false;
		}

	}

	private boolean evaluateCondition(VirtualFrame frame) {
		final Object conditionTerm = frame.getValue(conditionTSlot);
		final Object[] conditionArgs = mkArgs(frame);
		conditionArgs[0] = conditionTerm;
		final RuleResult result = conditionEvalNode.execute(conditionTerm.getClass(), conditionArgs);
		updateRwComponents(frame, result.components);
		return TypesGen.asBoolean(result.result);
	}

	private void updateResult(VirtualFrame frame, Object result) {
		frame.setObject(resultTSlot, result);
	}

	@ExplodeLoop
	private void updateRwComponents(VirtualFrame frame, Object[] components) {
		for (int i = 0; i < rwCompSlots.length; i++) {
			frame.setObject(rwCompSlots[i], components[i]);
		}
	}

	@ExplodeLoop
	private Object[] mkArgs(VirtualFrame frame) {
		final int numRoComps = roCompSlots.length;
		final int numRwComps = rwCompSlots.length;
		final Object[] args = new Object[numRoComps + numRwComps + 1];
		for (int i = 0; i < numRoComps; i++) {
			args[i + 1] = frame.getValue(roCompSlots[i]);
		}
		for (int i = 0; i < numRwComps; i++) {
			args[i + numRoComps + 1] = frame.getValue(rwCompSlots[i]);
		}
		return args;
	}

}
