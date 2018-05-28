package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class IntLiteralTermMatchPattern extends LiteralMatchPattern {

	protected final int lit;

	public IntLiteralTermMatchPattern(int lit, SourceSection source) {
		super(source);
		this.lit = lit;
	}

	@Specialization
	public void doSuccess(int i) {
		if (i != lit) {
			throw PremiseFailureException.SINGLETON;
		}
	}

}