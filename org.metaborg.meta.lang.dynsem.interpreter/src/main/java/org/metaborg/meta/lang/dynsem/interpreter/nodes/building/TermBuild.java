package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypes;
import metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import metaborg.meta.lang.dynsem.interpreter.terms.IConTerm;
import metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ListBuild.ConsListBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.ListBuild.NilListBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.LiteralTermBuild.FalseLiteralTermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.LiteralTermBuild.IntLiteralTermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.LiteralTermBuild.TrueLiteralTermBuild;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.terms.util.NotImplementedException;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_lang.IPersistentCollection;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;

@TypeSystemReference(BuiltinTypes.class)
@NodeInfo(description = "The abstract base node for all term construction")
public abstract class TermBuild extends Node {

	private Node createContext;

	public TermBuild(SourceSection source) {
		super(source);
		this.createContext = DynSemContext.LANGUAGE.createFindContextNode0();
	}

	protected DynSemContext getContext() {
		return DynSemContext.LANGUAGE.findContext0(createContext);
	}

	public abstract Object executeGeneric(VirtualFrame frame);

	public String executeString(VirtualFrame frame)
			throws UnexpectedResultException {
		return BuiltinTypesGen.expectString(executeGeneric(frame));
	}

	public int executeInteger(VirtualFrame frame)
			throws UnexpectedResultException {
		return BuiltinTypesGen.expectInteger(executeGeneric(frame));
	}

	public ITerm executeITerm(VirtualFrame frame)
			throws UnexpectedResultException {
		return BuiltinTypesGen.expectITerm(executeGeneric(frame));
	}

	public IConTerm executeIConTerm(VirtualFrame frame)
			throws UnexpectedResultException {
		return BuiltinTypesGen.expectIConTerm(executeGeneric(frame));
	}

	public PersistentMap<?, ?> executeMap(VirtualFrame frame)
			throws UnexpectedResultException {
		return BuiltinTypesGen.expectPersistentMap(executeGeneric(frame));
	}

	public boolean executeBoolean(VirtualFrame frame)
			throws UnexpectedResultException {
		return BuiltinTypesGen.expectBoolean(executeGeneric(frame));
	}

	public IPersistentCollection<?> executeList(VirtualFrame frame)
			throws UnexpectedResultException {
		return BuiltinTypesGen.expectIPersistentStack(executeGeneric(frame));
	}

	public static TermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		if (Tools.hasConstructor(t, "Con", 2)) {
			return ConBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "NativeOp", 2)) {
			return NativeOpTermBuild.create(t, fd);
		}
		if (Tools.hasConstructor((IStrategoAppl) t, "VarRef", 1)) {
			return VarRead.create(t, fd);
		}
		if (Tools.hasConstructor(t, "ArgRead", 1)) {
			return ArgRead.create(t);
		}
		if (Tools.hasConstructor(t, "Map", 1)) {
			return MapBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "MapExtend", 2)) {
			return MapExtendBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "MapSelect", 2)) {
			return MapSelectBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "MapHas", 2)) {
			return MapHas.create(t, fd);
		}
		if (Tools.hasConstructor(t, "True", 0)) {
			return TrueLiteralTermBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "False", 0)) {
			return FalseLiteralTermBuild.create(t, fd);
		}
		if(Tools.hasConstructor(t, "Int", 1)){
			return IntLiteralTermBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "List", 1)) {
			return NilListBuild.create(t, fd);
		}
		if (Tools.hasConstructor(t, "ListTail", 2)) {
			return ConsListBuild.create(t, fd);
		}

		throw new NotImplementedException("Unsupported term build: " + t);
	}

	public static TermBuild createFromLabelComp(IStrategoAppl t,
			FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "LabelComp", 2);
		return create(Tools.applAt(t, 1), fd);
	}

}