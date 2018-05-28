package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "labelstring", type = TermBuild.class) })
public abstract class mkLabel extends NativeOpBuild {

	public mkLabel(SourceSection source) {
		super(source);
	}

	@Specialization
	public Label executeString(String l) {
		throw new IllegalStateException("Custom label is not supported: " + l);
	}

}
