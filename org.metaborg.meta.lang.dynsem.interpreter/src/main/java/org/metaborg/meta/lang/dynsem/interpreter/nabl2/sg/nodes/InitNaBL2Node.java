package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2Context;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.NaBL2SolutionUtils;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.ObjectFactories;
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
		NaBL2Context nabl2ctx = nabl2Context();
		if (nabl2ctx != null) {
			IStrategoTerm solution = NaBL2SolutionUtils.getSolution(nabl2ctx);
			DynSemContext ctx = getContext();
			DynamicObject nabl2 = ObjectFactories.createNaBL2((IStrategoAppl) solution, ctx,
					getRootNode().getLanguage(DynSemLanguage.class));
			ctx.setNabl2Solution(nabl2);
		}

	}

}
