package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameLink;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.layouts.FrameLayoutImpl;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.ScopeIdentifier;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.Cached;
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

	@Specialization(guards = { "scopeident == scopeident_cached" })
	public DynamicObject createCached(ScopeIdentifier scopeident, Object links,
			@Cached("lookupListClass()") Class<? extends IListTerm<FrameLink>> linksListClass,
			@Cached("scopeident") ScopeIdentifier scopeident_cached,
			@Cached("lookupProtoFrame(scopeident)") DynamicObject protoFrame) {
		return actualCreate(linksListClass.cast(links), protoFrame);
	}

	@Specialization(replaces = "createCached")
	public DynamicObject executeCreateFrame(ScopeIdentifier scopeident, Object links,
			@Cached("lookupListClass()") Class<? extends IListTerm<FrameLink>> linksListClass) {
		return actualCreate(linksListClass.cast(links), getContext().getProtoFrame(scopeident));
	}

	private DynamicObject actualCreate(IListTerm<FrameLink> links, DynamicObject protoFrame) {
		assert FrameLayoutImpl.INSTANCE.isFrame(protoFrame);

		DynamicObject frame = protoFrame.copy(protoFrame.getShape());
		for (FrameLink link : links) {
			frame.set(link.link(), link.frame());
		}

		return frame;
	}

	protected Class<? extends IListTerm<FrameLink>> lookupListClass() {
		CompilerAsserts.neverPartOfCompilation();
		return getContext().getTermRegistry().getListClass(FrameLink.class);
	}

	protected DynamicObject lookupProtoFrame(ScopeIdentifier scopeident) {
		CompilerAsserts.neverPartOfCompilation();
		return getContext().getProtoFrame(scopeident);
	}

	public static NewFrame create(SourceSection source, TermBuild scope, TermBuild links) {
		return NewFrameNodeGen.create(source, scope, links);
	}

}
