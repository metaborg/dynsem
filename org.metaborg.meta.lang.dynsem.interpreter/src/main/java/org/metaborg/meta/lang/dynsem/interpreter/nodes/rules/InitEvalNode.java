package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.InitProtoFrames;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes.InitNaBL2Node;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.InlinedDispatch;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.utilities.AlwaysValidAssumption;

public class InitEvalNode extends DynSemRootNode {
	private final ITerm program;
	@Child private InitNaBL2Node initNabl2;
	@Child private InitProtoFrames initProtoFrames;
	@Child private InlinedDispatch initDispatch;

	public InitEvalNode(DynSemLanguage lang, SourceSection source, ITerm program) {
		super(lang, source, new FrameDescriptor(), AlwaysValidAssumption.INSTANCE);
		this.program = program;
		this.initNabl2 = new InitNaBL2Node(source);
		this.initProtoFrames = new InitProtoFrames(source);
		this.initDispatch = InlinedDispatch.create(source, "init");
	}

	@Override
	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame) {
		if (getContext().isNativeFramesEnabled()) {
			initNabl2.execute(frame);
			initProtoFrames.execute(frame);
		}
		RuleResult res = null;
		for (int i = 0; i < 30; i++) {
			// InterpreterUtils.printlnOut(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			long st = System.nanoTime();
			res = initDispatch.execute(new Object[] { program });
			long et = System.nanoTime();
			InterpreterUtils.printlnOut(et - st);
			// InterpreterUtils.printlnOut("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		}
		return res;
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
