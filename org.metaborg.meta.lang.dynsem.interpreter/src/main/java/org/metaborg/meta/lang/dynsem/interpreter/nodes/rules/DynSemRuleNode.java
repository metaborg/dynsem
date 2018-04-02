package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeCallPremise;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops.BreakNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops.ContinueNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops.WhileNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.CaseMatchPremise;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.RelationPremise;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class DynSemRuleNode extends DynSemNode {

	public DynSemRuleNode(SourceSection source) {
		super(source);
	}
	
	public abstract RuleResult execute(VirtualFrame frame);

	public static DynSemRuleNode create(IStrategoAppl t, FrameDescriptor ruleFD) {
		CompilerAsserts.neverPartOfCompilation();
//		WhileNode: Term * Term * Term * List(Term) * List(Term) -> NativeRule
		if (Tools.hasConstructor(t, "WhileNode", 5)) {
			return WhileNode.create(t, ruleFD);
		}
//	    BreakNode: Term * List(Term) -> NativeRule
		if (Tools.hasConstructor(t, "BreakNode", 2)) {
			return BreakNode.create(t, ruleFD);
		}
//	    ContinueNode: Term * List(Term) -> NativeRule
		if (Tools.hasConstructor(t, "ContinueNode", 2)) {
			return ContinueNode.create(t, ruleFD);
		}
		throw new NotImplementedException("Unsupported dynsemrulenode: " + t);		
	}
}
