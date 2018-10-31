package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
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

	public static SlotBind create(IStrategoAppl t, FrameDescriptor fd) {
		if (Tools.hasConstructor(t, "VarRef", 1)) {
			return new VarBind(fd.findFrameSlot(Tools.stringAt(t, 0).stringValue()),
					SourceUtils.dynsemSourceSectionFromATerm(t));
		} else if (Tools.hasConstructor(t, "ConstRef", 1)) {
			return new ConstBind(fd.findFrameSlot(Tools.stringAt(t, 0).stringValue()),
					SourceUtils.dynsemSourceSectionFromATerm(t));
		}
		throw new IllegalArgumentException("Unsupported slot bind term " + t);
	}

	public static final class VarBind extends SlotBind {

		public VarBind(FrameSlot slot, SourceSection source) {
			super(slot, source);
		}

		@Override
		public void executeMatch(VirtualFrame frame, Object t) {
			InterpreterUtils.writeSlot(getContext(), frame, slot, t, this);
		}

	}

	public static final class ConstBind extends SlotBind {

		public ConstBind(FrameSlot slot, SourceSection source) {
			super(slot, source);
		}

		@Override
		public void executeMatch(VirtualFrame frame, Object t) {
			InterpreterUtils.writeSlot(getContext(), frame, slot, t, this);
		}

	}

}
