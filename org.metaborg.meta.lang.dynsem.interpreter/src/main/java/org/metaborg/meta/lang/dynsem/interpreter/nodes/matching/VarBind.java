package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class VarBind extends MatchPattern {

	private final FrameSlot slot;

	public VarBind(FrameSlot slot, SourceSection source) {
		super(source);
		assert slot != null;
		this.slot = slot;
	}

	@Override
	public void executeMatch(VirtualFrame frame, Object t) {
		InterpreterUtils.writeSlot(frame, slot, t);
	}

	public static VarBind create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "VarRef", 1);
		return new VarBind(fd.findFrameSlot(Tools.stringAt(t, 0).stringValue()), SourceSectionUtil.fromStrategoTerm(t));
	}

}
