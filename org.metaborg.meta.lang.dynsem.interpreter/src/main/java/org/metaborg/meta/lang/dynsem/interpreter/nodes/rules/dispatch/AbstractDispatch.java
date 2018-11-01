package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.source.SourceSection;

public abstract class AbstractDispatch extends DynSemNode {

	protected final String arrowName;

	public AbstractDispatch(SourceSection source, String arrowName) {
		super(source);
		this.arrowName = arrowName;
	}

	public abstract RuleResult execute(Object[] args);

}
