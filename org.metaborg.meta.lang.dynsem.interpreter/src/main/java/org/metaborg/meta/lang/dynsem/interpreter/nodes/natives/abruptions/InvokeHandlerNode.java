package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import java.lang.reflect.Constructor;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.DispatchInteropNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoTuple;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class InvokeHandlerNode extends DynSemNode {

	@Child private TermBuild catchingTermBuildNode;
	@Child private DispatchInteropNode dispatchHandlerNode;
	@Child private ReflectiveHandlerInitLookup handlerInitLookupNode;

	public InvokeHandlerNode(SourceSection source, TermBuild catchingNode, DispatchInteropNode dispatchHandlerNode) {
		super(source);
		this.catchingTermBuildNode = catchingNode;
		this.dispatchHandlerNode = dispatchHandlerNode;
		this.handlerInitLookupNode = ReflectiveHandlerInitLookupNodeGen.create(getSourceSection());
	}

	public Object execute(VirtualFrame frame, VirtualFrame components, Object thrown) {
		Object catching = catchingTermBuildNode.executeGeneric(frame);
		Constructor<?> constructor = handlerInitLookupNode.execute(catching.getClass(), thrown.getClass());
		Object handler;
		try {
			handler = constructor.newInstance(thrown, catching);
		} catch (ReflectiveOperationException e) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			throw new RuntimeException("Failed to instantiate handler term", e);
		}

		return dispatchHandlerNode.executeInterop(frame, components, handler);
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
