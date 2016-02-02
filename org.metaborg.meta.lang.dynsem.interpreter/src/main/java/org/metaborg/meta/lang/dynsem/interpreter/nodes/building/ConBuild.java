package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.spoofax.interpreter.terms.IStrategoConstructor;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class ConBuild extends TermBuild {

	private String name;

	@Children private final TermBuild[] children;

	@CompilationFinal private IStrategoConstructor constructor;

	public ConBuild(String name, TermBuild[] children, SourceSection source) {
		super(source);
		this.name = name;
		this.children = children;
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		ITermBuildFactory<TermBuild> buildFactory = getContext()
				.lookupTermBuilder(name, children.length);
		TermBuild build = buildFactory.apply(getSourceSection(), children);
		return replace(build).executeGeneric(frame);
	}

}
