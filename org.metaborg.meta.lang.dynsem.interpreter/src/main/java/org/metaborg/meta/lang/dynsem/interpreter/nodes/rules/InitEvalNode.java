package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.InitProtoFrames;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes.InitNaBL2Node;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining.ConstantTermDispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining.InliningDispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.utilities.AlwaysValidAssumption;

public class InitEvalNode extends DynSemRootNode {
	private final ITerm program;
	@Child private InitNaBL2Node initNabl2;
	@Child private InitProtoFrames initProtoFrames;
	@Child private InliningDispatchNode initDispatch;

	public InitEvalNode(DynSemLanguage lang, SourceSection source, ITerm program) {
		super(lang, source, new FrameDescriptor(), AlwaysValidAssumption.INSTANCE);
		this.program = program;
		this.initNabl2 = new InitNaBL2Node(source);
		this.initProtoFrames = new InitProtoFrames(source);
		// this.initDispatch = DispatchNode.create(source, "init");
		this.initDispatch = ConstantTermDispatchNode.create(program.getClass(), "init");

		Truffle.getRuntime().createCallTarget(this);
	}

	@Override
	public RuleResult execute(VirtualFrame frame) {
		if (getContext().isNativeFramesEnabled()) {
			initNabl2.execute(frame);
			initProtoFrames.execute(frame);
		}
		// RuleResult res = initDispatch.execute(program.getClass(), new Object[] { program });
		// return res;
		return initDispatch.execute(new Object[] { program });
	}

	@Override
	public boolean isCloningAllowed() {
		return false;
	}

	@Override
	protected boolean isCloneUninitializedSupported() {
		return false;
	}

}
