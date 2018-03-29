package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import java.lang.reflect.Constructor;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.DispatchInteropNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoTuple;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class InvokeHandlerNode extends DynSemNode {

	public final static String HANDLER_NAME = "handler";
	public final static int HANDLER_ARITY = 2;

	@Child private TermBuild catchingTermBuildNode;
	@Child private DispatchInteropNode dispatchHandlerNode;

	public InvokeHandlerNode(SourceSection source, TermBuild catchingNode, DispatchInteropNode dispatchHandlerNode) {
		super(source);
		this.catchingTermBuildNode = catchingNode;
		this.dispatchHandlerNode = dispatchHandlerNode;
	}

	public Object execute(VirtualFrame frame, VirtualFrame components, Object thrown) {
		CompilerAsserts.neverPartOfCompilation();
		Object catching = catchingTermBuildNode.executeGeneric(frame);
		Class<?> handlerTermClass = getContext().getTermRegistry().getConstructorClass(HANDLER_NAME, HANDLER_ARITY);
		try {
			Constructor<?> constructor = handlerTermClass.getConstructor(thrown.getClass().getSuperclass(), catching.getClass().getSuperclass());
			Object handler = constructor.newInstance(thrown, catching);
			
			return dispatchHandlerNode.executeInterop(frame, components, handler);
		} catch (ReflectiveOperationException e) {
			CompilerAsserts.neverPartOfCompilation();
			throw new RuntimeException("Failed to construct handler term", e);
		}
	}

	public static InvokeHandlerNode create(IStrategoTuple t, FrameDescriptor ruleFD, FrameDescriptor componentsFD) {
		CompilerAsserts.neverPartOfCompilation();
		assert t.size() == 3;
		TermBuild catchingTermBuildNode = TermBuild.create(Tools.applAt(t, 1), ruleFD);
		DispatchInteropNode dispatchHandlerNode = DispatchInteropNode.create(Tools.listAt(t, 0), Tools.listAt(t, 2),
				componentsFD);
		return new InvokeHandlerNode(SourceUtils.dynsemSourceSectionFromATerm(t), catchingTermBuildNode,
				dispatchHandlerNode);
	}

}
