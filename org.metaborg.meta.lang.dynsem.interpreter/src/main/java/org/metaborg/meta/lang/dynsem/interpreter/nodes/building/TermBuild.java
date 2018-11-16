package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypes;
import org.metaborg.meta.lang.dynsem.interpreter.terms.BuiltinTypesGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITupleTerm;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.dsl.TypeSystemReference;
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

	public boolean isConstantNode() {
		return false;
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

}