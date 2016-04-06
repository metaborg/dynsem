package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ListMatch.ConsListMatch;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ListMatch.NilListMatch;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class MatchPattern extends DynSemNode {

	public MatchPattern(SourceSection source) {
		super(source);
	}

	public abstract boolean execute(Object term, VirtualFrame frame);

	public static MatchPattern create(IStrategoAppl t, FrameDescriptor fd) {
		if (Tools.hasConstructor(t, "Con", 2)) {
			return ConMatch.create(t, fd);
		}
		if (Tools.hasConstructor(t, "VarRef", 1)) {
			return VarBind.create(t, fd);
		}
		if (Tools.hasConstructor(t, "List", 1)) {
			return NilListMatch.create(t, fd);
		}
		if (Tools.hasConstructor(t, "ListTail", 2)) {
			return ConsListMatch.create(t, fd);
		}
		if (Tools.hasConstructor(t, "Cast", 2)) {
			// FIXME: this is a hack. we should use the type information from
			// the cast
			return MatchPattern.create(Tools.applAt(t, 0), fd);
		}
		throw new NotImplementedException("Unsupported match pattern: " + t);
	}

	public static MatchPattern createFromLabelComp(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "LabelComp", 2);
		return create(Tools.applAt(t, 1), fd);
	}
}
