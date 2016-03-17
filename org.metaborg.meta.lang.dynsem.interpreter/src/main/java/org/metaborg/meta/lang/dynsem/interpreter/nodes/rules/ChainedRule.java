package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.utilities.BranchProfile;

public class ChainedRule extends Rule {

	@CompilationFinal private ChainedRule next;

	private final BranchProfile ruleFailedTaken = BranchProfile.create();

	private String constr;
	private String name;
	private int arity;
	@Child private DirectCallNode callNode;

	public ChainedRule(Rule rule) {
		super(rule.getSourceSection(), rule.getFrameDescriptor());
		this.constr = rule.getConstructor();
		this.name = rule.getName();
		this.arity = rule.getArity();
		this.callNode = DirectCallNode.create(rule.getCallTarget());
		Truffle.getRuntime().createCallTarget(this);
	}

	@Override
	public int getArity() {
		return arity;
	}

	@Override
	public String getConstructor() {
		return constr;
	}

	@Override
	public String getName() {
		return name;
	}

	public void addNext(Rule r) {
		if (next != null) {
			next.addNext(r);
		} else {
			next = new ChainedRule(r);
		}
	}

	@Override
	public RuleResult execute(VirtualFrame frame) {
		try {
			return (RuleResult) callNode.call(frame, frame.getArguments());
		} catch (PremiseFailure pfx) {
			ruleFailedTaken.enter();
			if (next != null) {
				return next.execute(frame);
			} else {
				throw pfx;
			}
		}
	}

}
