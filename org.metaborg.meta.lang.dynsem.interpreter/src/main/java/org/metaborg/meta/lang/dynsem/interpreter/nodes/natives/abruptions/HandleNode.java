package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeOperationNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.SnapshotComponentsNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public class HandleNode extends NativeOperationNode {

	@Child private TermBuild throwingNode;
	@Children private TermBuild[] throwingInputComponentsNode;
	@Child private DispatchNode throwingInvokeNode;
	
	@Child private TermBuild catchingNode;
	@Child private DispatchNode handlerInvokeNode;

	public HandleNode(SourceSection source) {
		super(source);
		// TODO Auto-generated constructor stub
	}
	
	private final BranchProfile abrupted = BranchProfile.create();

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		Object throwingTerm = throwingNode.executeGeneric(frame);
		try {
			// here: we need to know what components the throwing relation takes and what the terms that generate them are
			Object[] args = new Object[throwingInputComponentsNode.length + 1];
			args[0] = throwingTerm;
			CompilerAsserts.compilationConstant(throwingInputComponentsNode.length);
			for (int i = 0; i < throwingInputComponentsNode.length; i++) {
				InterpreterUtils.setComponent(getContext(), args, i + 1, throwingInputComponentsNode[i].executeGeneric(frame));
			}
			return throwingInvokeNode.execute(frame, throwingTerm.getClass(), args);
			// success: evaluation was not aborted
		} catch (AbortedEvaluationException abort) {
			// evaluation was aborted
			abrupted.enter();
			// TODO: invoke the handler. watchout for the components!!! 
		}

//		throwingInvokeNode.execute(frame, dispatchClass, args);

		// TODO Auto-generated method stub
		return null;
	}
	

}
