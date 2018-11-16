package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class SortFunCallBuild extends TermBuild {

	private final String function;
	private final String sort;

	@Children protected final TermBuild[] children;

	public SortFunCallBuild(String sort, String function, TermBuild[] children, SourceSection source) {
		super(source);
		this.sort = sort;
		this.function = function;
		this.children = children;
	}

	@Override
	@Specialization
	public Object executeGeneric(VirtualFrame frame) {
		TermBuild build = getContext().getTermRegistry()
				.lookupNativeTypeAdapterBuildFactory(sort, function, children.length - 1)
				.apply(getSourceSection(), children);
		return replace(build).executeGeneric(frame);
	}

}