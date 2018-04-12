package org.metaborg.meta.lang.dynsem.interpreter.nabl2;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

public class InitNaBL2Node extends DynSemNode {

	public InitNaBL2Node(SourceSection source) {
		super(source);
	}

	public void execute(VirtualFrame frame) {
		final DynSemContext ctx = getContext();
		IStrategoTerm solution = NaBL2SolutionUtils.getSolution(nabl2Context());
		DynamicObject nabl2 = ObjectFactories.createNaBL2((IStrategoAppl) solution, ctx);
		ctx.setNabl2(nabl2);
	}

}
