package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.dynsem.interpreter.DynSemContext;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
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
	@ExplodeLoop
	public IStrategoTerm execute(VirtualFrame frame) {
		DynSemContext context = getContext();
		IStrategoTerm[] kids = new IStrategoTerm[children.length];
		for (int i = 0; i < children.length; i++) {
			kids[i] = children[i].execute(frame);
		}
		if (constructor == null) {
			CompilerDirectives.transferToInterpreterAndInvalidate();
			constructor = context.getTermFactory().makeConstructor(name,
					children.length);
		}

		return context.getTermFactory().makeAppl(constructor, kids);
	}

}
