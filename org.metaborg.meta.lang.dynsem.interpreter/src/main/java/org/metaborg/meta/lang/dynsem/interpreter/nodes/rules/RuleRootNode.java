package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import java.util.HashSet;
import java.util.Set;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public final class RuleRootNode extends DynSemRootNode {
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
	public RuleResult execute(VirtualFrame frame) {
		return rule.execute(frame);
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
		return new RuleRootNode(lang, getSourceSection(), fd, arrowName, dispatchClass,
				RuleNode.create(lang, sourceTerm, fd), sourceTerm);
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return dispatchClass.getSimpleName() + " -" + getArrowName() + "->";
	}

	@TruffleBoundary
	public static RuleRootNode create(DynSemLanguage lang, IStrategoAppl ruleT) {
		CompilerAsserts.neverPartOfCompilation();
		return createWithFrameDescriptor(lang, ruleT, createFrameDescriptor(ruleT));
	}

	@TruffleBoundary
	public static RuleRootNode createWithFrameDescriptor(DynSemLanguage lang, IStrategoAppl ruleT, FrameDescriptor fd) {
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
			return new RuleRootNode(lang, source, fd, arrowName, dispatchClass, RuleNode.create(lang, ruleT, fd),
					ruleT);
		}

		throw new IllegalArgumentException("Unsupported rule term: " + ruleT);
	}

	@TruffleBoundary
	protected static FrameDescriptor createFrameDescriptor(IStrategoTerm t) {
		final FrameDescriptor fd = new FrameDescriptor();
		final Set<String> vars = new HashSet<>();
		TermVisitor visitor = new TermVisitor() {

			@Override
			public void preVisit(IStrategoTerm t) {
				if (Tools.isTermAppl(t) && (Tools.hasConstructor((IStrategoAppl) t, "VarRef", 1)
						|| Tools.hasConstructor((IStrategoAppl) t, "ConstRef", 1))) {
					String v = Tools.stringAt(t, 0).stringValue();
					if (!vars.contains(v)) {
						fd.addFrameSlot(v, false, FrameSlotKind.Object);
						vars.add(v);
					}
				}
			}
		};

		visitor.visit(t);
		return fd;
	}

}
