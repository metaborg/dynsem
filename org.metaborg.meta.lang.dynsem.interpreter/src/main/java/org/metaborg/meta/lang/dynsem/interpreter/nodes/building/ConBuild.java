package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.spoofax.interpreter.terms.IStrategoConstructor;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class ConBuild extends TermBuild {

	private String name;

	@Children private final TermBuild[] children;

	@CompilationFinal private IStrategoConstructor constructor;

	public ConBuild(String name, TermBuild[] children, SourceSection source) {
		super(source);
		this.name = name;
		this.children = children;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		Class<TermBuild> buildClass = getContext().lookupTermBuildClass(name,
				children.length);

		try {
			Constructor<TermBuild> constr = buildClass
					.getConstructor(getConstructorClasses());
			TermBuild replacement = constr.newInstance(getSourceSection(),
					children);
			return replace(replacement).executeGeneric(frame);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(
					"Interpreter crash: term construction specialization failure",
					e);
		}
	}

	@ExplodeLoop
	@SuppressWarnings("rawtypes")
	private Class[] getConstructorClasses() {
		Class[] classes = new Class[children.length + 1];
		classes[0] = SourceSection.class;
		for (int i = 1; i <= children.length; i++) {
			classes[i] = TermBuild.class;
		}
		return classes;
	}
}
