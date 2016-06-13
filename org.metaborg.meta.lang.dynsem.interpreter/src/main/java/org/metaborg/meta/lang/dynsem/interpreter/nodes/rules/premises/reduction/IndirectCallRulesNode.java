package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.reduction;

import org.metaborg.meta.lang.dynsem.interpreter.PremiseFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleRoot;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public class IndirectCallRulesNode extends DynSemNode {

	public IndirectCallRulesNode(SourceSection source) {
		super(source);
	}

	@ExplodeLoop
	public RuleResult execute(VirtualFrame frame, RuleRoot[] rules, Object[] args) {
		for (int i = 0; i < rules.length; i++) {
			try {
				return rules[i].execute(frame);
			} catch (PremiseFailure pfx) {
				;
			}
		}

		throw PremiseFailure.INSTANCE;
	}

}
