package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.HashSet;
import java.util.Set;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops.WhileNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
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
	private final IStrategoAppl sourceTerm;
	private final String arrowName;
	private final Class<?> dispatchClass;

	@Child private RuleNode rule;

	private DynSemLanguage lang;

	protected RuleRootNode(DynSemLanguage lang, SourceSection sourceSection, FrameDescriptor frameDescriptor,
			String arrowName, Class<?> dispatchClass, RuleNode rule, IStrategoAppl ruleT) {
		super(lang, sourceSection, frameDescriptor, Truffle.getRuntime()
				.createAssumption("constant input for " + dispatchClass.getSimpleName() + " -" + arrowName + "->"));
		this.lang = lang;
		this.arrowName = arrowName;
		this.dispatchClass = dispatchClass;
		this.rule = rule;
		this.sourceTerm = ruleT;
	}

	@Override
	public abstract RuleResult execute(VirtualFrame frame);

	@Specialization(guards = "guardCheck(frame, inputTerm)", assumptions = "getConstantTermAssumption()", limit = "1")
	public RuleResult doHasSeenStatic(VirtualFrame frame, @Cached("getInputTerm(frame)") Object inputTerm) {
		return rule.execute(frame);
	}

	@Specialization(replaces = "doHasSeenStatic")
	public RuleResult doHasSeenDynamic(VirtualFrame frame) {
		return rule.execute(frame);
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

	public RuleNode getRuleNode() {
		return rule;
	}

	public IStrategoAppl getRuleSourceTerm() {
		return sourceTerm;
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
		// return createWithFrameDescriptor(lang, sourceTerm, getFrameDescriptor());
		FrameDescriptor fd = getFrameDescriptor();
		return RuleRootNodeGen.create(lang, getSourceSection(), fd, arrowName, dispatchClass,
				RuleNode.create(lang, sourceTerm, fd, getContext().getTermRegistry()), sourceTerm);
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return dispatchClass.getSimpleName() + " -" + getArrowName() + "->";
	}

	@TruffleBoundary
	public static RuleRootNode create(DynSemLanguage lang, IStrategoAppl ruleT, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		return createWithFrameDescriptor(lang, ruleT, createFrameDescriptor(ruleT), termReg);
	}

	@TruffleBoundary
	public static RuleRootNode createWithFrameDescriptor(DynSemLanguage lang, IStrategoAppl ruleT, FrameDescriptor fd,
			ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();

		IStrategoAppl relationT = Tools.applAt(ruleT, 2);
		assert Tools.hasConstructor(relationT, "Relation", 3);

		IStrategoAppl arrowTerm = Tools.applAt(relationT, 1);
		String arrowName = Tools.javaStringAt(arrowTerm, 1);

		String dispatchClassName = Tools.javaStringAt(ruleT, 4);
		Class<?> dispatchClass;

		try {
			dispatchClass = RuleRootNode.class.getClassLoader().loadClass(dispatchClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dispatchClassName);
		}

		if (Tools.hasConstructor(ruleT, "Rule", 5)) {
			SourceSection source = SourceUtils.dynsemSourceSectionFromATerm(ruleT);
			return RuleRootNodeGen.create(lang, source, fd, arrowName, dispatchClass,
					RuleNode.create(lang, ruleT, fd, termReg), ruleT);
		}

		throw new IllegalArgumentException("Unsupported rule term: " + ruleT);
	}

	@TruffleBoundary
	protected static FrameDescriptor createFrameDescriptor(IStrategoTerm t) {
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

		visitor.visit(t);
		return fd;
	}

}
