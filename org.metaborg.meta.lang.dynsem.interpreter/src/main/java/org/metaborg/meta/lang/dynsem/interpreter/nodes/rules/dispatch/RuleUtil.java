package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.dispatch;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemRootNode;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.nodes.Node;

public final class RuleUtil {

	public static Assumption getNearestConstantTermAssumption(Node n) {
		CompilerAsserts.neverPartOfCompilation("Should not search for assumption in compiled code");
		if (n instanceof DynSemRootNode) {
			return ((DynSemRootNode) n).getConstantTermAssumption();
		} else {
			Node p = n.getParent();
			if (p != null) {
				return getNearestConstantTermAssumption(p);
			}
			return null;
		}
	}
}
