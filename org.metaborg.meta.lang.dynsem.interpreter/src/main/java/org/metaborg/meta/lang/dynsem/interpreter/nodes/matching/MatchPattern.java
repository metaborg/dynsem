package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

public abstract class MatchPattern extends DynSemNode {

	public MatchPattern(SourceSection source) {
		super(source);
	}

	public abstract void executeMatch(VirtualFrame frame, Object term);

	public static MatchPattern create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		if (Tools.hasConstructor(t, "ArgBind", 1)) {
			return WldMatchPattern.create(t);
		}
		if (Tools.hasConstructor(t, "Wld", 0)) {
			return WldMatchPattern.create(t);
		}
		if (Tools.hasConstructor(t, "Con", 2)) {
			return ConMatch.create(t, fd);
		}
		if (Tools.hasConstructor(t, "VarRef", 1)) {
			return VarBind.create(t, fd);
		}
		if (Tools.hasConstructor(t, "TypedList", 2) || Tools.hasConstructor(t, "TypedListTail", 3)) {
			return ListMatch.create(t, fd);
		}

		if (Tools.hasConstructor(t, "List", 1) || Tools.hasConstructor(t, "ListTail", 2)) {
			return GenericListMatch.create(t, fd);
		}

		if (Tools.hasConstructor(t, "LabelComp", 2)) {
			// TODO we should use the type information from the labelcomp instead of skipping over it
			return MatchPattern.create(Tools.applAt(t, 1), fd);
		}
		if (Tools.hasConstructor(t, "Cast", 2)) {
			// TODO we should use the type information from the cast instead of skipping over it
			return MatchPattern.create(Tools.applAt(t, 0), fd);
		}
		if (Tools.hasConstructor(t, "As", 2)) {
			return AsMatch.create(t, fd);
		}

		return LiteralMatchPattern.create(t, fd);

		// throw new NotImplementedException("Unsupported match pattern: " + t);
	}

	public static MatchPattern[] cloneNodes(MatchPattern[] nodes) {
		final MatchPattern[] clone = new MatchPattern[nodes.length];
		for (int i = 0; i < clone.length; i++) {
			clone[i] = cloneNode(nodes[i]);
		}
		return clone;
	}

	public static MatchPattern cloneNode(MatchPattern node) {
		return null == node ? null : NodeUtil.cloneNode(node);
	}

	public static MatchPattern createFromLabelComp(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "LabelComp", 2);
		return create(Tools.applAt(t, 1), fd);
	}
}
