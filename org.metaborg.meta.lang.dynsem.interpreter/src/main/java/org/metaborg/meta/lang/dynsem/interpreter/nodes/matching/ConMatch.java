package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ConMatch extends MatchPattern {

	private final String name;
	@Children private final MatchPattern[] children;

	public ConMatch(String name, MatchPattern[] children, SourceSection source) {
		super(source);
		this.name = name;
		this.children = children;
	}

	@Specialization
	@ExplodeLoop
	public boolean doApplTerm(VirtualFrame frame, ApplTerm appl) {
		if (name != appl.name() || children.length != appl.size()) {
			return false;
		}
		Object[] subterms = appl.subterms();
		for(int i = 0; i < children.length; i++) {
			if (!children[i].executeMatch(frame, subterms[i])) {
				return false;
			}
		}
		return true;
	}

	@TruffleBoundary
	private static boolean stringEq(String a, String b) {
		return a.equals(b);
	}

	public static ConMatch create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "Con", 3);
		String constr = Tools.stringAt(t, 0).stringValue();
		IStrategoList childrenT = Tools.listAt(t, 1);
		MatchPattern[] children = new MatchPattern[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = MatchPattern.create(Tools.applAt(childrenT, i), fd);
		}

		return ConMatchNodeGen.create(constr.intern(), children, SourceUtils.dynsemSourceSectionFromATerm(t));
	}
}
