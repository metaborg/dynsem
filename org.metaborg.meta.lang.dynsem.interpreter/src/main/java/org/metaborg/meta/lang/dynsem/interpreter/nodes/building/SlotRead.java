package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class SlotRead extends TermBuild {
	protected final FrameSlot slot;

	public SlotRead(FrameSlot slot, SourceSection source) {
		super(source);
		assert slot != null;
		this.slot = slot;
	}

	public static SlotRead create(IStrategoAppl t, FrameDescriptor fd) {
		if (Tools.hasConstructor(t, "ConstRef", 1)) {
			return new ConstRead(fd.findFrameSlot(Tools.stringAt(t, 0).stringValue()),
					SourceUtils.dynsemSourceSectionFromATerm(t));
		} else if (Tools.hasConstructor(t, "VarRef", 1)) {
			return new VarRead(fd.findFrameSlot(Tools.stringAt(t, 0).stringValue()),
					SourceUtils.dynsemSourceSectionFromATerm(t));
		}
		throw new IllegalArgumentException("Unsupported slot read term " + t);
	}

	public static final class VarRead extends SlotRead {

		public VarRead(FrameSlot slot, SourceSection source) {
			super(slot, source);
		}

		@Override
		public Object executeGeneric(VirtualFrame frame) {
			return InterpreterUtils.readSlot(getContext(), frame, slot, this);
		}

		@Override
		public Object executeEvaluated(VirtualFrame frame, Object... terms) {
			return InterpreterUtils.readSlot(getContext(), frame, slot, this);
		}

		@Override
		@TruffleBoundary
		public String toString() {
			return "VarRead(" + slot.getIdentifier() + ")";
		}

	}

	public static final class ConstRead extends SlotRead {

		public ConstRead(FrameSlot slot, SourceSection source) {
			super(slot, source);
		}

		@Override
		public Object executeGeneric(VirtualFrame frame) {
			return InterpreterUtils.readSlot(getContext(), frame, slot, this);
		}

		@Override
		public Object executeEvaluated(VirtualFrame frame, Object... terms) {
			return InterpreterUtils.readSlot(getContext(), frame, slot, this);
		}

		@Override
		@TruffleBoundary
		public String toString() {
			return "ConstRead(" + slot.getIdentifier() + ")";
		}

	}

}
