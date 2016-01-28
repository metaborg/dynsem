package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.dynsem.interpreter.DynSemContext;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class IntBuild extends TermBuild {

	private final int value;

	public IntBuild(int value, SourceSection source) {
		super(source);
		this.value = value;
	}

	@Override
	public IStrategoTerm execute(VirtualFrame frame) {
		DynSemContext context = getContext();
		return context.getTermFactory().makeInt(value);
	}

}
