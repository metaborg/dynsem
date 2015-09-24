package org.metaborg.meta.interpreter.framework;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;

public final class FrameUtils {
	
	@ExplodeLoop
	public static Object[] backupSlots(VirtualFrame frame, FrameSlot[] slots) {
		CompilerAsserts.compilationConstant(slots.length);
		Object[] backup = new Object[slots.length];
		for (int i = 0; i < slots.length; i++) {
			backup[i] = frame.getValue(slots[i]);
		}
		return backup;
	}

	@ExplodeLoop
	public static void restoreSlots(VirtualFrame frame, FrameSlot[] slots,
			Object[] backup) {
		assert slots.length == backup.length;
		CompilerAsserts.compilationConstant(slots.length);
		CompilerAsserts.compilationConstant(backup.length);
		for (int i = 0; i < backup.length; i++) {
			frame.setObject(slots[i], backup[i]);
		}
	}
}
