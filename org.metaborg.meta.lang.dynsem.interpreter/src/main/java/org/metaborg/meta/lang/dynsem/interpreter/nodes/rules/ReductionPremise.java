package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import metaborg.meta.lang.dynsem.interpreter.terms.IConTerm;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;

public class ReductionPremise extends Premise {

	@Children protected final TermBuild[] rorwNodes;

	@Child protected TermBuild lhsNode;

	@Child protected MatchPattern rhsNode;

	@Children protected final MatchPattern[] rhsRwNodes;

	@CompilationFinal private DynSemContext context;

	public ReductionPremise(TermBuild[] rorwNodes, TermBuild lhsNode,
			MatchPattern rhsNode, MatchPattern[] rhsComponentNodes,
			SourceSection source) {
		super(source);
		this.rorwNodes = rorwNodes;
		this.lhsNode = lhsNode;
		this.rhsNode = rhsNode;
		this.rhsRwNodes = rhsComponentNodes;
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
		for (int i = 0; i < rhsRwNodes.length; i++) {
			if (!rhsRwNodes[i].execute(components[i], frame)) {
				return false;
			}
		}
		return true;
	}

	@ExplodeLoop
	private Object[] evalComponentsNodes(VirtualFrame frame) {
		Object[] roArgs = new Object[rorwNodes.length];
		for (int i = 0; i < rorwNodes.length; i++) {
			roArgs[i] = rorwNodes[i].executeGeneric(frame);
		}
		return roArgs;
	}

	private Rule lookupRule(IConTerm lshTerm) {
		if (context == null) {
			context = DynSemContext.LANGUAGE
					.findContext0(DynSemContext.LANGUAGE
							.createFindContextNode0());
		}

		return context.lookupRule(lshTerm.constructor(), lshTerm.arity());
	}

	public static ReductionPremise create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "Relation", 4);

		IStrategoList rosT = Tools.listAt(Tools.applAt(t, 0), 0);
		TermBuild[] roNodes = new TermBuild[rosT.getSubtermCount()];
		for (int i = 0; i < roNodes.length; i++) {
			roNodes[i] = TermBuild.createFromLabelComp(Tools.applAt(rosT, i),
					fd);
		}

		IStrategoAppl sourceT = Tools.applAt(t, 1);
		assert Tools.hasConstructor(sourceT, "Source", 2);
		TermBuild lhsNode = TermBuild.create(Tools.applAt(sourceT, 0), fd);

		IStrategoList rwsT = Tools.listAt(sourceT, 1);
		TermBuild[] rwNodes = new TermBuild[rwsT.getSubtermCount()];
		for (int i = 0; i < rwNodes.length; i++) {
			rwNodes[i] = TermBuild.createFromLabelComp(Tools.applAt(rwsT, i),
					fd);
		}

		TermBuild[] rorwNodes = new TermBuild[roNodes.length + rwNodes.length];
		System.arraycopy(roNodes, 0, rorwNodes, 0, roNodes.length);
		System.arraycopy(rwNodes, 0, rorwNodes, roNodes.length, rwNodes.length);

		IStrategoAppl targetT = Tools.applAt(t, 3);
		assert Tools.hasConstructor(targetT, "Target", 2);
		MatchPattern rhsNode = MatchPattern
				.create(Tools.applAt(targetT, 0), fd);

		IStrategoList rhsRwsT = Tools.listAt(targetT, 1);
		MatchPattern[] rhsRwNodes = new MatchPattern[rhsRwsT.size()];
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i] = MatchPattern.create(Tools.applAt(rhsRwsT, i), fd);
		}

		return new ReductionPremise(rorwNodes, lhsNode, rhsNode, rhsRwNodes,
				SourceSectionUtil.fromStrategoTerm(t));
	}
}
