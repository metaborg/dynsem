package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerDirectives;
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

}
