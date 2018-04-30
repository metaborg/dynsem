package org.metaborg.meta.lang.dynsem.interpreter.nodes.building.con;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.concrete.ApplTerm;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.source.SourceSection;

public final class ConBuild extends TermBuild {

	private final String name;
	private final String sort;

	@Children private TermBuild[] children;

	public ConBuild(SourceSection source, String name, String sort, TermBuild[] children) {
		super(source);
		this.sort = sort;
		this.name = name;
		this.children = children;
	}

	@Override
	public ApplTerm executeGeneric(VirtualFrame frame) {
		return executeApplTerm(frame);
	}

	@Override
	public ApplTerm executeEvaluated(VirtualFrame frame, Object... terms) {
		return doCreate(terms);
	}

	@Override
	public ApplTerm executeApplTerm(VirtualFrame frame) {
		return doCreate(evalArgs(frame));
	}

	public ApplTerm doCreate(Object[] args) {
		return new ApplTerm(sort, name, args, null);
	}

	@ExplodeLoop
	private Object[] evalArgs(VirtualFrame frame) {
		Object[] args = new Object[children.length];

		CompilerAsserts.compilationConstant(children.length);

		for (int i = 0; i < children.length; i++) {
			args[i] = children[i].executeGeneric(frame);
		}
		return args;
	}



}
