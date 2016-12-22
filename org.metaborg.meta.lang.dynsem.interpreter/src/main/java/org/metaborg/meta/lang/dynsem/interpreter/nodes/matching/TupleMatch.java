package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class TupleMatch extends MatchPattern {

	@Children private final MatchPattern[] elemPatterns;
	private final Class<?> tupleClass;

	public TupleMatch(SourceSection source, MatchPattern[] elemPatterns, Class<?> tupleClass) {
		super(source);
		this.elemPatterns = elemPatterns;
		this.tupleClass = tupleClass;
	}

	@Override
	public void executeMatch(VirtualFrame frame, Object term) {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		final MatchPattern concreteMatch = InterpreterUtils
				.notNull(getContext().getTermRegistry().lookupMatchFactory(tupleClass))
				.apply(getSourceSection(), cloneNodes(elemPatterns));

		replace(concreteMatch).executeMatch(frame, term);
	}

	public static TupleMatch create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedTuple", 2);

		IStrategoList childrenT = Tools.listAt(t, 0);
		MatchPattern[] children = new MatchPattern[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = MatchPattern.create(Tools.applAt(childrenT, i), fd);
		}

		final String dispatchClassName = Tools.stringAt(t, 1).stringValue();
		Class<?> dispatchClass;

		try {
			dispatchClass = Rule.class.getClassLoader().loadClass(dispatchClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dispatchClassName);
		}

		return new TupleMatch(SourceSectionUtil.fromStrategoTerm(t), children, dispatchClass);
	}

}
