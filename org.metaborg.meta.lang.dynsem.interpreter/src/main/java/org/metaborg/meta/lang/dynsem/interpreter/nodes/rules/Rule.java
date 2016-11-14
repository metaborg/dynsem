package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.HashSet;
import java.util.Set;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Rule extends DynSemNode {

	private final FrameDescriptor fd;
	private final String arrowName;
	private final RuleKind kind;
	private final Class<?> dispatchClass;

	public Rule(SourceSection sourceSection, FrameDescriptor fd, RuleKind kind, String arrowName,
			Class<?> dispatchClass) {
		super(sourceSection);
		this.fd = fd;
		this.arrowName = arrowName;
		this.kind = kind;
		this.dispatchClass = dispatchClass;
	}

	public String getArrowName() {
		return arrowName;
	}

	public RuleKind getKind() {
		return kind;
	}

	public Class<?> getDispatchClass() {
		return dispatchClass;
	}

	public abstract RuleResult execute(VirtualFrame frame);

	public FrameDescriptor getFrameDescriptor() {
		return fd;
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return "Reduction rule <" + getArrowName() + "> on <" + dispatchClass.getName() + ">";
	}

	public static Rule create(IStrategoTerm t) {
		assert Tools.isTermAppl(t) : "expected application term but instead got a " + t;
		return create((IStrategoAppl) t);
	}

	public static Rule create(IStrategoAppl ruleT) {
		CompilerAsserts.neverPartOfCompilation();

		assert Tools.hasConstructor(ruleT, "Rule", 5) : "Unexpected constructor " + ruleT.getConstructor();

		FrameDescriptor fd = createFrameDescriptor(ruleT);

		IStrategoList premisesTerm = Tools.listAt(ruleT, 0);
		Premise[] premises = new Premise[premisesTerm.size()];
		for (int i = 0; i < premises.length; i++) {
			premises[i] = Premise.create(Tools.applAt(premisesTerm, i), fd);
		}

		IStrategoAppl relationT = Tools.applAt(ruleT, 2);
		assert Tools.hasConstructor(relationT, "Relation", 3);
		IStrategoAppl lhsSourceTerm = Tools.applAt(relationT, 0);
		IStrategoAppl lhsLeftTerm = Tools.applAt(lhsSourceTerm, 0);
		IStrategoList lhsCompsTerm = Tools.listAt(lhsSourceTerm, 1);

		IStrategoAppl arrowTerm = Tools.applAt(relationT, 1);

		String arrowName = Tools.javaStringAt(arrowTerm, 1);

		IStrategoAppl lhsConTerm = null;

		if (Tools.hasConstructor(lhsLeftTerm, "As", 2)) {
			lhsConTerm = Tools.applAt(lhsLeftTerm, 1);
		} else {
			lhsConTerm = lhsLeftTerm;
		}

		RuleTarget target = RuleTarget.create(Tools.applAt(relationT, 2), fd);

		RuleKind kind = readRuleKind(Tools.applAt(ruleT, 3));
		String dispatchClassName = Tools.javaStringAt(ruleT, 4);
		Class<?> dispatchClass;

		try {
			dispatchClass = Rule.class.getClassLoader().loadClass(dispatchClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dispatchClassName);
		}

		if (Tools.hasConstructor(ruleT, "Rule", 5)) {

			return new ReductionRule(SourceSectionUtil.fromStrategoTerm(ruleT), fd, kind, arrowName, dispatchClass,
					RuleInputsNode.create(lhsConTerm, lhsCompsTerm, fd), premises, target);
		}

		throw new NotImplementedException("Unsupported rule term: " + ruleT);
	}

	private static RuleKind readRuleKind(IStrategoAppl flag) {
		if (Tools.hasConstructor(flag, "TermKind", 0)) {
			return RuleKind.TERM;
		}
		if (Tools.hasConstructor(flag, "SortKind", 0)) {
			return RuleKind.SORT;
		}
		if (Tools.hasConstructor(flag, "ASTKind", 0)) {
			return RuleKind.AST;
		}
		if (Tools.hasConstructor(flag, "PrimitiveKind", 0)) {
			return RuleKind.PRIMITIVE;
		}
		if (Tools.hasConstructor(flag, "NativeKind", 0)) {
			return RuleKind.NATIVETYPE;
		}
		if (Tools.hasConstructor(flag, "ListKind", 0)) {
			return RuleKind.LIST;
		}
		if (Tools.hasConstructor(flag, "TupleKind", 0)) {
			return RuleKind.TUPLE;
		}
		if (Tools.hasConstructor(flag, "MapKind", 0)) {
			return RuleKind.MAP;
		}
		throw new RuntimeException("Unsupported rule kind flag: " + flag);
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

}