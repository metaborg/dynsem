package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class SortFunCallBuild extends TermBuild {

	private final String function;
	private final String sort;

	@Children protected final TermBuild[] children;

	public SortFunCallBuild(String sort, String function, TermBuild[] children, SourceSection source) {
		super(source);
		this.sort = sort;
		this.function = function;
		this.children = children;
	}

	public static SortFunCallBuild create(IStrategoAppl t, FrameDescriptor fd) {
		assert Tools.hasConstructor(t, "NativeFunCall", 4);
		String sort = Tools.stringAt(t, 0).stringValue();
		String function = Tools.stringAt(t, 1).stringValue();
		TermBuild receiver = TermBuild.create(Tools.applAt(t, 2), fd);
		IStrategoList argsT = Tools.listAt(t, 3);
		TermBuild[] children = new TermBuild[argsT.size() + 1];

		children[0] = receiver;
		for (int i = 0; i < argsT.size(); i++) {
			children[i + 1] = TermBuild.create(Tools.applAt(argsT, i), fd);
		}

		return new SortFunCallBuild(sort, function, children, SourceSectionUtil.fromStrategoTerm(t));
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		CompilerDirectives.transferToInterpreterAndInvalidate();
		TermBuild build = InterpreterUtils.notNull(
				getContext().getTermRegistry().lookupNativeTypeAdapterBuildFactory(sort, function, children.length - 1))
				.apply(getSourceSection(), children);
		return replace(build).executeGeneric(frame);
	}
}
