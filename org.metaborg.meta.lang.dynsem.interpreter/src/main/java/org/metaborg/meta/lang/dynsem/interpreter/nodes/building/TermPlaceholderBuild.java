package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.TermPlaceholder;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class TermPlaceholderBuild extends TermBuild {

	public TermPlaceholderBuild(SourceSection source) {
		super(source);
	}

	@Specialization
	public ITerm executePlaceholder(VirtualFrame frame) {
		return TermPlaceholder.INSTANCE;
	}

}
