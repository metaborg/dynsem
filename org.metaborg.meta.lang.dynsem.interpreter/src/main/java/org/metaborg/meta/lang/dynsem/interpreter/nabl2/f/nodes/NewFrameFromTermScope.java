package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes.GetScopeOfTermNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.source.SourceSection;

public final class NewFrameFromTermScope {

	public static NewFrame create(SourceSection source, TermBuild t, TermBuild links) {
		return NewFrameNodeGen.create(source, GetScopeOfTermNodeGen.create(source, t), links);
	}

}
