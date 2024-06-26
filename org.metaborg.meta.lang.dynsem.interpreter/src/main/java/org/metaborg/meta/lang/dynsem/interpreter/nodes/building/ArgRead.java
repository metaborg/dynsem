package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ArgRead extends TermBuild {

	private final int index;

	public ArgRead(int index, SourceSection source) {
		super(source);
		this.index = index;
	}

	// FIXME: we should introduce types here
	@Specialization
	public Object executeRead(VirtualFrame frame) {
		return frame.getArguments()[index];
	}

	public static TermBuild create(IStrategoAppl t) {
		assert Tools.hasConstructor(t, "ArgRead", 1);
		return ArgReadNodeGen.create(Tools.intAt(t, 0).intValue(), SourceUtils.dynsemSourceSectionFromATerm(t));

	}

	@Override
	@TruffleBoundary
	public String toString() {
		return "ArgRead(" + index + ")";
	}

}
