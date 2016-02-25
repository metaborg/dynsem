package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.HashSet;
import java.util.Set;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

/**
 * 
 * A node corresponding to a (merged) DynSem rule.
 * 
 *
 * A rule has inputs of three types: (1) Read-only semantic components (ROs),
 * (2) Pattern-bound variables (PVs), (3) Read-write semantic components (RWs)
 * 
 * These inputs are passed through the arguments array in the following order
 * <ROs,PVs,RWs>. This order coincides with the binding order inside the rule.
 * 
 * @author vladvergu
 *
 */
public class ReductionRule extends Rule {

	private final String name;
	private final String constr;
	private final int arity;

	@Children protected final Premise[] premises;

	@Child protected RuleTarget target;

	public ReductionRule(String name, String constr, int arity,
			Premise[] premises, RuleTarget output, SourceSection source,
			FrameDescriptor fd) {
		super(source, fd);
		this.name = name;
		this.constr = constr;
		this.arity = arity;
		this.premises = premises;
		this.target = output;
	}

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		/* evaluate the premises */
		for (int i = 0; i < premises.length; i++) {
			premises[i].execute(frame);
		}

		/* evaluate the rule target */
		return target.execute(frame);
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

	public static ReductionRule create(IStrategoTerm ruleT) {
		assert Tools.isTermAppl(ruleT);
		assert Tools.hasConstructor((IStrategoAppl) ruleT, "Rule", 3);

		FrameDescriptor fd = createFrameDescriptor(ruleT);

		IStrategoList premisesTerm = Tools.listAt(ruleT, 0);
		Premise[] premises = new Premise[premisesTerm.size()];
		for (int i = 0; i < premises.length; i++) {
			premises[i] = Premise.create(Tools.applAt(premisesTerm, i), fd);
		}

		IStrategoAppl relationT = Tools.applAt(ruleT, 2);
		assert Tools.hasConstructor(relationT, "Relation", 4);

		IStrategoAppl arrowTerm = Tools.applAt(relationT, 2);
		assert Tools.hasConstructor(arrowTerm, "NamedDynamicEmitted", 2);

		String name = Tools.stringAt(arrowTerm, 1).stringValue();

		IStrategoAppl lhsLeftTerm = Tools.applAt(Tools.applAt(relationT, 1), 0);
		IStrategoAppl lhsConTerm = null;

		// FIXME this should be done differently perhaps through desugaring of
		// the spec to bring the constructor name and arity outwards
		if (Tools.hasConstructor(lhsLeftTerm, "As", 2)) {
			lhsConTerm = Tools.applAt(lhsLeftTerm, 1);
		} else {
			lhsConTerm = lhsLeftTerm;
		}

		assert lhsConTerm != null && Tools.hasConstructor(lhsConTerm, "Con", 2);
		String constr = Tools.stringAt(lhsConTerm, 0).stringValue();
		int arity = Tools.listAt(lhsConTerm, 1).size();

		RuleTarget target = RuleTarget.create(Tools.applAt(relationT, 3), fd);

		return new ReductionRule(name, constr, arity, premises, target,
				SourceSectionUtil.fromStrategoTerm(ruleT), fd);
	}

	private static FrameDescriptor createFrameDescriptor(IStrategoTerm t) {
		Set<String> vars = new HashSet<>();
		TermVisitor visitor = new TermVisitor() {

			@Override
			public void preVisit(IStrategoTerm t) {
				if (Tools.isTermAppl(t)
						&& Tools.hasConstructor((IStrategoAppl) t, "VarRef", 1)) {
					vars.add(Tools.stringAt(t, 0).stringValue());
				}
			}
		};

		visitor.visit(t);
		FrameDescriptor fd = FrameDescriptor.create();
		for (String v : vars) {
			fd.addFrameSlot(v);
		}
		return fd;
	}

	@Override
	public String toString() {
		return name + "/" + constr + "/" + arity + " "
				+ NodeUtil.printCompactTreeToString(this);
	}

}
