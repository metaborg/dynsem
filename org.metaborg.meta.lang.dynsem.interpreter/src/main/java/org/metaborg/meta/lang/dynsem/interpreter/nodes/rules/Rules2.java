package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class Rules2 extends Rule {

	@Children private final DirectCallNode[] callNodes;

	public Rules2(DynSemLanguage lang, SourceSection source, CallTarget[] cts) {
		super(lang, source);
		callNodes = new DirectCallNode[cts.length];
		for (int i = 0; i < callNodes.length; i++) {
			callNodes[i] = DirectCallNode.create(cts[i]);
		}
		Truffle.getRuntime().createCallTarget(this);
	}

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		final Object[] args = frame.getArguments();
		for (int i = 0; i < callNodes.length; i++) {
			try {
				return (RuleResult) callNodes[i].call(args);
			} catch (PremiseFailureException rafx) {

			}
		}

		throw new ReductionFailure("Rules failed", InterpreterUtils.createStacktrace());

	}

	@Override
	public boolean isCloningAllowed() {
		return true;
	}

	@Override
	protected boolean isCloneUninitializedSupported() {
		return false;
	}

	@Override
	protected Rule cloneUninitialized() {
		throw new UnsupportedOperationException();
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return "(" + callNodes.length + ") --> "
				+ (callNodes.length > 0 ? callNodes[0].getCallTarget() : "(not avail)");
	}
}
