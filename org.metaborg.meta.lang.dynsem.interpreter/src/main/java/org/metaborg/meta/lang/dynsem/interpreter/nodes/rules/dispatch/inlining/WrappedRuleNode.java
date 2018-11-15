package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class WrappedRuleNode extends DynSemNode {

	@Child private RuleNode wrappedRule;

	public WrappedRuleNode(SourceSection source, RuleNode wrappedRule) {
		super(source);
		this.wrappedRule = wrappedRule;
	}

	public RuleResult execute(VirtualFrame frame) {
		return wrappedRule.execute(frame);
	}

	public static final class PassthroughWrappedRule extends WrappedRuleNode {

		public PassthroughWrappedRule(SourceSection source, RuleNode wrappedRule) {
			super(source, wrappedRule);
		}

	}

	public static final class BoundaryRuleWrap extends WrappedRuleNode {
		private final Assumption constantTermAssumption;

		public BoundaryRuleWrap(SourceSection source, RuleNode wrappedRule) {
			super(source, wrappedRule);
			this.constantTermAssumption = Truffle.getRuntime().createAssumption("constant input boundary assumption");
		}

		@Override
		protected Assumption getConstantInputAssumption() {
			return constantTermAssumption;
		}

	}

}
