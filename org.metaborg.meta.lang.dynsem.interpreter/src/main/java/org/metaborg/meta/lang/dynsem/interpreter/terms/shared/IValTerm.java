package org.metaborg.meta.lang.dynsem.interpreter.terms.shared;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class IValTerm implements IApplTerm {

	@Override
	public Class<? extends IApplTerm> getSortClass() {
		return IValTerm.class;
	}

	@TruffleBoundary
	public static IValTerm create(IStrategoTerm term) {
		return null;
	}

	public static abstract class Build extends TermBuild {

		public Build(SourceSection source) {
			super(source);
		}

		public abstract IValTerm executeIValTerm(VirtualFrame frame);

		@Override
		public IValTerm executeGeneric(VirtualFrame frame) {
			return executeIValTerm(frame);
		}

	}

}
