package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.github.krukow.clj_lang.IPersistentStack;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

@NodeChildren({ @NodeChild(value = "termBuild", type = TermBuild.class) })
@Deprecated
public abstract class TermExpansion extends Node {

	public abstract Object[] execute(VirtualFrame frame);

	@Specialization(guards="term == cachedTerm")
	public Object[] doConBuild(final ITerm term, @Cached("term") ITerm cachedTerm) {
		return doConBuildForReal(cachedTerm);
	}

	@Specialization(contains="doConBuild")
	public Object[] doConBuildCacheMiss(final ITerm term){
		return doConBuildForReal(term);
	}
	
	private Object[] doConBuildForReal(ITerm cachedTerm) {
		int arity = cachedTerm.arity();
		Object[] terms = new Object[arity + 1];
		terms[0] = cachedTerm;

		System.arraycopy(cachedTerm.allSubterms(), 0, terms, 1, arity);

		return terms;
	}

	@Specialization
	public Object[] doListBuild(@SuppressWarnings("rawtypes") final IPersistentStack list) {
		return new Object[] { list };
	}
}
