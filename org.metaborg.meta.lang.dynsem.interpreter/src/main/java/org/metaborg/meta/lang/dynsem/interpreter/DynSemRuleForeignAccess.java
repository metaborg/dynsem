package org.metaborg.meta.lang.dynsem.interpreter;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.ForeignAccess.Factory;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.RootNode;

public class DynSemRuleForeignAccess implements Factory {

	public static final ForeignAccess INSTANCE = ForeignAccess.create(new DynSemRuleForeignAccess());

	@Override
	public boolean canHandle(TruffleObject o) {
		return o instanceof DynSemRule;
	}

	@Override
	public CallTarget accessMessage(Message tree) {
		if (Message.createExecute(0).equals(tree)) {
			return Truffle.getRuntime().createCallTarget(new DynSemForeignCallerRootNode());

		} else if (Message.IS_NULL.equals(tree)) {
			return Truffle.getRuntime().createCallTarget(new DynSemForeignNullCheckNode());
		} else if (Message.IS_EXECUTABLE.equals(tree)) {
			return Truffle.getRuntime().createCallTarget(new DynSemForeignExecutableCheckNode());
		} else if (Message.IS_BOXED.equals(tree)) {
			return Truffle.getRuntime().createCallTarget(RootNode.createConstantNode(false));
		} else {
			throw new IllegalArgumentException(stringConcat(tree.toString(), " not supported"));
		}
	}

	@TruffleBoundary
	private String stringConcat(String s1, String s2) {
		return s1 + s2;
	}

	private static class DynSemForeignCallerRootNode extends RootNode {

		public DynSemForeignCallerRootNode() {
			super(DynSemLanguage.class, null, null);
		}

		@Override
		public Object execute(VirtualFrame frame) {
			DynSemRule rule = (DynSemRule) ForeignAccess.getReceiver(frame);
			return rule.getRuleTarget().getCallTarget().call(ForeignAccess.getArguments(frame).toArray());
		}

	}

	private static class DynSemForeignExecutableCheckNode extends RootNode {
		public DynSemForeignExecutableCheckNode() {
			super(DynSemLanguage.class, null, null);
		}

		@Override
		public Object execute(VirtualFrame frame) {
			Object receiver = ForeignAccess.getReceiver(frame);
			return receiver instanceof DynSemRule;
		}
	}

	private static class DynSemForeignNullCheckNode extends RootNode {
		public DynSemForeignNullCheckNode() {
			super(DynSemLanguage.class, null, null);
		}

		@Override
		public Object execute(VirtualFrame frame) {
			Object receiver = ForeignAccess.getReceiver(frame);
			return receiver == null;
		}
	}

}
