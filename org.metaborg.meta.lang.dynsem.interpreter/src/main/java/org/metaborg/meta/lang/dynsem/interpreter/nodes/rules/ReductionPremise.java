package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import metaborg.meta.lang.dynsem.interpreter.terms.IConTerm;
import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;

public class ReductionPremise extends Premise {

	@Children protected final TermBuild[] componentsNodes;

	@Child protected TermBuild lhsNode;

	@Child protected MatchPattern rhsNode;

	@Children protected final MatchPattern[] rhsComponentNodes;

	@CompilationFinal private DynSemContext context;

	public ReductionPremise(TermBuild[] componentsNodes, TermBuild lhsNode,
			TermBuild[] rwNodes, MatchPattern rhsNode,
			MatchPattern[] rhsComponentNodes, SourceSection source) {
		super(source);
		this.componentsNodes = componentsNodes;
		this.lhsNode = lhsNode;
		this.rhsNode = rhsNode;
		this.rhsComponentNodes = rhsComponentNodes;
	}

	@Override
	public void execute(VirtualFrame frame) {
		Object[] componentArgs = evalComponentsNodes(frame);
		IConTerm lshTerm;
		try {
			lshTerm = lhsNode.executeIConTerm(frame);
			Rule targetRule = lookupRule(lshTerm);

			RuleResult ruleRes = (RuleResult) targetRule.getCallTarget().call(
					lshTerm.allSubterms(), componentArgs);
			if (!rhsNode.execute(ruleRes.result, frame)) {
				throw new PremiseFailure();
			}
			if (!evalRhsComponents(ruleRes.components, frame)) {
				throw new PremiseFailure();
			}
		} catch (UnexpectedResultException e) {
			throw new RuntimeException("Cannot reduce on term: "
					+ e.getResult());
		}

	}

	@ExplodeLoop
	private boolean evalRhsComponents(Object[] components, VirtualFrame frame) {
		for (int i = 0; i < rhsComponentNodes.length; i++) {
			if (!rhsComponentNodes[i].execute(components[i], frame)) {
				return false;
			}
		}
		return true;
	}

	@ExplodeLoop
	private Object[] evalComponentsNodes(VirtualFrame frame) {
		Object[] roArgs = new Object[componentsNodes.length];
		for (int i = 0; i < componentsNodes.length; i++) {
			roArgs[i] = componentsNodes[i].executeGeneric(frame);
		}
		return roArgs;
	}

	private Rule lookupRule(ITerm lshTerm) {

		IStrategoAppl appl = (IStrategoAppl) lshTerm;
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
