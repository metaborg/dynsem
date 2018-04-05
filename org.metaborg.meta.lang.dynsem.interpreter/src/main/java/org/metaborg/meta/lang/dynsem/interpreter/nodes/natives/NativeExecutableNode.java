package org.metaborg.meta.lang.dynsem.interpreter.nodes.natives;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions.HandleNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.abruptions.RaiseNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops.BreakNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops.ContinueNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.loops.WhileNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.RuleResult;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NativeExecutableNode extends DynSemNode {

	public NativeExecutableNode(SourceSection source) {
		super(source);
	}

	public abstract RuleResult execute(VirtualFrame frame);
	
	public static NativeExecutableNode create(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor ruleFD) {
		CompilerAsserts.neverPartOfCompilation();
		// WhileNode: Term * Term * Term * List(Term) * List(Term) -> NativeRule
		if (Tools.hasConstructor(t, "WhileNode", 5)) {
			return WhileNode.create(lang, t, ruleFD);
		}
		// BreakNode: Term * List(Term) -> NativeRule
		if (Tools.hasConstructor(t, "BreakNode", 2)) {
			return BreakNode.create(lang, t, ruleFD);
		}
		// ContinueNode: Term * List(Term) -> NativeRule
		if (Tools.hasConstructor(t, "ContinueNode", 2)) {
			return ContinueNode.create(lang, t, ruleFD);
		}
		// Raise: List(Term) * Term -> NativeRule
		if (Tools.hasConstructor(t, "Raise", 2)) {
			return RaiseNode.create(lang, t, ruleFD);
		}
		// Handle: Term * Term -> NativeRule
		// Handle: Term * Term * Term -> NativeRule
		if (Tools.hasConstructor(t, "Handle", 2) || Tools.hasConstructor(t, "Handle", 3)) {
			return HandleNode.create(lang, t, ruleFD);
		}

		throw new NotImplementedException("Unsupported dynsemrulenode: " + t);
	}
}
