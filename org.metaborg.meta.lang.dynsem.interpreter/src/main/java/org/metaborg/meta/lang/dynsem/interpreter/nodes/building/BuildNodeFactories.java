package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.MapBuild.BindMapBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.MapBuild.EmptyMapBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.SlotRead.VarRead;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.SlotReadFactory.ConstReadNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;

public final class BuildNodeFactories {
	private BuildNodeFactories() {
	}

	public static TermBuild create(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		if (Tools.hasConstructor(t, "Con", 2)) {
			return createConBuild(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "NativeOp", 2)) {
			return createNativeOp(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "VarRef", 1) || Tools.hasConstructor(t, "ConstRef", 1)) {
			return createSlotRead(t, fd, termReg);
		}
		// if (Tools.hasConstructor(t, "ArgRead", 1)) {
		// return ArgRead.create(t);
		// }
		if (Tools.hasConstructor(t, "Map_", 1)) {
			return createMapBuild(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "MapExtend", 2)) {
			return createMapExtend(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "MapUnbind", 2)) {
			return createMapUnbind(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "DeAssoc", 2)) {
			return createMapDeAssoc(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "MapHas", 2)) {
			return createMapHas(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "TypedMapKeys", 2)) {
			return createMapKeys(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "TypedMapValues", 2)) {
			return createMapValues(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "True", 0)) {
			return createBoolTrue(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "False", 0)) {
			return createBoolFalse(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "Int", 1)) {
			return createIntLiteral(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "String", 1)) {
			return createStringLiteral(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "StrConcat", 2)) {
			return createStringConcat(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "ListSource", 2)) {
			return create(Tools.applAt(t, 0), fd, termReg);
		}
		if (Tools.hasConstructor(t, "TypedList", 2) || Tools.hasConstructor(t, "TypedListTail", 3)) {
			return createList(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "ListConcat", 2)) {
			return createListConcat(t, fd, termReg);
		}

		if (Tools.hasConstructor(t, "Reverse", 1)) {
			return createListReverse(t, fd, termReg);
		}

		if (Tools.hasConstructor(t, "ListLength", 1)) {
			return createListLength(t, fd, termReg);
		}

		if (Tools.hasConstructor(t, "TypedTuple", 2)) {
			return createTupleBuild(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "NativeFunCall", 4)) {
			return createSortFunCall(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "Fresh", 0)) {
			return createFresh(t, fd, termReg);
		}
		if (Tools.hasConstructor(t, "Cast", 2)) {
			// FIXME: this is a hack. we should use the type information from
			// the cast
			return create(Tools.applAt(t, 0), fd, termReg);
		}
		if (Tools.hasConstructor(t, "TermPlaceholder", 0)) {
			return createPlaceholder(t, fd, termReg);
		}

		throw new NotImplementedException("Unsupported term build: " + t);
	}

	public static TermBuild createFromLabelComp(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "LabelComp", 2);
		return create(Tools.applAt(t, 1), fd, termReg);
	}

	public static TermBuild createConBuild(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Con", 2);
		String constr = Tools.stringAt(t, 0).stringValue();

		IStrategoList childrenT = Tools.listAt(t, 1);
		TermBuild[] children = new TermBuild[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = create(Tools.applAt(childrenT, i), fd, termReg);
		}
		return termReg.lookupBuildFactory(termReg.getConstructorClass(constr, children.length))
				.apply(SourceUtils.dynsemSourceSectionFromATerm(t), children);
	}

	public static TermBuild createMapDeAssoc(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "DeAssoc", 2);
		TermBuild left = create(Tools.applAt(t, 0), fd, termReg);
		TermBuild right = create(Tools.applAt(t, 1), fd, termReg);

		return DeAssocNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), left, right);
	}

	public static TermBuild createBoolFalse(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "False", 0);
		return FalseLiteralTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	public static TermBuild createBoolTrue(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "True", 0);
		return TrueLiteralTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	public static TermBuild createFresh(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		return FreshNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	public static TermBuild createIntLiteral(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		return IntLiteralTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t),
				Integer.parseInt(Tools.javaStringAt(t, 0)));
	}

	public static StringLiteralTermBuild createStringLiteral(IStrategoAppl t, FrameDescriptor fd,
			ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		return StringLiteralTermBuildNodeGen.create(Tools.javaStringAt(t, 0),
				SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	public static TermBuild createList(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "TypedList", 2) || Tools.hasConstructor(t, "TypedListTail", 3);

		IStrategoList elemTs = Tools.listAt(t, 0);
		final TermBuild[] elemNodes = new TermBuild[elemTs.size()];
		for (int i = 0; i < elemNodes.length; i++) {
			elemNodes[i] = create(Tools.applAt(elemTs, i), fd, termReg);
		}

		TermBuild tailNodes = null;
		if (Tools.hasConstructor(t, "TypedListTail", 3)) {
			tailNodes = create(Tools.applAt(t, 1), fd, termReg);
		}

		final String dispatchClassName = Tools.javaStringAt(t, Tools.hasConstructor(t, "TypedList", 2) ? 1 : 2);
		Class<?> dispatchClass;

		try {
			dispatchClass = RuleNode.class.getClassLoader().loadClass(dispatchClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dispatchClassName);
		}

		// return ListBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), elemNodes, tailNodes,
		// dispatchClass);
		return termReg.lookupBuildFactory(dispatchClass).apply(SourceUtils.dynsemSourceSectionFromATerm(t), elemNodes,
				tailNodes);
	}

	public static TermBuild createListConcat(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "ListConcat", 2);

		TermBuild left = create(Tools.applAt(t, 0), fd, termReg);
		TermBuild right = create(Tools.applAt(t, 1), fd, termReg);

		return ListConcatTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), left, right);
	}

	public static TermBuild createListLength(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "ListLength", 1);

		TermBuild list = create(Tools.applAt(t, 0), fd, termReg);

		return ListLengthTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), list);
	}

	public static TermBuild createListReverse(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "Reverse", 1);

		TermBuild list = create(Tools.applAt(t, 0), fd, termReg);

		return ListReverseTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), list);
	}

	public static MapBuild createMapBuild(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Map_", 1);
		IStrategoList bindsT = Tools.listAt(t, 0);
		if (bindsT.size() == 0) {
			return createMapEmpty(t, termReg);
		}
		if (bindsT.size() == 1) {
			return createMapBind(t, fd, termReg);
		}

		throw new RuntimeException("Unsupported map build term: " + t);
	}

	public static EmptyMapBuild createMapEmpty(IStrategoAppl t, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "Map_", 1);
		assert Tools.isTermList(t.getSubterm(0)) && Tools.listAt(t, 0).size() == 0;
		return MapBuildFactory.EmptyMapBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	public static BindMapBuild createMapBind(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "Map_", 1);
		assert Tools.isTermList(t.getSubterm(0)) && Tools.listAt(t, 0).size() == 1;
		IStrategoAppl bind = Tools.applAt(Tools.listAt(t, 0), 0);

		assert Tools.hasConstructor(bind, "Bind", 2);
		TermBuild keyNode = create(Tools.applAt(bind, 0), fd, termReg);
		TermBuild valNode = create(Tools.applAt(bind, 1), fd, termReg);

		return MapBuildFactory.BindMapBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), keyNode,
				valNode);
	}

	public static MapExtendBuild createMapExtend(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "MapExtend", 2);
		TermBuild lmap = create(Tools.applAt(t, 0), fd, termReg);
		TermBuild rmap = create(Tools.applAt(t, 1), fd, termReg);

		return MapExtendBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), lmap, rmap);
	}

	public static MapHas createMapHas(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "MapHas", 2);
		TermBuild mapNode = create(Tools.applAt(t, 0), fd, termReg);
		TermBuild keyNode = create(Tools.applAt(t, 1), fd, termReg);
		return MapHasNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), mapNode, keyNode);
	}

	public static MapUnbindBuild createMapUnbind(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "MapUnbind", 2);
		TermBuild map = create(Tools.applAt(t, 0), fd, termReg);
		TermBuild key = create(Tools.applAt(t, 1), fd, termReg);

		return MapUnbindBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), map, key);
	}

	public static TermBuild createNativeOp(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "NativeOp", 2);
		String constr = Tools.stringAt(t, 0).stringValue();

		IStrategoList childrenT = Tools.listAt(t, 1);
		TermBuild[] children = new TermBuild[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = create(Tools.applAt(childrenT, i), fd, termReg);
		}

		return termReg.lookupNativeOpBuildFactory(termReg.getNativeOperatorClass(constr, children.length))
				.apply(SourceUtils.dynsemSourceSectionFromATerm(t), children);

	}

	public static SlotRead createSlotRead(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		if (Tools.hasConstructor(t, "ConstRef", 1)) {
			return ConstReadNodeGen.create(fd.findFrameSlot(Tools.stringAt(t, 0).stringValue().intern().hashCode()),
					SourceUtils.dynsemSourceSectionFromATerm(t));
		} else if (Tools.hasConstructor(t, "VarRef", 1)) {
			return new VarRead(fd.findFrameSlot(Tools.stringAt(t, 0).stringValue().intern().hashCode()),
					SourceUtils.dynsemSourceSectionFromATerm(t));
		}
		throw new IllegalArgumentException("Unsupported slot read term " + t);
	}

	public static SortFunCallBuild createSortFunCall(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "NativeFunCall", 4);
		String sort = Tools.stringAt(t, 0).stringValue();
		String function = Tools.stringAt(t, 1).stringValue();
		TermBuild receiver = create(Tools.applAt(t, 2), fd, termReg);
		IStrategoList argsT = Tools.listAt(t, 3);
		TermBuild[] children = new TermBuild[argsT.size() + 1];

		children[0] = receiver;
		for (int i = 0; i < argsT.size(); i++) {
			children[i + 1] = create(Tools.applAt(argsT, i), fd, termReg);
		}

		return SortFunCallBuildNodeGen.create(sort, function, children, SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	public static StringConcatTermBuild createStringConcat(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		assert Tools.hasConstructor(t, "StrConcat", 2);

		TermBuild left = create(Tools.applAt(t, 0), fd, termReg);
		TermBuild right = create(Tools.applAt(t, 1), fd, termReg);

		return StringConcatTermBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), left, right);
	}

	public static TermBuild createTupleBuild(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedTuple", 2);

		IStrategoList childrenT = Tools.listAt(t, 0);
		TermBuild[] children = new TermBuild[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = create(Tools.applAt(childrenT, i), fd, termReg);
		}

		String dispatchClassName = Tools.stringAt(t, 1).stringValue();
		Class<?> dispatchClass;

		try {
			dispatchClass = RuleNode.class.getClassLoader().loadClass(dispatchClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not load dispatch class " + dispatchClassName);
		}
		return termReg.lookupBuildFactory(dispatchClass).apply(SourceUtils.dynsemSourceSectionFromATerm(t), children);
	}

	public static TypedMapKeys createMapKeys(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedMapKeys", 2);
		TermBuild mapNode = create(Tools.applAt(t, 0), fd, termReg);
		String keyClass = Tools.javaStringAt(t, 1);
		return TypedMapKeysNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), keyClass, mapNode);
	}

	public static TypedMapValues createMapValues(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TypedMapValues", 2);
		TermBuild mapNode = create(Tools.applAt(t, 0), fd, termReg);
		String valueListClass = Tools.javaStringAt(t, 1);
		return TypedMapValuesNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), valueListClass, mapNode);
	}

	public static TermPlaceholderBuild createPlaceholder(IStrategoAppl t, FrameDescriptor fd, ITermRegistry termReg) {
		return TermPlaceholderBuildNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t));
	}
}
