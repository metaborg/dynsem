package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import trans.pp_type_0_0;
import trans.trans;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

/**
 * {@link ListReductionPremise} represents and specifies evaluation logic for a
 * list reduction premise, i.e. a premise which applies a rule to a term of type list.
 * 
 * @author vladvergu
 *
 */
public class ListReductionPremise extends Premise {

	@Child protected RelationAppLhs lhsNode;

	private final String ruleKey;
	private final String arrowName;

	@CompilationFinal private DirectCallNode callNode;

	@Child protected MatchPattern rhsNode;

	@Children protected final MatchPattern[] rhsRwNodes;

	@CompilationFinal private DynSemContext context;

	public ListReductionPremise(TermBuild[] roNodes, TermBuild lhsNode,
			String arrowName, TermBuild[] rwNodes, MatchPattern rhsNode,
			MatchPattern[] rhsComponentNodes, String ruleKey,
			SourceSection source) {
		super(source);
		this.lhsNode = new RelationAppLhs(lhsNode, roNodes, rwNodes,
				source);
		this.rhsNode = rhsNode;
		this.rhsRwNodes = rhsComponentNodes;
		this.arrowName = arrowName;
		this.ruleKey = ruleKey;
	}

	@Override
	public void execute(VirtualFrame frame) {

		Object[] args = lhsNode.executeObjectArray(frame);

		if (callNode == null) {
			callNode = DirectCallNode.create(DynSemContext.LANGUAGE
					.getContext().getRuleRegistry()
					.lookupRule(arrowName, ruleKey, 1).getCallTarget());
		}

		RuleResult ruleRes = (RuleResult) callNode.call(frame, args);

		if (!rhsNode.execute(ruleRes.result, frame)) {
			throw new PremiseFailure();
		}
		if (!evalRhsComponents(ruleRes.components, frame)) {
			throw new PremiseFailure();
		}
	}

	@ExplodeLoop
	private boolean evalRhsComponents(Object[] components, VirtualFrame frame) {
		CompilerAsserts.compilationConstant(rhsRwNodes.length);
		for (int i = 0; i < rhsRwNodes.length; i++) {
			if (!rhsRwNodes[i].execute(components[i], frame)) {
				return false;
			}
		}
		return true;
	}

	public static ListReductionPremise create(IStrategoAppl t,
			FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "ListRelation", 5);

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

		String key = "_"
				+ Tools.asJavaString(pp_type_0_0.instance.invoke(trans.init(),
						Tools.termAt(t, 4)));

		return new ListReductionPremise(roNodes, lhsNode, arrowName, rwNodes,
				rhsNode, rhsRwNodes, key, SourceSectionUtil.fromStrategoTerm(t));
	}

	@Override
	public String toString() {
		return "--" + arrowName + "--> "
				+ NodeUtil.printCompactTreeToString(this);
	}
}
