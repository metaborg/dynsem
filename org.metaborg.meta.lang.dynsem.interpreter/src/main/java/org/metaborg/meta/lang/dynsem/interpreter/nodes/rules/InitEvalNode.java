package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.InitNaBL2Node;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class InitEvalNode extends DynSemRootNode {
	private final ITerm program;
	@Child private InitNaBL2Node initNabl2;
	@Child private DispatchNode rootDispatch;

	public InitEvalNode(DynSemLanguage lang, SourceSection sourceSection, ITerm program) {
		super(lang, sourceSection);
		this.program = program;
		this.initNabl2 = new InitNaBL2Node(sourceSection);
		this.rootDispatch = DispatchNodeGen.create(sourceSection, "init");

		Truffle.getRuntime().createCallTarget(this);
	}

	@Override
	public Object execute(VirtualFrame frame) {
		initNabl2.execute(frame);
		System.err.println("Early exit from InitNode");
		System.exit(1);
		return rootDispatch.execute(program.getClass(), new Object[] { program });
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
