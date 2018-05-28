package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes.InitProtoFrames;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes.InitNaBL2Node;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.DispatchNode;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class InitEvalNode extends DynSemRootNode {
	private final ITerm program;
	@Child private InitNaBL2Node initNabl2;
	@Child private InitProtoFrames initProtoFrames;
	@Child private DispatchNode initDispatch;

	public InitEvalNode(DynSemLanguage lang, SourceSection source, ITerm program) {
		super(lang, source);
		this.program = program;
		this.initNabl2 = new InitNaBL2Node(source);
		this.initProtoFrames = new InitProtoFrames(source);
		this.initDispatch = DispatchNode.create(source, "init");

		Truffle.getRuntime().createCallTarget(this);
	}

	@Override
	public Object execute(VirtualFrame frame) {
		if (getContext().isNativeFramesEnabled()) {
			initNabl2.execute(frame);
			initProtoFrames.execute(frame);
		}
		return initDispatch.execute(program.getClass(), new Object[] { program });
	}

	@Override
	public boolean isCloningAllowed() {
		return false;
	}

	@Override
	protected boolean isCloneUninitializedSupported() {
		return false;
	}

	@Override
	protected Rule cloneUninitialized() {
		throw new UnsupportedOperationException();
	}

}
