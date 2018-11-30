package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.ExplodeLoop.LoopExplosionKind;
import com.oracle.truffle.api.source.SourceSection;

public class MultiRule extends Rule {

	@Children protected final SingleRule[] rules;

	public MultiRule(SourceSection source, SingleRule[] rules) {
		super(source);
		assert rules.length > 1;
		this.rules = rules;
	}

	@Override
	@ExplodeLoop(kind = LoopExplosionKind.FULL_EXPLODE_UNTIL_RETURN)
	public RuleResult evaluateRule(VirtualFrame frame) {
		CompilerAsserts.compilationConstant(rules);
		for (int i = 0; i < rules.length; i++) {
			try {
				return rules[i].evaluateRule(frame);
			} catch (PremiseFailureException pmfex) {
				continue;
			}
		}
		throw new ReductionFailure("No more rules to try", InterpreterUtils.createStacktrace(), this);
	}

}
