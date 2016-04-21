package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.HashSet;
import java.util.Set;

import org.metaborg.meta.lang.dynsem.interpreter.RuleRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;
import org.spoofax.terms.util.NotImplementedException;
import org.strategoxt.lang.Context;

import trans.pp_type_0_0;
import trans.rw_type_0_0;
import trans.trans;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Rule extends DynSemNode {

	private final FrameDescriptor fd;
	private final String key;

	public Rule(SourceSection sourceSection, FrameDescriptor fd, String key) {
		super(sourceSection);
		this.fd = fd;
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public abstract RuleResult execute(VirtualFrame frame);

	public FrameDescriptor getFrameDescriptor() {
		return fd;
	}

	public static Rule create(IStrategoTerm t) {
		assert Tools.isTermAppl(t) : "expected application term but got " + t;
		return create((IStrategoAppl) t);
	}

	public static Rule create(IStrategoAppl t) {
		if (Tools.hasConstructor(t, "Rule", 3)) {
			return ReductionRule.create(t);
		} else if (Tools.hasConstructor(t, "RecRule", 3)) {
			return RecursiveReductionRule.create(t);
		}
		throw new NotImplementedException("Unsupported rule term: " + t);
	}

	protected static FrameDescriptor createFrameDescriptor(IStrategoTerm t) {
		Set<String> vars = new HashSet<>();
		TermVisitor visitor = new TermVisitor() {

			@Override
			public void preVisit(IStrategoTerm t) {
				if (Tools.isTermAppl(t) && Tools.hasConstructor((IStrategoAppl) t, "VarRef", 1)) {
					vars.add(Tools.stringAt(t, 0).stringValue());
				}
			}
		};

		visitor.visit(t);
		FrameDescriptor fd = new FrameDescriptor();
		for (String v : vars) {
			fd.addFrameSlot(v);
		}
		return fd;
	}

	protected static String createRuleKey(IStrategoAppl relationT) {
		assert Tools.hasConstructor(relationT, "Relation", 3);

		IStrategoAppl arrowTerm = Tools.applAt(relationT, 1);
		assert Tools.hasConstructor(arrowTerm, "NamedDynamicEmitted", 2);

		String name = Tools.stringAt(arrowTerm, 1).stringValue();

		IStrategoAppl lhsLeftTerm = Tools.applAt(Tools.applAt(relationT, 0), 0);

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

		return RuleRegistry.makeKey(name, constr, arity);
	}

}