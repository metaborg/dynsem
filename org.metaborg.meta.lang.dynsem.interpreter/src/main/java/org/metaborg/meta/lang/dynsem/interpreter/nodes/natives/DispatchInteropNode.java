package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class DispatchInteropNode extends DynSemNode {

	private final FrameSlot[] inputComponents;
	private final FrameSlot[] outputComponents;
	@Child private DispatchNode dispatchInvokeNode;

	public DispatchInteropNode(SourceSection source, FrameSlot[] inComponents, FrameSlot[] outComponents) {
		super(source);
		this.inputComponents = inComponents;
		this.outputComponents = outComponents;
		this.dispatchInvokeNode = DispatchNodeGen.create(getSourceSection(), "");
	}

	public Object executeInterop(VirtualFrame frame, VirtualFrame components, Object inputTerm) {
		RuleResult result = dispatchInvokeNode.execute(frame, inputTerm.getClass(),
				createArgumentsArray(components, inputTerm));

		updateComponentSnapshot(components, result.components);

		return result.result;
	}

	@ExplodeLoop
	private Object[] createArgumentsArray(VirtualFrame components, Object inputTerm) {
		final Object[] args = new Object[inputComponents.length + 1];
		CompilerAsserts.compilationConstant(args.length);
		args[0] = inputTerm;
		for (int i = 0; i < inputComponents.length; i++) {
			InterpreterUtils.setComponent(getContext(), args, i + 1, components.getValue(inputComponents[i]));
		}
		return args;
	}

	@ExplodeLoop
	public void updateComponentSnapshot(VirtualFrame components, Object[] resultComps) {
		assert resultComps.length == outputComponents.length;
		CompilerAsserts.compilationConstant(resultComps.length);
		for (int i = 0; i < resultComps.length; i++) {
			components.setObject(outputComponents[i], resultComps[i]);
		}
	}

	public static DispatchInteropNode create(IStrategoList inLabels, IStrategoList outLabels,
			FrameDescriptor componentsFD) {
		CompilerAsserts.neverPartOfCompilation();
		return new DispatchInteropNode(SourceUtils.dynsemSourceSectionFromATerm(inLabels),
				findSlots(inLabels, componentsFD), findSlots(outLabels, componentsFD));
	}

	private static FrameSlot[] findSlots(IStrategoList labels, FrameDescriptor componentsFD) {
		CompilerAsserts.neverPartOfCompilation();
		FrameSlot[] slots = new FrameSlot[labels.size()];
		int i = 0;
		for (IStrategoTerm label : labels) {
			slots[i] = componentsFD.findFrameSlot(Tools.javaStringAt(label, 0));
			i++;
		}
		return slots;
	}

}
