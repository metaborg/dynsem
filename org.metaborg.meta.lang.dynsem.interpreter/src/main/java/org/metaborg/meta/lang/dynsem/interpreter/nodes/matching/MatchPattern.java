package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.SourceSection;

public abstract class MatchPattern extends Node {

	private Node createContext;

	public MatchPattern(SourceSection source) {
		super(source);
		this.createContext = DynSemLanguage.INSTANCE.createFindContextNode0();
	}

	protected DynSemContext getContext() {
		return DynSemLanguage.INSTANCE.findContext0(createContext);
	}

	public abstract boolean execute(Object term, VirtualFrame frame);

	public static MatchPattern create(IStrategoAppl t, FrameDescriptor fd) {
		if (Tools.hasConstructor(t, "Con", 2)) {
			return ConMatch.create(t, fd);
		}
		if (Tools.hasConstructor(t, "VarRef", 1)) {
			return VarBind.create(t, fd);
		}

		throw new NotImplementedException("Unsupported match pattern: " + t);
	}

	public static MatchPattern createFromLabelComp(IStrategoAppl t,
			FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "LabelComp", 2);
		return create(Tools.applAt(t, 1), fd);
	}
}
