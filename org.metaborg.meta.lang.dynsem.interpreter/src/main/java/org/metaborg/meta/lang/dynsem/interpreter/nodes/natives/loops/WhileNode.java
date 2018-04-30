package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops;

import java.util.Arrays;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeExecutableNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.source.SourceSection;

public class WhileNode extends NativeExecutableNode {

	private static final byte ID_CONDITION = 1;
	private static final byte ID_CONDITION_CLASS = 2;
	private static final byte ID_BODY = 3;
	private static final byte ID_BODY_CLASS = 4;
	private static final byte ID_RESULT = 5;

	@Child private TermBuild conditionBuildNode;
	@Child private TermBuild bodyBuildNode;
	@Child private TermBuild defaultValBuildNode;

	@Children private final TermBuild[] roCompBuilds;
	@Children private final TermBuild[] rwCompBuilds;

	@Child private LoopNode loopNode;

	private final FrameDescriptor loopFrameDescriptor;

	private final FrameSlot conditionTSlot;
	private final FrameSlot conditionClassSlot;
	private final FrameSlot bodyTSlot;
	private final FrameSlot bodyClassSlot;
	private final FrameSlot resultTSlot;
	// @CompilationFinal(dimensions = 1) private final FrameSlot[] roCompSlots;
	// @CompilationFinal(dimensions = 1) private final FrameSlot[] rwCompSlots;

	public WhileNode(SourceSection source, TermBuild conditionBuildNode, TermBuild bodyBuildNode,
			TermBuild defaultValBuildNode, TermBuild[] roCompBuilds, TermBuild[] rwCompBuilds) {
		super(source);
		this.conditionBuildNode = conditionBuildNode;
		this.bodyBuildNode = bodyBuildNode;
		this.defaultValBuildNode = defaultValBuildNode;
		this.roCompBuilds = roCompBuilds;
		this.rwCompBuilds = rwCompBuilds;

		loopFrameDescriptor = new FrameDescriptor();

		this.conditionTSlot = loopFrameDescriptor.addFrameSlot(ID_CONDITION);
		this.conditionClassSlot = loopFrameDescriptor.addFrameSlot(ID_CONDITION_CLASS);
		this.bodyTSlot = loopFrameDescriptor.addFrameSlot(ID_BODY);
		this.bodyClassSlot = loopFrameDescriptor.addFrameSlot(ID_BODY_CLASS);
		this.resultTSlot = loopFrameDescriptor.addFrameSlot(ID_RESULT);
		//
		// this.roCompSlots = new FrameSlot[roCompBuilds.length];
		// for (int i = 0; i < roCompBuilds.length; i++) {
		// this.roCompSlots[i] = loopFrameDescriptor.addFrameSlot("RO_" + i);
		// }
		// this.rwCompSlots = new FrameSlot[rwCompBuilds.length];
		// for (int i = 0; i < rwCompBuilds.length; i++) {
		// this.rwCompSlots[i] = loopFrameDescriptor.addFrameSlot("RW_" + i);
		// }
		this.loopNode = Truffle.getRuntime().createLoopNode(
				new WhileRepeatingNode(source, conditionTSlot, conditionClassSlot, bodyTSlot, bodyClassSlot,
						resultTSlot, rwCompBuilds.length));
	}

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		Object[] args = new Object[1 + roCompBuilds.length + rwCompBuilds.length];
		for (int i = 0; i < roCompBuilds.length; i++) {
			args[i + 1] = roCompBuilds[i].executeGeneric(frame);
		}

		for (int i = 0; i < rwCompBuilds.length; i++) {
			args[i + 1 + roCompBuilds.length] = rwCompBuilds[i].executeGeneric(frame);
		}

		VirtualFrame loopFrame = Truffle.getRuntime().createVirtualFrame(args, loopFrameDescriptor);

		Object conditionT = conditionBuildNode.executeGeneric(frame);
		loopFrame.setObject(conditionTSlot, conditionT);
		loopFrame.setObject(conditionClassSlot, conditionT.getClass());

		Object bodyT = bodyBuildNode.executeGeneric(frame);
		loopFrame.setObject(bodyTSlot, bodyT);
		loopFrame.setObject(bodyClassSlot, bodyT.getClass());

		Object defaultValT = defaultValBuildNode.executeGeneric(frame);
		loopFrame.setObject(resultTSlot, defaultValT);
		//
		// for (int i = 0; i < roCompBuilds.length; i++) {
		// loopFrame.setObject(roCompSlots[i], roCompBuilds[i].executeGeneric(frame));
		// }
		//
		// for (int i = 0; i < rwCompBuilds.length; i++) {
		// loopFrame.setObject(rwCompSlots[i], rwCompBuilds[i].executeGeneric(frame));
		// }

		loopNode.executeLoop(loopFrame);

		// Object[] outRwComps = new Object[rwCompBuilds.length];

		//
		// for (int i = 0; i < .length; i++) {
		// outRwComps[i] = loopFrame.getValue(rwCompSlots[i]);
		// }

		return new RuleResult(loopFrame.getValue(resultTSlot),
				Arrays.copyOfRange(args, 1 + roCompBuilds.length, args.length));

	}

	public static WhileNode create(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		// WhileNode: Term * Term * Term * List(Term) * List(Term) -> NativeRule
		assert Tools.hasConstructor(t, "WhileNode", 5);

		TermBuild conditionBuildNode = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild bodyBuildNode = TermBuild.create(Tools.applAt(t, 1), fd);
		TermBuild defaultValBuildNode = TermBuild.create(Tools.applAt(t, 2), fd);

		IStrategoList roCompsT = Tools.listAt(t, 3);
		TermBuild[] roCompBuilds = new TermBuild[roCompsT.size()];
		for (int i = 0; i < roCompBuilds.length; i++) {
			roCompBuilds[i] = TermBuild.create(Tools.applAt(roCompsT, i), fd);
		}

		IStrategoList rwCompsT = Tools.listAt(t, 4);
		TermBuild[] rwCompBuilds = new TermBuild[rwCompsT.size()];
		for (int i = 0; i < rwCompBuilds.length; i++) {
			rwCompBuilds[i] = TermBuild.create(Tools.applAt(rwCompsT, i), fd);
		}

		return new WhileNode(SourceUtils.dynsemSourceSectionFromATerm(t), conditionBuildNode, bodyBuildNode,
				defaultValBuildNode, roCompBuilds, rwCompBuilds);
	}

}
