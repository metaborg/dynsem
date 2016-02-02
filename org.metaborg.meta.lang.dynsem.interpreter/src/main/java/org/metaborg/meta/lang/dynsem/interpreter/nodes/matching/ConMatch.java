package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.frame.VirtualFrame;
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
	public boolean execute(Object term, VirtualFrame frame) {
		ITermMatchPatternFactory<MatchPattern> matchFactory = getContext()
				.lookupMatchPattern(name, children.length);
		MatchPattern matcher = matchFactory.apply(getSourceSection(), children);
		return replace(matcher).execute(term, frame);
	}
}
