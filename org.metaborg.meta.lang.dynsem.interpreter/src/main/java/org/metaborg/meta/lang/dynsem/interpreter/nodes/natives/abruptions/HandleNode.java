package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions;

import java.util.Arrays;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeExecutableNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchChainRoot;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

public class HandleNode extends NativeExecutableNode {

	@Child private TermBuild evalBuildNode;
	@Child private DispatchNode evalDispatchNode;

	@Child private TermBuild catchBuildNode;
	@Child private TermBuild continueBuildNode;
	@Child private DispatchNode continueDispatchNode;

	@Child private HandlerBuild handlerBuildNode;

	@Child private DispatchChainRoot handlerDispatch;

	// @CompilationFinal private CallTarget handlerCallTarget;

	public HandleNode(SourceSection source, TermBuild evalBuildNode, TermBuild catchBuildNode,
			TermBuild continueBuildNode) {
		super(source);
		this.evalBuildNode = evalBuildNode;
		this.catchBuildNode = catchBuildNode;
		this.continueBuildNode = continueBuildNode;
		this.handlerBuildNode = new HandlerBuild(source);
		this.evalDispatchNode = DispatchNode.create(source, "");
		this.continueDispatchNode = DispatchNode.create(source, "");
	}

	private final BranchProfile catchTaken = BranchProfile.create();
	private final BranchProfile handlerFails = BranchProfile.create();
	private final ConditionProfile continueExistsCondition = ConditionProfile.createBinaryProfile();

	// FIXME: @ExplodeLoop
	@Override
	public RuleResult execute(VirtualFrame frame) {
		final Object[] handleArgs = frame.getArguments();
		ITerm evalT = (ITerm) evalBuildNode.executeGeneric(frame);
		RuleResult result = null;
		try {
			// try to evaluate the wrapped body
			Object[] args = Arrays.copyOf(handleArgs, handleArgs.length);
			args[0] = evalT;
			result = evalDispatchNode.execute(evalT.dispatchkey(), args);
		} catch (AbortedEvaluationException abex) {
			catchTaken.enter();
			Object catchingT = catchBuildNode.executeGeneric(frame);

			ITerm handlerT = (ITerm) handlerBuildNode.execute(frame, abex.getThrown(), catchingT);
			// handler gets the ROs from the handle
			// and the RW from the exception
			Object[] rwComps = abex.getComponents();

			Object[] args = new Object[handleArgs.length];
			args[0] = handlerT;
			final int numRoComps = handleArgs.length - rwComps.length - 1;
			final int numRwComps = rwComps.length;

			// copy the RO components
			for (int i = 0; i < numRoComps; i++) {
				args[i + 1] = handleArgs[i + 1];
			}
			// copy the RW components
			for (int i = 0; i < numRwComps; i++) {
				args[i + numRoComps + 1] = rwComps[i];
			}

			if (handlerDispatch == null) {
				 CompilerDirectives.transferToInterpreterAndInvalidate();
				handlerDispatch = insert(
						DispatchChainRoot.createUninitialized(getSourceSection(), "",
								handlerT.dispatchkey(), true));
			}
			try {
				return handlerDispatch.execute(args);
			} catch (PremiseFailureException rafx) {
				handlerFails.enter();
				throw abex;
			}
		}

		if (continueExistsCondition.profile(continueBuildNode == null)) {
			return result;
		} else {
			ITerm continueT = (ITerm) continueBuildNode.executeGeneric(frame);
			// continue gets the ROs from the handle
			// and the RW from the eval result
			Object[] rwComps = result.components;
			Object[] args = new Object[handleArgs.length];
			args[0] = continueT;
			final int numRoComps = handleArgs.length - rwComps.length - 1;
			final int numRwComps = rwComps.length;

			// copy the RO components
			for (int i = 0; i < numRoComps; i++) {
				args[i + 1] = handleArgs[i + 1];
			}
			// copy the RW components
			for (int i = 0; i < numRwComps; i++) {
				args[i + numRoComps + 1] = rwComps[i];
			}

			return continueDispatchNode.execute(continueT.dispatchkey(), args);
		}
	}

	public static HandleNode create(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Handle", 2) || Tools.hasConstructor(t, "Handle", 3);
		// Handle: Term * Term * Term -> NativeRule
		TermBuild evalBuildNode = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild catchBuildNode = TermBuild.create(Tools.applAt(t, 1), fd);
		TermBuild continueBuildNode = t.getConstructor().getArity() == 3 ? TermBuild.create(Tools.applAt(t, 2), fd)
				: null;

		return new HandleNode(SourceUtils.dynsemSourceSectionFromATerm(t), evalBuildNode, catchBuildNode,
				continueBuildNode);
	}

}
