package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import java.lang.reflect.Constructor;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.DispatchInteropNode;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class InvokeHandlerNode extends DynSemNode {

	public final static String HANDLER_NAME = "handler";
	public final static int HANDLER_ARITY = 2;

	@Child private TermBuild catchingTermBuildNode;
	@Child private DispatchInteropNode dispatchHandlerNode;

	public InvokeHandlerNode(SourceSection source, TermBuild catchingNode, FrameSlot[] inComponents,
			FrameSlot[] outComponents) {
		super(source);
		this.dispatchHandlerNode = new DispatchInteropNode(source, inComponents, outComponents);
	}

	
	public Object execute(VirtualFrame frame, VirtualFrame components, Object thrown) {
		CompilerAsserts.neverPartOfCompilation();
		Object catching = catchingTermBuildNode.executeGeneric(frame);
		Class<?> handlerTermClass = getContext().getTermRegistry().getConstructorClass(HANDLER_NAME, HANDLER_ARITY);
		try {
			Constructor<?> constructor = handlerTermClass.getConstructor(thrown.getClass(), catching.getClass());
			Object handler = constructor.newInstance(thrown, catching);
			return dispatchHandlerNode.executeInterop(frame, components, handler);
		} catch (ReflectiveOperationException e) {
			CompilerAsserts.neverPartOfCompilation();
			throw new RuntimeException("Failed to construct handler term", e);
		}
	}

}
