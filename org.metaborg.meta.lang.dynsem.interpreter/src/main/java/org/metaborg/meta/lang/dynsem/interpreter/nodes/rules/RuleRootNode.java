package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.HashSet;
import java.util.Set;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops.WhileNode;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class RuleRootNode extends DynSemRootNode {
	private final IStrategoAppl[] sourceTerms;
	private final String arrowName;
	private final Class<?> dispatchClass;

	@Child private Rule rule;

	private DynSemLanguage lang;

	public RuleRootNode(DynSemLanguage lang, SourceSection sourceSection, FrameDescriptor frameDescriptor,
			String arrowName, Class<?> dispatchClass, Rule rule, IStrategoAppl[] ruleTs) {
		super(lang, sourceSection, frameDescriptor, Truffle.getRuntime()
				.createAssumption("constant input for " + dispatchClass.getSimpleName() + " -" + arrowName + "->"));
		this.lang = lang;
		this.arrowName = arrowName;
		this.dispatchClass = dispatchClass;
		this.rule = rule;
		this.sourceTerms = ruleTs;
	}

	@Override
	public abstract RuleResult execute(VirtualFrame frame);

	@Specialization(guards = "guardCheck(frame, inputTerm)", assumptions = "getConstantTermAssumption()", limit = "1")
	public RuleResult doHasSeenStatic(VirtualFrame frame, @Cached("getInputTerm(frame)") Object inputTerm) {
		return rule.evaluateRule(frame);
	}

	@Specialization(replaces = "doHasSeenStatic")
	public RuleResult doHasSeenDynamic(VirtualFrame frame) {
		return rule.evaluateRule(frame);
	}

	protected boolean guardCheck(VirtualFrame frame, Object refTerm) {
		Assumption assumption = getConstantTermAssumption();
		if (!assumption.isValid()) {
			return false;
		}
		if (getInputTerm(frame) != refTerm) {
			assumption.invalidate();
			return false;
		}
		return true;
	}

	protected static Object getInputTerm(VirtualFrame frame) {
		return frame.getArguments()[0];
	}

	public String getArrowName() {
		return arrowName;
	}

	public Class<?> getDispatchClass() {
		return dispatchClass;
	}

	public Rule getRuleNode() {
		return rule;
	}

	public IStrategoAppl[] getSourceATerms() {
		return sourceTerms;
	}

	@Override
	public boolean isCloningAllowed() {
		return true;
	}

	@Override
	protected boolean isCloneUninitializedSupported() {
		return true;
	}

	@Override
	protected RuleRootNode cloneUninitialized() {
		FrameDescriptor fd = getFrameDescriptor();
		return createFromATerms(lang, sourceTerms, fd, getContext().getTermRegistry());
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return dispatchClass.getSimpleName() + " -" + getArrowName() + "->";
	}

	@TruffleBoundary
	public static RuleRootNode createFromATerms(DynSemLanguage lang, IStrategoAppl[] ruleTs, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		return createFromATerms(lang, ruleTs, createFrameDescriptor(ruleTs), termReg);
	}

	@TruffleBoundary
	public static RuleRootNode createFromATerms(DynSemLanguage lang, IStrategoAppl[] ruleTs, FrameDescriptor fd,
			ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert ruleTs.length > 0;
		IStrategoAppl tmp = ruleTs[0];
		String arrName = readArrowNameFromATerm(tmp);
		Class<?> dispatchClass = readDispatchClassFromATerm(tmp);

		SingleRule[] rules = new SingleRule[ruleTs.length];
		for (int i = 0; i < ruleTs.length; i++) {
			IStrategoAppl ruleT = ruleTs[i];

			rules[i] = SingleRule.createFromATerm(lang, ruleT, fd, termReg);
		}
		SourceSection src = rules[0].getSourceSection();
		if (rules.length == 1) {
			return RuleRootNodeGen.create(lang, src, fd, arrName, dispatchClass, rules[0],
					ruleTs);
		} else {
			return RuleRootNodeGen.create(lang, src, fd, arrName, dispatchClass, new MultiRule(src, rules), ruleTs);
		}
	}

	@TruffleBoundary
	public static Class<?> readDispatchClassFromATerm(IStrategoAppl ruleTerm) {
		String dspClass = Tools.javaStringAt(ruleTerm, 4);
		try {
			return RuleRootNode.class.getClassLoader().loadClass(dspClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dspClass);
		}
	}

	public static String readArrowNameFromATerm(IStrategoAppl ruleTerm) {
		return Tools.javaStringAt(Tools.applAt(Tools.applAt(ruleTerm, 2), 1), 1);
	}

	@TruffleBoundary
	protected static FrameDescriptor createFrameDescriptor(IStrategoAppl[] ts) {
		final FrameDescriptor fd = new FrameDescriptor();
		final Set<Integer> vars = new HashSet<>();
		TermVisitor visitor = new TermVisitor() {

			@Override
			@TruffleBoundary
			public void preVisit(IStrategoTerm t) {
				CompilerAsserts.neverPartOfCompilation();
				if (Tools.isTermAppl(t) && (Tools.hasConstructor((IStrategoAppl) t, "VarRef", 1)
						|| Tools.hasConstructor((IStrategoAppl) t, "ConstRef", 1))) {
					String vstr = Tools.stringAt(t, 0).stringValue();
					int v = vstr.intern().hashCode();
					if (!vars.contains(v)) {
						fd.addFrameSlot(v, false, FrameSlotKind.Object);
						vars.add(v);
					}
				} else if (Tools.isTermAppl(t) && Tools.hasConstructor((IStrategoAppl) t, "CountedWhileNode", 5)) {
					fd.addFrameSlot(WhileNode.genComponentsFrameSlotName(Tools.javaIntAt(t, 0)), FrameSlotKind.Object);
					fd.addFrameSlot(WhileNode.genResultFrameSlotName(Tools.javaIntAt(t, 0)), FrameSlotKind.Object);
				}
			}
		};
		for (IStrategoAppl t : ts) {
			visitor.visit(t);
		}
		return fd;
	}

}
