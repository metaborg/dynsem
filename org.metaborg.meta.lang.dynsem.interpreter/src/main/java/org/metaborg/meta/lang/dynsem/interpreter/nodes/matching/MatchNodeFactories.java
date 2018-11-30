package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.SlotBind.ConstBind;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.SlotBind.VarBind;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.lists.GenericListMatch;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.lists.GenericListMatchNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.source.SourceSection;

public final class MatchNodeFactories {
	private MatchNodeFactories() {

	}

	public static MatchPattern create(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		if (Tools.hasConstructor(t, "ArgBind", 1)) {
			return createNoOp(t, termReg);
		}
		if (Tools.hasConstructor(t, "Wld", 0)) {
			return createNoOp(t, termReg);
		}
		if (Tools.hasConstructor(t, "Con", 2)) {
			return createCon(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "VarRef", 1) || Tools.hasConstructor(t, "ConstRef", 1)) {
			return createSlotBind(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "TypedList", 2) || Tools.hasConstructor(t, "TypedListTail", 3)) {
			return createListTyped(t, fd, termReg);
		}

		if (Tools.hasConstructor(t, "List_", 1) || Tools.hasConstructor(t, "ListTail", 2)) {
			return createListGeneric(t, fd, termReg);
		}

		if (Tools.hasConstructor(t, "TypedTuple", 2)) {
			return createTuple(t, fd, termReg);
		}

		if (Tools.hasConstructor(t, "LabelComp", 2)) {
			// TODO we should use the type information from the labelcomp instead of skipping over it
			return create(Tools.applAt(t, 1), fd, termReg);
		}
		if (Tools.hasConstructor(t, "Cast", 2)) {
			// TODO we should use the type information from the cast instead of skipping over it
			return create(Tools.applAt(t, 0), fd, termReg);
		}
		if (Tools.hasConstructor(t, "As", 2)) {
			return createAs(t, fd, termReg);
		}

		return createLiteral(t, fd, termReg);

	}

	public static MatchPattern createFromLabelComp(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "LabelComp", 2);
		return create(Tools.applAt(t, 1), fd, termReg);
	}

	public static AsMatch createAs(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "As", 2);
		return AsMatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t),
				createSlotBind(Tools.applAt(t, 0), fd, termReg), create(Tools.applAt(t, 1), fd, termReg));

	}

	public static MatchPattern createCon(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Con", 2);
		String constr = Tools.stringAt(t, 0).stringValue();
		IStrategoList childrenT = Tools.listAt(t, 1);
		MatchPattern[] children = new MatchPattern[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = create(Tools.applAt(childrenT, i), fd, termReg);
		}

		// return new ConMatch(constr, children, SourceUtils.dynsemSourceSectionFromATerm(t));
		return termReg.lookupMatchFactory(termReg.getConstructorClass(constr, children.length))
				.apply(SourceUtils.dynsemSourceSectionFromATerm(t), children);
	}

	public static LiteralMatchPattern createLiteral(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		SourceSection source = SourceUtils.dynsemSourceSectionFromATerm(t);
		if (Tools.hasConstructor(t, "True", 0)) {
			return TrueLiteralTermMatchPatternNodeGen.create(source);
		}
		if (Tools.hasConstructor(t, "False", 0)) {
			return FalseLiteralTermMatchPatternNodeGen.create(source);
		}
		if (Tools.hasConstructor(t, "Int", 1)) {

			return IntLiteralTermMatchPatternNodeGen.create(Integer.parseInt(Tools.stringAt(t, 0).stringValue()),
					source);
		}
		if (Tools.hasConstructor(t, "String", 1)) {

			return StringLiteralTermMatchPatternNodeGen.create(Tools.stringAt(t, 0).stringValue(), source);
		}

		throw new NotImplementedException("Unsupported literal: " + t);
	}

	public static NoOpPattern createNoOp(IStrategoAppl t, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		return new NoOpPattern(SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	public static SlotBind createSlotBind(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		if (Tools.hasConstructor(t, "VarRef", 1)) {
			return new VarBind(fd.findFrameSlot(Tools.stringAt(t, 0).stringValue().intern().hashCode()),
					SourceUtils.dynsemSourceSectionFromATerm(t));
		} else if (Tools.hasConstructor(t, "ConstRef", 1)) {
			return new ConstBind(fd.findFrameSlot(Tools.stringAt(t, 0).stringValue().intern().hashCode()),
					SourceUtils.dynsemSourceSectionFromATerm(t));
		}
		throw new IllegalArgumentException("Unsupported slot bind term " + t);
	}

	public static MatchPattern createTuple(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedTuple", 2);

		IStrategoList childrenT = Tools.listAt(t, 0);
		MatchPattern[] children = new MatchPattern[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = create(Tools.applAt(childrenT, i), fd, termReg);
		}

		final String dispatchClassName = Tools.stringAt(t, 1).stringValue();
		Class<?> dispatchClass;

		try {
			dispatchClass = RuleNode.class.getClassLoader().loadClass(dispatchClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dispatchClassName);
		}

		// return new TupleMatch(SourceUtils.dynsemSourceSectionFromATerm(t), children, dispatchClass);
		return termReg.lookupMatchFactory(dispatchClass).apply(SourceUtils.dynsemSourceSectionFromATerm(t), children);
	}

	public static GenericListMatch createListGeneric(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "List_", 1) || Tools.hasConstructor(t, "ListTail", 2);

		final int numHeadElems = Tools.listAt(t, 0).size();

		MatchPattern tailPattern = null;
		if (Tools.hasConstructor(t, "ListTail", 2)) {
			tailPattern = create(Tools.applAt(t, 1), fd, termReg);
		}

		return GenericListMatchNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), numHeadElems, tailPattern);
	}

	public static MatchPattern createListTyped(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "TypedList", 2) || Tools.hasConstructor(t, "TypedListTail", 3);

		IStrategoList elemTs = Tools.listAt(t, 0);
		final MatchPattern[] elemPatterns = new MatchPattern[elemTs.size()];
		for (int i = 0; i < elemPatterns.length; i++) {
			elemPatterns[i] = create(Tools.applAt(elemTs, i), fd, termReg);
		}

		MatchPattern tailPattern = null;
		if (Tools.hasConstructor(t, "TypedListTail", 3)) {
			tailPattern = create(Tools.applAt(t, 1), fd, termReg);
		}

		final String dispatchClassName = Tools.javaStringAt(t, Tools.hasConstructor(t, "TypedList", 2) ? 1 : 2);
		Class<?> dispatchClass;

		try {
			dispatchClass = RuleNode.class.getClassLoader().loadClass(dispatchClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dispatchClassName);
		}

		// return new ListMatch(SourceUtils.dynsemSourceSectionFromATerm(t), elemPatterns, tailPattern, dispatchClass);
		return termReg.lookupMatchFactory(dispatchClass).apply(SourceUtils.dynsemSourceSectionFromATerm(t),
				elemPatterns, tailPattern);
	}
}
