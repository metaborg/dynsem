package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameLink;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "scope", type = TermBuild.class),
		@NodeChild(value = "links", type = TermBuild.class) })
public abstract class NewFrame extends NativeOpBuild {

	public NewFrame(SourceSection source) {
		super(source);
	}

	@Specialization
	public DynamicObject executeCreateFrame(ScopeIdentifier scopeident, IListTerm<?> links) {
		DynSemContext ctx = getContext();

		IListTerm<FrameLink> actualLinks = ctx.getTermRegistry().getListClass(FrameLink.class).cast(links);

		DynamicObject protoFrame = ctx.getProtoFrame(scopeident);
		assert FrameLayoutImpl.INSTANCE.isFrame(protoFrame);

		DynamicObject frame = protoFrame.copy(protoFrame.getShape());
		for (FrameLink link : actualLinks) {
			frame.set(link.link(), link.frame());
		}

		return frame;
	}

	public static NewFrame create(SourceSection source, TermBuild scope, TermBuild links) {
		return NewFrameNodeGen.create(source, scope, links);
	}

}
