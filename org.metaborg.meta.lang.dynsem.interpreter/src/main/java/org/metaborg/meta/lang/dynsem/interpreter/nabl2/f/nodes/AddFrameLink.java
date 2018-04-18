package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameLink;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class), @NodeChild(value = "link", type = TermBuild.class) })
public abstract class AddFrameLink extends NativeOpBuild {

	public AddFrameLink(SourceSection source) {
		super(source);
	}

	@Specialization
	public DynamicObject executeLink(DynamicObject frm, FrameLink link) {
		throw new RuntimeException("AddFrameLink not implemented");
	}

	public static AddFrameLink create(SourceSection source, TermBuild frm, TermBuild link) {
		return AddFrameLinkNodeGen.create(source, frm, link);
	}

}
