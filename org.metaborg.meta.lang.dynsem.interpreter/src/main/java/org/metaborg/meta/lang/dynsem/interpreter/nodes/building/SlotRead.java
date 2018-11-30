package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
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

	public static final class VarRead extends SlotRead {

		public VarRead(FrameSlot slot, SourceSection source) {
			super(slot, source);
		}

		@Override
		public Object executeGeneric(VirtualFrame frame) {
			return frame.getValue(slot);
		}

		@Override
		public Object executeEvaluated(VirtualFrame frame, Object... terms) {
			return frame.getValue(slot);
		}

		@Override
		@TruffleBoundary
		public String toString() {
			return "VarRead(" + slot.getIdentifier() + ")";
		}

	}

	public static abstract class ConstRead extends SlotRead {

		public ConstRead(FrameSlot slot, SourceSection source) {
			super(slot, source);
		}

		@Override
		public Assumption getConstantBuildAssumption() {
			return getConstantInputAssumption();
		}

		@Specialization(assumptions = "constantInputAssumption")
		public Object doConstantTerm(VirtualFrame frame, @Cached("doDynamic(frame)") Object cached_term,
				@Cached("getConstantBuildAssumption()") Assumption constantInputAssumption) {
			return cached_term;
		}

		@Specialization(replaces = "doConstantTerm")
		public Object doDynamic(VirtualFrame frame) {
			return frame.getValue(slot);
		}

		@Override
		@TruffleBoundary
		public String toString() {
			return "ConstRead(" + slot.getIdentifier() + ")";
		}

	}

}
