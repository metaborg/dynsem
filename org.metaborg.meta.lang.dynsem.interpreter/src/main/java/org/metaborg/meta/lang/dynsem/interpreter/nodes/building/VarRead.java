package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class VarRead extends TermBuild {

	private final String name;

	public VarRead(String name, SourceSection source) {
		super(source);
		this.name = name;
	}

	@Override
	public IStrategoTerm execute(VirtualFrame frame) {
		FrameSlot slot = frame.getFrameDescriptor().findFrameSlot(name);
		return (IStrategoTerm) frame.getValue(slot);
	}

}
