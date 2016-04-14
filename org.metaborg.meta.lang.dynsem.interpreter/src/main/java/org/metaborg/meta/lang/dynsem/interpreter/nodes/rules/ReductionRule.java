package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.strategoxt.lang.Context;

import trans.pp_type_0_0;
import trans.rw_type_0_0;
import trans.trans;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

/**
 * 
 * A node corresponding to a (merged) DynSem rule.
 * 
 *
 * A rule has inputs of three types: (1) Read-only semantic components (ROs), (2) Pattern-bound variables (PVs), (3)
 * Read-write semantic components (RWs)
 * 
 * These inputs are passed through the arguments array in the following order <ROs,PVs,RWs>. This order coincides with
 * the binding order inside the rule.
 * 
 * @author vladvergu
 *
 */
public class ReductionRule extends Rule {

	private final String name;
	private final String constr;
	private final int arity;

	@Child protected RuleInputsNode inputsNode;

	@Children protected final Premise[] premises;

	@Child protected RuleTarget target;

	public ReductionRule(SourceSection source, FrameDescriptor fd, String name, String constr, int arity,
			RuleInputsNode inputsNode, Premise[] premises, RuleTarget output) {
		super(source, fd);
		this.name = name;
		this.constr = constr;
		this.arity = arity;
		this.inputsNode = inputsNode;
		this.premises = premises;
		this.target = output;
	}

	@Override
	public RuleResult execute(VirtualFrame frame) {
		/* evaluate the inputs node */
		inputsNode.execute(frame);

		/* evaluate the premises */
		evaluatePremises(frame);

		/* evaluate the rule target */
		return target.execute(frame);
	}

	@ExplodeLoop
	private void evaluatePremises(VirtualFrame frame) {
		CompilerAsserts.compilationConstant(premises.length);
		for (int i = 0; i < premises.length; i++) {
			premises[i].execute(frame);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getConstructor() {
		return constr;
	}

	@Override
	public int getArity() {
		return arity;
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return "Reduction rule: " + name + "/" + constr + "/" + arity;
	}

	public static ReductionRule create(IStrategoAppl ruleT) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(ruleT, "Rule", 3) : "Unexpected constructor " + ruleT.getConstructor();

		FrameDescriptor fd = createFrameDescriptor(ruleT);

		IStrategoList premisesTerm = Tools.listAt(ruleT, 0);
		Premise[] premises = new Premise[premisesTerm.size()];
		for (int i = 0; i < premises.length; i++) {
			premises[i] = Premise.create(Tools.applAt(premisesTerm, i), fd);
		}

		IStrategoAppl relationT = Tools.applAt(ruleT, 2);
		assert Tools.hasConstructor(relationT, "Relation", 3);

		IStrategoAppl arrowTerm = Tools.applAt(relationT, 1);
		assert Tools.hasConstructor(arrowTerm, "NamedDynamicEmitted", 2);

		String name = Tools.stringAt(arrowTerm, 1).stringValue();

		IStrategoAppl lhsSourceTerm = Tools.applAt(relationT, 0);
		IStrategoAppl lhsLeftTerm = Tools.applAt(lhsSourceTerm, 0);
		IStrategoList lhsCompsTerm = Tools.listAt(lhsSourceTerm, 1);

		IStrategoAppl lhsConTerm = null;

		if (Tools.hasConstructor(lhsLeftTerm, "As", 2)) {
			lhsConTerm = Tools.applAt(lhsLeftTerm, 1);
		} else {
			lhsConTerm = lhsLeftTerm;
		}

		String constr = null;
		int arity;
		if (Tools.hasConstructor(lhsConTerm, "Con", 2)) {
			assert lhsConTerm != null && Tools.hasConstructor(lhsConTerm, "Con", 2);
			lhsConTerm = lhsLeftTerm;
			constr = Tools.stringAt(lhsConTerm, 0).stringValue();
			arity = Tools.listAt(lhsConTerm, 1).size();
		} else if (Tools.hasConstructor(lhsConTerm, "Cast", 2)) {
			IStrategoAppl tyTerm = Tools.applAt(lhsConTerm, 1);
			assert Tools.hasConstructor(tyTerm, "ListSort", 1);
			Context ctx = trans.init();
			constr = "_"
					+ Tools.asJavaString(pp_type_0_0.instance.invoke(ctx, rw_type_0_0.instance.invoke(ctx, tyTerm)));
			arity = 1;
		} else {
			throw new RuntimeException("Unsupported rule LHS: " + lhsLeftTerm);
		}

		RuleTarget target = RuleTarget.create(Tools.applAt(relationT, 2), fd);

		return new ReductionRule(SourceSectionUtil.fromStrategoTerm(ruleT), fd, name, constr, arity,
				RuleInputsNode.create(lhsConTerm, lhsCompsTerm, fd), premises, target);
	}
}
