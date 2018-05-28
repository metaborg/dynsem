package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm1", type = TermBuild.class),
		@NodeChild(value = "frm2", type = TermBuild.class) })
public abstract class IdentFrames extends NativeOpBuild {

	public IdentFrames(SourceSection source) {
		super(source);
	}

	@Specialization
	public boolean executeClone(DynamicObject frm1, DynamicObject frm2) {
		// TODO proper cast-check for frames
		return (frm1 == frm2);
	}

}
