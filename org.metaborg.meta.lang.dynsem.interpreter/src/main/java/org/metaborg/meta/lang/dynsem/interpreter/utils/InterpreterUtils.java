package org.metaborg.meta.lang.dynsem.interpreter.utils;

import java.io.File;

import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.imploder.IToken;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameInstance;
import com.oracle.truffle.api.frame.FrameInstance.FrameAccess;
import com.oracle.truffle.api.frame.FrameInstanceVisitor;
import com.oracle.truffle.api.nodes.RootNode;

public final class InterpreterUtils {

	// public static void setComponent(DynSemContext ctx, Object[] arguments, int idx, Object val, Node location) {
	// CompilerAsserts.compilationConstant(ctx.isSafeComponentsEnabled());
	// if (ctx.isSafeComponentsEnabled() && val == null) {
	// throw new ReductionFailure("Attempted to write null component at index " + idx, createStacktrace(),
	// location);
	// }
	// arguments[idx] = val;
	// }
	//
	// public static Object getComponent(DynSemContext ctx, Object[] arguments, int idx, Node location) {
	// CompilerAsserts.compilationConstant(ctx.isSafeComponentsEnabled());
	// if (ctx.isSafeComponentsEnabled() && idx >= arguments.length) {
	// throw new ReductionFailure("Attempted access to unbound component at index " + idx, createStacktrace(),
	// location);
	// }
	// final Object val = arguments[idx];
	// if (ctx.isSafeComponentsEnabled() && val == null) {
	// throw new ReductionFailure("Attempted access to null component at index " + idx, createStacktrace(),
	// location);
	// }
	// return val;
	// }
	//
	// public static Object readSlot(DynSemContext ctx, VirtualFrame frame, FrameSlot slot, Node location) {
	// final Object val = frame.getValue(slot);
	// CompilerAsserts.compilationConstant(ctx.isSafeComponentsEnabled());
	// if (ctx.isSafeComponentsEnabled() && val == null) {
	// throw new ReductionFailure("Accessed null value for slot " + slot.getIdentifier(), createStacktrace(),
	// location);
	// }
	//
	// return val;
	// }
	//
	// public static void writeSlot(DynSemContext ctx, VirtualFrame frame, FrameSlot slot, Object val, Node location) {
	// CompilerAsserts.compilationConstant(ctx.isSafeComponentsEnabled());
	// if (ctx.isSafeComponentsEnabled() && val == null) {
	// throw new ReductionFailure("Attempted to write null value for slot " + slot.getIdentifier(),
	// createStacktrace(), location);
	// }
	// frame.setObject(slot, val);
	// }
	//
	// public static <T> T notNull(DynSemContext ctx, T val, Node location) {
	// CompilerAsserts.compilationConstant(ctx.isSafeComponentsEnabled());
	// if (ctx.isSafeComponentsEnabled() && val == null) {
	// throw new ReductionFailure("Null value encountered", createStacktrace(), location);
	// }
	// return val;
	// }

	@TruffleBoundary
	public static void printlnOut(Object o) {
		System.out.println(o);
	}

	@TruffleBoundary
	public static void printlnErr(Object o) {
		System.err.println(o);
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
				Object[] arguments = frame.getArguments();
				if (arguments.length > 0) {
					Object inputTerm = arguments[0];
					str.append(findProgramLocation(inputTerm));
				}
				str.append(" ").append(rn.toString());
				if (arguments.length > 0) {
					str.append(" | AST: ").append(arguments[0].toString());
				}
				return null;
			}

		});

		return str.toString();
	}

	private static Object findProgramLocation(Object inputTerm) {
		if (inputTerm instanceof ITerm) {
			IStrategoTerm t = ((ITerm) inputTerm).getStrategoTerm();
			if (t != null) {
				ImploderAttachment imploder = ImploderAttachment.get(t);
				if (imploder != null) {
					IToken ltoken = imploder.getLeftToken();
					if (ltoken != null) {
						return string2filename(ltoken.getFilename()) + ":" + (ltoken.getLine() + 1) + ":"
								+ (ltoken.getColumn() + 1);
					}
				}
			}
		}
		return "<unavailable>";
	}

	private static String string2filename(String path) {
		return new File(path).getName();
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
