package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class SlotBind extends MatchPattern {

	protected final FrameSlot slot;

	public SlotBind(FrameSlot slot, SourceSection source) {
		super(source);
		assert slot != null;
		this.slot = slot;
	}

	public static final class VarBind extends SlotBind {

		public VarBind(FrameSlot slot, SourceSection source) {
			super(slot, source);
		}

		@Override
		public void executeMatch(VirtualFrame frame, Object t) {
			frame.setObject(slot, t);
			// InterpreterUtils.writeSlot(getContext(), frame, slot, t, this);
		}

	}

	public static final class ConstBind extends SlotBind {

		public ConstBind(FrameSlot slot, SourceSection source) {
			super(slot, source);
		}

		@Override
		public void executeMatch(VirtualFrame frame, Object t) {
			frame.setObject(slot, t);
			// InterpreterUtils.writeSlot(getContext(), frame, slot, t, this);
		}

	}

}
