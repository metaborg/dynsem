package org.metaborg.meta.lang.dynsem.interpreter.utils;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameInstance;
import com.oracle.truffle.api.frame.FrameInstance.FrameAccess;
import com.oracle.truffle.api.frame.FrameInstanceVisitor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public final class InterpreterUtils {

	public static void setComponent(DynSemContext ctx, Object[] arguments, int idx, Object val) {
		CompilerAsserts.compilationConstant(ctx.isSafeComponentsEnabled());
		if (ctx.isSafeComponentsEnabled() && val == null) {
			throw new ReductionFailure("Attempted to write null component at index " + idx, createStacktrace());
		}
		arguments[idx] = val;
	}

	public static Object getComponent(DynSemContext ctx, Object[] arguments, int idx) {
		CompilerAsserts.compilationConstant(ctx.isSafeComponentsEnabled());
		if(ctx.isSafeComponentsEnabled() && idx >= arguments.length) {
			throw new ReductionFailure("Attempted access to unbound component at index " + idx, createStacktrace());
		}
		final Object val = arguments[idx];
		if (ctx.isSafeComponentsEnabled() && val == null) {
			throw new ReductionFailure("Attempted access to null component at index " + idx, createStacktrace());
		}
		return val;
	}

	public static Object readSlot(DynSemContext ctx, VirtualFrame frame, FrameSlot slot) {
		final Object val = frame.getValue(slot);
		CompilerAsserts.compilationConstant(ctx.isSafeComponentsEnabled());
		if (ctx.isSafeComponentsEnabled() && val == null) {
			throw new ReductionFailure("Accessed null value for slot " + slot.getIdentifier(), createStacktrace());
		}

		return val;
	}

	public static void writeSlot(DynSemContext ctx, VirtualFrame frame, FrameSlot slot, Object val) {
		CompilerAsserts.compilationConstant(ctx.isSafeComponentsEnabled());
		if (ctx.isSafeComponentsEnabled() && val == null) {
			throw new ReductionFailure("Attempted to write null value for slot " + slot.getIdentifier(),
					createStacktrace());
		}
		frame.setObject(slot, val);
	}

	public static <T> T notNull(DynSemContext ctx, T val) {
		CompilerAsserts.compilationConstant(ctx.isSafeComponentsEnabled());
		if (ctx.isSafeComponentsEnabled() && val == null) {
			throw new ReductionFailure("Null value encountered", createStacktrace());
		}
		return val;
	}

	@TruffleBoundary
	public static String createStacktrace() {
		CompilerAsserts.neverPartOfCompilation();
		final StringBuilder str = new StringBuilder();

		Truffle.getRuntime().iterateFrames(new FrameInstanceVisitor<Integer>() {

			@Override
			public Integer visitFrame(FrameInstance frameInstance) {
				CallTarget callTarget = frameInstance.getCallTarget();
				Frame frame = frameInstance.getFrame(FrameAccess.READ_ONLY);
				RootNode rn = ((RootCallTarget) callTarget).getRootNode();
				if (rn.getClass().getName().contains("DynSemRuleForeignAccess")) {
					return 1;
				}
				if (str.length() > 0) {
					str.append(System.getProperty("line.separator"));
				}
				str.append("Frame: ").append(rn.toString());
				FrameDescriptor frameDescriptor = frame.getFrameDescriptor();
				for (FrameSlot s : frameDescriptor.getSlots()) {
					str.append("\n\t ").append(s.getIdentifier()).append("=").append(frame.getValue(s));
				}
				return null;
			}

		});

		return str.toString();
	}

	@TruffleBoundary
	public static int stackDepth() {
		CompilerAsserts.neverPartOfCompilation();
		final StringBuilder str = new StringBuilder();
		Truffle.getRuntime().iterateFrames(new FrameInstanceVisitor<Integer>() {

			@Override
			public Integer visitFrame(FrameInstance frameInstance) {
				CallTarget callTarget = frameInstance.getCallTarget();
				RootNode rn = ((RootCallTarget) callTarget).getRootNode();
				if (rn.getClass().getName().contains("DynSemRuleForeignAccess")) {
					return 1;
				}
				str.append(" ");
				return null;
			}

		});
		return str.length();
	}
}
