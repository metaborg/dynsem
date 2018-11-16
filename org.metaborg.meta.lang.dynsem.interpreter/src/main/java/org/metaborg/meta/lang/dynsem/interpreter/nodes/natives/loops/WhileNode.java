package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeExecutableNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.source.SourceSection;

public class WhileNode extends NativeExecutableNode {

	private final FrameSlot componentsFrameSlot;
	private final FrameSlot loopResultFrameSlot;

	@Children private final TermBuild[] componentBuildNodes;
	@Child private TermBuild defaultResultNode;
	// @Children private final TermBuild[] roCompBuilds;
	// @Children private final TermBuild[] rwCompBuilds;

	@Child private LoopNode loopNode;

	public WhileNode(SourceSection source, TermBuild conditionBuildNode, TermBuild bodyBuildNode,
			TermBuild defaultValBuildNode, TermBuild[] componentBuildNodes, FrameSlot componentsFrameSlot,
			FrameSlot loopResultFrameSlot) {
		super(source);
		this.componentBuildNodes = componentBuildNodes;
		this.componentsFrameSlot = componentsFrameSlot;
		this.loopResultFrameSlot = loopResultFrameSlot;
		this.defaultResultNode = defaultValBuildNode;
		this.loopNode = Truffle.getRuntime().createLoopNode(new WhileRepeatingNode(source, conditionBuildNode,
				bodyBuildNode, componentsFrameSlot, loopResultFrameSlot));
	}

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		// evaluate the input components. we'll make this array with space for the input term
		Object[] args = new Object[1 + componentBuildNodes.length];
		for (int i = 0; i < componentBuildNodes.length; i++) {
			args[i + 1] = componentBuildNodes[i].executeGeneric(frame);
		}
		// set the component in the frame
		frame.setObject(componentsFrameSlot, args);

		loopNode.executeLoop(frame);
		Object resultTerm = frame.getValue(loopResultFrameSlot);
		// TODO: propagate the semantic components ...
		if (resultTerm == null) {
			resultTerm = this.defaultResultNode.executeGeneric(frame);
		}
		// TODO: propagate semantic components
		return new RuleResult(resultTerm, new Object[0]);
	}

	@TruffleBoundary
	public static final String genComponentsFrameSlotName(int idx) {
		return "__WhileNodeComponentsSlot__" + idx;
	}

	@TruffleBoundary
	public static final String genResultFrameSlotName(int idx) {
		return "__WhileNodeResultSlot__" + idx;
	}

}
