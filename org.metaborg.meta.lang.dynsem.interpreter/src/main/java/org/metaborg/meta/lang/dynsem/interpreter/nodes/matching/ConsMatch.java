package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Cons;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.Nil;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ConsMatch extends ListMatchPattern {

	@Child protected MatchPattern headPattern;
	@Child protected MatchPattern tailPattern;

	public ConsMatch(SourceSection source, MatchPattern headPattern, MatchPattern tailPattern) {
		super(source);
		this.headPattern = headPattern;
		this.tailPattern = tailPattern;
	}

	@Specialization
	public boolean doTail(VirtualFrame frame, Cons cons) {
		assert tailPattern != null;
		return headPattern.executeMatch(frame, cons.head()) && tailPattern.executeMatch(frame, cons.tail());
	}

	@Specialization
	public boolean doMismatch(VirtualFrame frame, Nil nil) {
		return false;
	}

}
