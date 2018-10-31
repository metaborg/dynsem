package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypes;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITupleTerm;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.terms.util.NotImplementedException;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

@TypeSystemReference(BuiltinTypes.class)
@NodeInfo(description = "The abstract base node for all term construction")
public abstract class TermBuild extends DynSemNode {

	public TermBuild(SourceSection source) {
		super(source);
	}

	public abstract Object executeGeneric(VirtualFrame frame);

	public abstract Object executeEvaluated(VirtualFrame frame, Object... terms);

	public String executeString(VirtualFrame frame) {
		return BuiltinTypesGen.asString(executeGeneric(frame));
	}

	public int executeInteger(VirtualFrame frame) {
		return BuiltinTypesGen.asInteger(executeGeneric(frame));
	}

	public ITerm executeITerm(VirtualFrame frame) {
		return BuiltinTypesGen.asITerm(executeGeneric(frame));
	}

	public IApplTerm executeIApplTerm(VirtualFrame frame) {
		return BuiltinTypesGen.asIApplTerm(executeGeneric(frame));
	}

	public PersistentMap<?, ?> executeMap(VirtualFrame frame) {
		return BuiltinTypesGen.asPersistentMap(executeGeneric(frame));
	}

	public boolean executeBoolean(VirtualFrame frame) {
		return BuiltinTypesGen.asBoolean(executeGeneric(frame));
	}

	public Object[] executeObjectArray(VirtualFrame frame) {
		return BuiltinTypesGen.asObjectArray(executeGeneric(frame));
	}

	public IListTerm<?> executeIList(VirtualFrame frame) {
		return BuiltinTypesGen.asIListTerm(executeGeneric(frame));
	}

	public ITupleTerm executeITuple(VirtualFrame frame) {
		return BuiltinTypesGen.asITupleTerm(executeGeneric(frame));
	}

	public static TermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		if (Tools.hasConstructor(t, "Con", 2)) {
			return ConBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "NativeOp", 2)) {
			return NativeOpTermBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "VarRef", 1) || Tools.hasConstructor(t, "ConstRef", 1)) {
			return SlotRead.create(t, fd);
		}
		// if (Tools.hasConstructor(t, "ArgRead", 1)) {
		// return ArgRead.create(t);
		// }
		if (Tools.hasConstructor(t, "Map_", 1)) {
			return MapBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "MapExtend", 2)) {
			return MapExtendBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "MapUnbind", 2)) {
			return MapUnbindBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "DeAssoc", 2)) {
			return DeAssoc.create(t, fd);
		}
		if (Tools.hasConstructor(t, "MapHas", 2)) {
			return MapHas.create(t, fd);
		}
		if (Tools.hasConstructor(t, "TypedMapKeys", 2)) {
			return TypedMapKeys.create(t, fd);
		}
		if (Tools.hasConstructor(t, "TypedMapValues", 2)) {
			return TypedMapValues.create(t, fd);
		}
		if (Tools.hasConstructor(t, "True", 0)) {
			return TrueLiteralTermBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "False", 0)) {
			return FalseLiteralTermBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "Int", 1)) {
			return IntLiteralTermBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "String", 1)) {
			return StringLiteralTermBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "StrConcat", 2)) {
			return StringConcatTermBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "ListSource", 2)) {
			return create(Tools.applAt(t, 0), fd);
		}
		if (Tools.hasConstructor(t, "TypedList", 2) || Tools.hasConstructor(t, "TypedListTail", 3)) {
			return ListBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "ListConcat", 2)) {
			return ListConcatTermBuildNodeGen.create(t, fd);
		}

		if (Tools.hasConstructor(t, "Reverse", 1)) {
			return ListReverseTermBuildNodeGen.create(t, fd);
		}

		if (Tools.hasConstructor(t, "ListLength", 1)) {
			return ListLengthTermBuild.create(t, fd);
		}

		if (Tools.hasConstructor(t, "TypedTuple", 2)) {
			return TupleBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "NativeFunCall", 4)) {
			return SortFunCallBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "Fresh", 0)) {
			return Fresh.create(t, fd);
		}
		if (Tools.hasConstructor(t, "Cast", 2)) {
			// FIXME: this is a hack. we should use the type information from
			// the cast
			return TermBuild.create(Tools.applAt(t, 0), fd);
		}
		if (Tools.hasConstructor(t, "TermPlaceholder", 0)) {
			return TermPlaceholderBuild.create(t, fd);
		}


		throw new NotImplementedException("Unsupported term build: " + t);
	}

	public static TermBuild[] cloneNodes(TermBuild[] nodes) {
		final TermBuild[] clone = new TermBuild[nodes.length];
		for (int i = 0; i < clone.length; i++) {
			clone[i] = cloneNode(nodes[i]);
		}
		return clone;
	}

	public static TermBuild cloneNode(TermBuild node) {
		return null == node ? null : NodeUtil.cloneNode(node);
	}

	public static TermBuild createFromLabelComp(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "LabelComp", 2);
		return create(Tools.applAt(t, 1), fd);
	}

}