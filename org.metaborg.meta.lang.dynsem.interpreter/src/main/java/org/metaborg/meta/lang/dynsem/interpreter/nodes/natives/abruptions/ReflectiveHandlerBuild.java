package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ReflectiveHandlerBuild extends DynSemNode {

	private static final String HANDLER_CTOR_NAME = "handler";
	private static final int HANDLER_CTOR_ARITY = 2;

	public ReflectiveHandlerBuild(SourceSection source) {
		super(source);
	}

	public abstract Object execute(Object thrown, Object catching);

	@Specialization(limit = "1", guards = { "thrown.getClass() == cachedThrownClass",
			"catching.getClass() == cachedCatchingClass" })
	public Object executeCached(Object thrown, Object catching, @Cached("thrown.getClass()") Class<?> cachedThrownClass,
			@Cached("catching.getClass()") Class<?> cachedCatchingClass,
			@Cached("findConstructorHandle(cachedThrownClass, cachedCatchingClass)") MethodHandle cachedCtrHandle) {
		return doInvoke(cachedCtrHandle, thrown, catching);
	}

	// FIXME: somehow we should allow inlining of the method invocation
	@TruffleBoundary
	private Object doInvoke(MethodHandle ctrHandle, Object thrown, Object catching) {
		try {
			return ctrHandle.invoke(thrown, catching);
		} catch (Throwable e) {
			throw new RuntimeException("Failed to instantiate handler term", e);
		}
	}

	@Specialization(replaces = "executeCached")
	public Object executeSlow(Object thrown, Object catching) {
		return doInvoke(findConstructorHandle(thrown.getClass(), catching.getClass()), thrown, catching);
	}

	@TruffleBoundary
	protected MethodHandle findConstructorHandle(Class<?> thrownClass, Class<?> catchingClass) {
		CompilerAsserts.neverPartOfCompilation();
		Class<?> handlerTermClass = getContext().getTermRegistry().getConstructorClass(HANDLER_CTOR_NAME,
				HANDLER_CTOR_ARITY);
		MethodHandle constructorHandle;
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		try {
			constructorHandle = lookup.findConstructor(handlerTermClass,
					methodType(void.class, thrownClass.getSuperclass(), catchingClass.getSuperclass()));
			return constructorHandle.asFixedArity();
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException("Failed to find constructors for handler class", e);
		}
	}

}
