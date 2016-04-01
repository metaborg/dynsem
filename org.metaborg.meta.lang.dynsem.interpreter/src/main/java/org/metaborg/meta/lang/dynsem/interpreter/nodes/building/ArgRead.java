package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class ArgRead extends TermBuild {

	private final int index;

	public ArgRead(int index, SourceSection source) {
		super(source);
		this.index = index;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		return frame.getArguments()[index];
	}

	public static TermBuild create(IStrategoAppl t) {
		assert Tools.hasConstructor(t, "ArgRead", 1);
		return new ArgRead(Tools.intAt(t, 0).intValue(), SourceSectionUtil.fromStrategoTerm(t));
	}

	@Override
	public String toString() {
		return "ArgRead(" + index + ")";
	}

}
