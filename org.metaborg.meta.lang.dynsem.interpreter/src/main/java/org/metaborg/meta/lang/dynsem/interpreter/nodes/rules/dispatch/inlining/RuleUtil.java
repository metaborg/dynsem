package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch.inlining;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.BoundaryRuleNode;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.nodes.Node;

public final class RuleUtil {

	public static Assumption getNearestConstantTermAssumption(Node n) {
		if (n instanceof DynSemRootNode) {
			return ((DynSemRootNode) n).getConstantTermAssumption();
		} else if (n instanceof BoundaryRuleNode) {
			return ((BoundaryRuleNode) n).getConstantInputAssumption();
		} else {
			Node p = n.getParent();
			if (p != null) {
				return getNearestConstantTermAssumption(p);
			}
			return null;
		}
	}
}
