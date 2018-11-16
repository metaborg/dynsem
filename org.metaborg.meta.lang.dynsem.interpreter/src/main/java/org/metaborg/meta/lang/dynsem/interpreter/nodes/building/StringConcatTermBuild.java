package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "left", type = TermBuild.class),
		@NodeChild(value = "right", type = TermBuild.class) })
public abstract class StringConcatTermBuild extends TermBuild {

	public StringConcatTermBuild(SourceSection source) {
		super(source);
	}

	@Specialization
	@TruffleBoundary
	public String executeStrings(String l, String r) {
		return l + r;
	}


}
