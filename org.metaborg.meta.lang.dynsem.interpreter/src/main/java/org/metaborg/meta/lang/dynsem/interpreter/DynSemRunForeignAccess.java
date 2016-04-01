package org.metaborg.meta.lang.dynsem.interpreter;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.Rule;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IConTerm;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.ForeignAccess.Factory;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.RootNode;

public class DynSemRunForeignAccess implements Factory {

	public static final ForeignAccess INSTANCE = ForeignAccess.create(new DynSemRunForeignAccess());

	@Override
	public boolean canHandle(TruffleObject o) {
		return o instanceof DynSemPrimedRun;
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
			throw new IllegalArgumentException(tree.toString() + " not supported");
		}
	}

	private static class DynSemForeignCallerRootNode extends RootNode {

		public DynSemForeignCallerRootNode() {
			super(DynSemLanguage.class, null, null);
		}

		@Override
		public Object execute(VirtualFrame frame) {
			DynSemPrimedRun run = (DynSemPrimedRun) ForeignAccess.getReceiver(frame);
			IConTerm program = run.getProgram();

			return run.getCallTarget().call(Rule.buildArguments(program, program.allSubterms(), new Object[] {}));
		}

	}

	private static class DynSemForeignExecutableCheckNode extends RootNode {
		public DynSemForeignExecutableCheckNode() {
			super(DynSemLanguage.class, null, null);
		}

		@Override
		public Object execute(VirtualFrame frame) {
			Object receiver = ForeignAccess.getReceiver(frame);
			return receiver instanceof DynSemPrimedRun;
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
