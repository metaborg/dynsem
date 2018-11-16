package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public final class NoOpPattern extends MatchPattern {

	public NoOpPattern(SourceSection source) {
		super(source);
	}

	@Override
	public void executeMatch(VirtualFrame frame, Object term) {
		// intentionally blank
	}

}
