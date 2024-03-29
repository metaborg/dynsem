package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class VarRead extends TermBuild {

	private final FrameSlot slot;

	public VarRead(FrameSlot slot, SourceSection source) {
		super(source);
		assert slot != null;
		this.slot = slot;
	}

	// FIXME: we should provide type information here for the return type
	@Specialization
	public Object executeRead(VirtualFrame frame) {
		return InterpreterUtils.readSlot(getContext(), frame, slot, this);
	}

	public static TermBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "VarRef", 1);
		return VarReadNodeGen.create(fd.findFrameSlot(Tools.stringAt(t, 0).stringValue()),
				SourceUtils.dynsemSourceSectionFromATerm(t));
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return "VarRead(" + slot.getIdentifier() + ")";
	}

}
