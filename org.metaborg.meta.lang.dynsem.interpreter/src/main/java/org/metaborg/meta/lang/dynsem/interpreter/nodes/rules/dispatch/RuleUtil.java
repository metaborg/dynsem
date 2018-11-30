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

	// public static RuleRootNode fuseRoots(RuleRootNode[] roots, DynSemContext ctx) {
	//
	// if (roots.length == 0) {
	// return null;
	// }
	//
	// if (roots.length == 1) {
	// return roots[0];
	// }
	//
	// FrameDescriptor jointFD = new FrameDescriptor();
	// Rule[] rulesToJoin = new Rule[roots.length];
	// for (int i = 0; i < roots.length; i++) {
	// RuleRootNode root = roots[i];
	// FrameDescriptor fd = root.getFrameDescriptor();
	// List<? extends FrameSlot> slots = fd.getSlots();
	// for (FrameSlot slot : slots) {
	// assert jointFD.findFrameSlot(slot.getIdentifier()) == null;
	// jointFD.addFrameSlot(slot.getIdentifier(), slot.getKind());
	// }
	// assert root.getRuleNode() instanceof SingleRule;
	// rulesToJoin[i] = SingleRule.create(ctx.getRuleRegistry().getLanguage(), root.getRuleSourceTerm(), jointFD,
	// ctx.getTermRegistry());
	// }
	//// return RuleRootNodeGen.create(lang, sourceSection, frameDescriptor, arrowName, dispatchClass, rule, ruleT);
	// }
}
