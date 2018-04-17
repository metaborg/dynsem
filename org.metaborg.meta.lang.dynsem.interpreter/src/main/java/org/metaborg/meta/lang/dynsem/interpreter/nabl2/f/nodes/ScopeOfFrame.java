package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class) })
public abstract class ScopeOfFrame extends NativeOpBuild {

	public ScopeOfFrame(SourceSection source) {
		super(source);
	}

	@Specialization
	public ScopeIdentifier executeScopeOf(DynamicObject frm) {
		throw new RuntimeException("Scope of frame NOT IMPLEMENTED");
	}

	public static ScopeOfFrame create(SourceSection source, TermBuild frm) {
		return ScopeOfFrameNodeGen.create(source, frm);
	}

}
