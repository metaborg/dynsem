package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameLink;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Label;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "label", type = TermBuild.class),
		@NodeChild(value = "frm", type = TermBuild.class) })
public abstract class NewFrameLink extends NativeOpBuild {

	public NewFrameLink(SourceSection source) {
		super(source);
	}

	@Specialization
	public FrameLink execLink(Label label, DynamicObject f) {
		return new FrameLink(label, f);
	}

	public static NewFrameLink create(SourceSection source, TermBuild label, TermBuild frm) {
		return NewFrameLinkNodeGen.create(source, label, frm);
	}

}
