package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "frm", type = TermBuild.class)
public abstract class CloneFrame extends NativeOpBuild {

	public CloneFrame(SourceSection source) {
		super(source);
	}

	@Specialization
	public DynamicObject executeClone(DynamicObject frm) {
		throw new IllegalStateException("CloneFrame not implemented");
	}

	public static CloneFrame create(SourceSection source, TermBuild frm) {
		return CloneFrameNodeGen.create(source, frm);
	}

}
