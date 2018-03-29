package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import java.lang.reflect.Constructor;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ReflectiveHandlerInitLookup extends DynSemNode {

	public final static String HANDLER_NAME = "handler";
	public final static int HANDLER_ARITY = 2;

	public ReflectiveHandlerInitLookup(SourceSection source) {
		super(source);
	}

	public abstract Constructor<?> execute(Class<?> catchingClass, Class<?> thrownClass);

	@Specialization(limit = "1", guards = { "catchingClass == cachedCatchingClass",
			"thrownClass == cachedThrownClass" })
	public Constructor<?> executeCached(Class<?> catchingClass, Class<?> thrownClass,
			@Cached("catchingClass") Class<?> cachedCatchingClass, @Cached("thrownClass") Class<?> cachedThrownClass,
			@Cached("executeSlow(cachedCatchingClass, cachedThrownClass)") Constructor<?> cachedConstructor) {
		// FIXME: this is breaking compilation. try to use the invokedynamic stuff to lookup the handle
		// (https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/MethodHandles.Lookup.html)
		return cachedConstructor;
	}

	@Specialization(replaces = "executeCached")
	public Constructor<?> executeSlow(Class<?> catchingClass, Class<?> thrownClass) {
		Class<?> handlerTermClass = getContext().getTermRegistry().getConstructorClass(HANDLER_NAME, HANDLER_ARITY);
		try {
			return handlerTermClass.getConstructor(thrownClass.getSuperclass(), catchingClass.getSuperclass());
		} catch (ReflectiveOperationException e) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			throw new RuntimeException("Failed to find handler class or its constructor", e);
		}
	}

}
