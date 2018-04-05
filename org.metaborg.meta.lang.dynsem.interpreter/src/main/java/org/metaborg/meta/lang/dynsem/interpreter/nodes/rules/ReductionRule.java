package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.HashSet;
import java.util.Set;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.Premise;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class ReductionRule extends Rule {

	public final static String DEFAULT_NAME = "";

	private final String arrowName;
	private final Class<?> dispatchClass;

	@Child protected RuleInputsNode inputsNode;

	@Children protected final Premise[] premises;

	@Child protected RuleTarget target;

	private final IStrategoAppl sourceCode;

	public ReductionRule(DynSemLanguage lang, SourceSection source, FrameDescriptor fd, String arrowName,
			Class<?> dispatchClass, RuleInputsNode inputsNode, Premise[] premises, RuleTarget output,
			IStrategoAppl sourceCode) {
		super(lang, source, fd);
		this.arrowName = arrowName;
		this.dispatchClass = dispatchClass;
		this.inputsNode = inputsNode;
		this.premises = premises;
		this.target = output;
		this.sourceCode = sourceCode;
		adoptChildren();
		Truffle.getRuntime().createCallTarget(this);
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
	protected ReductionRule cloneUninitialized() {
		return createWithFrameDescriptor(language(), sourceCode, getFrameDescriptor());
	}

	public String getArrowName() {
		return arrowName;
	}

	public Class<?> getDispatchClass() {
		return dispatchClass;
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return dispatchClass.getSimpleName() + " -" + getArrowName() + "->";
	}

	public static ReductionRule create(DynSemLanguage lang, IStrategoAppl ruleT) {
		CompilerAsserts.neverPartOfCompilation();
		return createWithFrameDescriptor(lang, ruleT, createFrameDescriptor(ruleT));
	}

	public static ReductionRule createWithFrameDescriptor(DynSemLanguage lang, IStrategoAppl ruleT,
			FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();

		assert Tools.hasConstructor(ruleT, "Rule", 5) : "Unexpected constructor " + ruleT.getConstructor();

		IStrategoList premisesTerm = Tools.listAt(ruleT, 0);
		Premise[] premises = new Premise[premisesTerm.size()];
		for (int i = 0; i < premises.length; i++) {
			premises[i] = Premise.create(lang, Tools.applAt(premisesTerm, i), fd);
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

		String dispatchClassName = Tools.javaStringAt(ruleT, 4);
		Class<?> dispatchClass;

		try {
			dispatchClass = ReductionRule.class.getClassLoader().loadClass(dispatchClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dispatchClassName);
		}

		if (Tools.hasConstructor(ruleT, "Rule", 5)) {

			return new ReductionRule(lang, SourceUtils.dynsemSourceSectionFromATerm(ruleT), fd, arrowName,
					dispatchClass, RuleInputsNode.create(lhsConTerm, lhsCompsTerm, fd), premises, target, ruleT);
		}

		throw new NotImplementedException("Unsupported rule term: " + ruleT);
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
