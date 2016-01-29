package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class ConMatch extends MatchPattern {

	private final String name;
	@Children private final MatchPattern[] children;

	public ConMatch(String name, MatchPattern[] children, SourceSection source) {
		super(source);
		this.name = name;
		this.children = children;
	}

	@Override
	@ExplodeLoop
	public boolean execute(ITerm term, VirtualFrame frame) {
		Class<MatchPattern> patternClass = getContext()
				.lookupMatchPatternClass(name, children.length);
		try {
			Constructor<MatchPattern> constr = patternClass
					.getConstructor(getConstructorClasses());
			MatchPattern replacement = constr.newInstance(getSourceSection(),
					children);
			return replace(replacement).execute(term, frame);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(
					"Interpreter crash: term match specialization failure", e);
		}
	}

	@ExplodeLoop
	@SuppressWarnings("rawtypes")
	private Class[] getConstructorClasses() {
		Class[] classes = new Class[children.length + 1];
		classes[0] = SourceSection.class;
		for (int i = 1; i <= children.length; i++) {
			classes[i] = MatchPattern.class;
		}
		return classes;
	}

}
