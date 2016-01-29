package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class ReductionPremise extends Premise {

	@Children protected final TermBuild[] componentsNodes;

	@Child protected TermBuild reductionNode;

	@Child protected MatchPattern rhs;

	@CompilationFinal private DynSemContext context;

	public ReductionPremise(TermBuild[] componentsNodes, TermBuild inputNode,
			TermBuild[] rwNodes, MatchPattern rhs, SourceSection source) {
		super(source);
		this.componentsNodes = componentsNodes;
		this.reductionNode = inputNode;
		this.rhs = rhs;
	}

	@Override
	public void execute(VirtualFrame frame) {
		IStrategoTerm[] componentArgs = evalComponentsNodes(frame);
		IStrategoTerm reductionTerm = reductionNode.executeGeneric(frame);

		if (!Tools.isTermAppl(reductionTerm)) {
			throw new RuntimeException("Cannot reduce on term: "
					+ reductionTerm);
		}

		Rule targetRule = lookupRule(reductionTerm);
		IStrategoTerm resTrm = (IStrategoTerm) targetRule.getCallTarget().call(
				reductionTerm.getAllSubterms(), componentArgs);

		rhs.execute(resTrm, frame);
	}

	@ExplodeLoop
	private IStrategoTerm[] evalComponentsNodes(VirtualFrame frame) {
		IStrategoTerm[] roArgs = new IStrategoTerm[componentsNodes.length];
		for (int i = 0; i < componentsNodes.length; i++) {
			roArgs[i] = componentsNodes[i].executeGeneric(frame);
		}
		return roArgs;
	}

	private Rule lookupRule(IStrategoTerm reductionTerm) {
		IStrategoAppl appl = (IStrategoAppl) reductionTerm;
		String ctorName = appl.getConstructor().getName();
		int ctorAppl = appl.getSubtermCount();

		if (context == null) {
			context = DynSemLanguage.INSTANCE
					.findContext0(DynSemLanguage.INSTANCE
							.createFindContextNode0());
		}

		return context.lookupRule(ctorName, ctorAppl);
	}

}
