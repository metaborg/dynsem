package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class ConBuild extends TermBuild {

	private String name;

	@Children private final TermBuild[] children;

	public ConBuild(String name, TermBuild[] children, SourceSection source) {
		super(source);
		this.name = name;
		this.children = children;
	}

	public static ConBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "Con", 2);
		String constr = Tools.stringAt(t, 0).stringValue();

		IStrategoList childrenT = Tools.listAt(t, 1);
		TermBuild[] children = new TermBuild[childrenT.size()];
		for (int i = 0; i < children.length; i++) {
			children[0] = TermBuild.create(Tools.applAt(childrenT, i), fd);
		}

		return new ConBuild(constr, children,
				SourceSectionUtil.fromStrategoTerm(t));
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		ITermBuildFactory buildFactory = getContext().lookupTermBuilder(name,
				children.length);
		TermBuild build = buildFactory.apply(getSourceSection(), children);
		return replace(build).executeGeneric(frame);
	}

}
