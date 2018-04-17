package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

public abstract class mkLabelI extends NativeOpBuild {

	public mkLabelI(SourceSection source) {
		super(source);
	}

	@Specialization
	public Label executeI() {
		return Label.I;
	}

	public static NativeOpBuild create(SourceSection source) {
		return mkLabelINodeGen.create(source);
	}

}
