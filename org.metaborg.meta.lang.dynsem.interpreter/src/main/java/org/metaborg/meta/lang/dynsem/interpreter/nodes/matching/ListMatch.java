package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class ListMatch extends MatchPattern {

	@Children private final MatchPattern[] elemPatterns;
	@Child private MatchPattern tailPattern;
	private final Class<?> listClass;

	public ListMatch(SourceSection source, MatchPattern[] elemPatterns, MatchPattern tailPattern, Class<?> listClass) {
		super(source);
		this.elemPatterns = elemPatterns;
		this.tailPattern = tailPattern;
		this.listClass = listClass;
	}

	@Override
	public void executeMatch(VirtualFrame frame, Object term) {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		final MatchPattern concreteMatch = InterpreterUtils
				.notNull(getContext().getTermRegistry().lookupMatchFactory(listClass))
				.apply(getSourceSection(), cloneNodes(elemPatterns), cloneNode(tailPattern));

		replace(concreteMatch).executeMatch(frame, term);
	}

	public static ListMatch create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "TypedList", 2) || Tools.hasConstructor(t, "TypedListTail", 3);

		IStrategoList elemTs = Tools.listAt(t, 0);
		final MatchPattern[] elemPatterns = new MatchPattern[elemTs.size()];
		for (int i = 0; i < elemPatterns.length; i++) {
			elemPatterns[i] = MatchPattern.create(Tools.applAt(elemTs, i), fd);
		}

		MatchPattern tailPattern = null;
		if (Tools.hasConstructor(t, "TypedListTail", 3)) {
			tailPattern = MatchPattern.create(Tools.applAt(t, 1), fd);
		}

		final String dispatchClassName = Tools.javaStringAt(t, Tools.hasConstructor(t, "TypedList", 2) ? 1 : 2);
		Class<?> dispatchClass;

		try {
			dispatchClass = Rule.class.getClassLoader().loadClass(dispatchClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dispatchClassName);
		}

		return new ListMatch(SourceSectionUtil.fromStrategoTerm(t), elemPatterns, tailPattern, dispatchClass);
	}

}
