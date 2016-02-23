package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IConTerm;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;

/**
 * {@link ReductionPremise} represents and specifies evaluation logic for a
 * reduction premise, i.e. a premise which applies a rule to a term.
 * 
 * @author vladvergu
 *
 */
public class ReductionPremise extends Premise {

	@Children protected final TermBuild[] roNodes;

	@Child protected TermBuild lhsNode;

	@Children protected final TermBuild[] rwNodes;

	private final String arrowName;

	@Child protected MatchPattern rhsNode;

	@Children protected final MatchPattern[] rhsRwNodes;

	@CompilationFinal private DynSemContext context;

	public ReductionPremise(TermBuild[] roNodes, TermBuild lhsNode,
			String arrowName, TermBuild[] rwNodes, MatchPattern rhsNode,
			MatchPattern[] rhsComponentNodes, SourceSection source) {
		super(source);
		this.roNodes = roNodes;
		this.lhsNode = lhsNode;
		this.arrowName = arrowName;
		this.rwNodes = rwNodes;
		this.rhsNode = rhsNode;
		this.rhsRwNodes = rhsComponentNodes;
	}

	/**
	 * The {@link #execute(VirtualFrame)} function first evaluates the
	 * {@link #roNodes} using the {@link #evalRWNodes(VirtualFrame)} helper
	 * function. Then {@link #rwNodes} are evaluated. Then the {@link #lhsNode}
	 * representing the input term to the reduction is evaluated. The target
	 * rule to apply is found based on the term to reduce on. Following
	 * application of the rule the local variables are bound to values in the
	 * {@link RuleResult}.
	 * 
	 * @Override
	 */
	public void execute(VirtualFrame frame) {
		Object[] roArgs = evalRONodes(frame);
		Object[] rwArgs = evalRWNodes(frame);
		IConTerm lhsTerm;
		try {
			lhsTerm = lhsNode.executeIConTerm(frame);
			Rule targetRule = lookupRule(lhsTerm);
			
			Object[] args = Rule.buildArguments(lhsTerm, lhsTerm.allSubterms(), roArgs, rwArgs);
			RuleResult ruleRes = (RuleResult) targetRule.getCallTarget().call(args);
			
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
	private Object[] evalRONodes(VirtualFrame frame) {
		Object[] roArgs = new Object[roNodes.length];
		for (int i = 0; i < roNodes.length; i++) {
			roArgs[i] = roNodes[i].executeGeneric(frame);
		}
		return roArgs;
	}

	@ExplodeLoop
	private Object[] evalRWNodes(VirtualFrame frame) {
		Object[] rwArgs = new Object[rwNodes.length];
		for (int i = 0; i < rwNodes.length; i++) {
			rwArgs[i] = rwNodes[i].executeGeneric(frame);
		}
		return rwArgs;
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

	private Rule lookupRule(IConTerm lshTerm) {
		if (context == null) {
			context = DynSemContext.LANGUAGE
					.findContext0(DynSemContext.LANGUAGE
							.createFindContextNode0());
		}

		return context.getRuleRegistry().lookupRule(arrowName,
				lshTerm.constructor(), lshTerm.arity());
	}

	public static ReductionPremise create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "Relation", 4);

		IStrategoList rosT = Tools.listAt(Tools.applAt(t, 0), 0);
		TermBuild[] roNodes = new TermBuild[rosT.getSubtermCount()];
		for (int i = 0; i < roNodes.length; i++) {
			roNodes[i] = TermBuild.createFromLabelComp(Tools.applAt(rosT, i),
					fd);
		}

		IStrategoAppl arrowTerm = Tools.applAt(t, 2);
		assert Tools.hasConstructor(arrowTerm, "NamedDynamicEmitted", 2);

		String arrowName = Tools.stringAt(arrowTerm, 1).stringValue();

		IStrategoAppl sourceT = Tools.applAt(t, 1);
		assert Tools.hasConstructor(sourceT, "Source", 2);
		TermBuild lhsNode = TermBuild.create(Tools.applAt(sourceT, 0), fd);

		IStrategoList rwsT = Tools.listAt(sourceT, 1);
		TermBuild[] rwNodes = new TermBuild[rwsT.getSubtermCount()];
		for (int i = 0; i < rwNodes.length; i++) {
			rwNodes[i] = TermBuild.createFromLabelComp(Tools.applAt(rwsT, i),
					fd);
		}

		IStrategoAppl targetT = Tools.applAt(t, 3);
		assert Tools.hasConstructor(targetT, "Target", 2);
		MatchPattern rhsNode = MatchPattern
				.create(Tools.applAt(targetT, 0), fd);

		IStrategoList rhsRwsT = Tools.listAt(targetT, 1);
		MatchPattern[] rhsRwNodes = new MatchPattern[rhsRwsT.size()];
		for (int i = 0; i < rhsRwNodes.length; i++) {
			rhsRwNodes[i] = MatchPattern.createFromLabelComp(
					Tools.applAt(rhsRwsT, i), fd);
		}

		return new ReductionPremise(roNodes, lhsNode, arrowName, rwNodes,
				rhsNode, rhsRwNodes, SourceSectionUtil.fromStrategoTerm(t));
	}
	
	@Override
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}
}
