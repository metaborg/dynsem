package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.DynSemNode;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.natives.NativeCallPremise;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.terms.util.NotImplementedException;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Premise extends DynSemNode {

	public Premise(SourceSection source) {
		super(source);
	}

	public abstract void execute(VirtualFrame frame);

	@TruffleBoundary
	public static Premise create(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		if (Tools.hasConstructor(t, "CaseMatch", 2)) {
			return CaseMatchPremise.create(lang, t, fd);
		}
		IStrategoAppl premT = Tools.applAt(t, 0);
		if (Tools.hasConstructor(premT, "Relation", 3)) {
			return RelationPremise.create(premT, fd);
		}
		if (Tools.hasConstructor(premT, "Match", 2)) {
			return MatchPremise.create(premT, fd);
		}
		if (Tools.hasConstructor(premT, "NativeRelationPremise", 3)) {
			return NativeCallPremise.create(lang, premT, fd);
		}
		if (Tools.hasConstructor(premT, "TermEq", 2)) {
			return TermEqPremise.create(premT, fd);
		}
		if (Tools.hasConstructor(premT, "Fails", 1)) {
			return FailsPremise.create(lang, premT, fd);
		}

		throw new NotImplementedException("Unsupported premise: " + t);
	}
}
