package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Rules extends Rule {

	@Child private DirectCallNode primaryCallNode;
	@Child private DirectCallNode alternativeCallNode;

	public Rules(DynSemLanguage lang, SourceSection source, CallTarget primaryCT, CallTarget alternativeCT) {
		super(lang, source);
		this.primaryCallNode = DirectCallNode.create(primaryCT);
		this.alternativeCallNode = DirectCallNode.create(alternativeCT);
		Truffle.getRuntime().createCallTarget(this);
	}

	private final BranchProfile alternativeTaken = BranchProfile.create();

	@Specialization
	public RuleResult executeFixed(VirtualFrame frame) {
		try {
			return (RuleResult) primaryCallNode.call(frame.getArguments());
		} catch (PremiseFailureException rafx) {
			alternativeTaken.enter();
			return (RuleResult) alternativeCallNode.call(frame.getArguments());
		}
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
		return "(seq) --> " + primaryCallNode.getCallTarget();
	}
}
