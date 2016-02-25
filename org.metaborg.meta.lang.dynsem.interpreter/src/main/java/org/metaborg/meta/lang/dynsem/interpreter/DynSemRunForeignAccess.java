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

	public static final ForeignAccess INSTANCE = ForeignAccess
			.create(new DynSemRunForeignAccess());

	@Override
	public boolean canHandle(TruffleObject o) {
		return o instanceof DynSemPrimedRun;
	}

	@Override
	public CallTarget accessMessage(Message tree) {
		if (Message.createExecute(0).equals(tree)) {
			return Truffle.getRuntime().createCallTarget(
					new DynSemRunForeignCallerNode());
		} else {
			throw new IllegalArgumentException(tree.toString()
					+ " not supported");
		}
	}

	private static class DynSemRunForeignCallerNode extends RootNode {

		public DynSemRunForeignCallerNode() {
			super(DynSemLanguage.class, null, null);
		}

		@Override
		public Object execute(VirtualFrame frame) {
			DynSemPrimedRun run = (DynSemPrimedRun) ForeignAccess
					.getReceiver(frame);
			IConTerm program = run.getProgram();

			return run.getCallTarget().call(
					Rule.buildArguments(program, program.allSubterms(),
							new Object[] {}));
		}

	}

}
