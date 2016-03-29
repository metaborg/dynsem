package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IConTerm;

import com.github.krukow.clj_lang.IPersistentStack;
import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

@NodeChildren({ @NodeChild(value = "termBuild", type = TermBuild.class) })
public abstract class TermExpansion extends Node {

	public abstract Object[] execute(VirtualFrame frame);

	private final Assumption conStable = Truffle.getRuntime().createAssumption("ConStable");
	
	@Specialization
	public Object[] doConBuild(final IConTerm term) {
		int arity = term.arity();
		Object[] terms = new Object[arity + 1];
		terms[0] = term;

		System.arraycopy(term.allSubterms(), 0, terms, 1, arity);

		return terms;
	}

	@Specialization
	@SuppressWarnings("rawtypes")
	public Object[] doListBuild(final IPersistentStack list) {
		return new Object[] { list };
	}
}
