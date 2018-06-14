package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameLink;
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

	@Override
	public abstract DynamicObject executeGeneric(VirtualFrame frame);

	@Specialization(guards = { "scopeident == scopeident_cached" })
	public DynamicObject executeCachedProto(ScopeIdentifier scopeident, Object links,
			@Cached("lookupListClass()") Class<? extends IListTerm<FrameLink>> linksListClass,
			@Cached("scopeident") ScopeIdentifier scopeident_cached,
			@Cached("getContext().getProtoFrame(scopeident_cached)") DynamicObject protoFrame_cached,
			@Cached("createFrameCloner()") CloneFrame cloner) {
		DynamicObject clone = cloner.executeWithEvaluatedFrame(protoFrame_cached);
		setLinks(clone, linksListClass.cast(links).toArray());
		return clone;
	}


	@Specialization(replaces = "executeCachedProto")
	public DynamicObject executeUncached(ScopeIdentifier scopeident, Object links,
			@Cached("lookupListClass()") Class<? extends IListTerm<FrameLink>> linksListClass,
			@Cached("createFrameCloner()") CloneFrame cloner) {
		DynamicObject clone = cloner.executeWithEvaluatedFrame(getContext().getProtoFrame(scopeident));
		setLinks(clone, linksListClass.cast(links).toArray());
		return clone;
	}

	private void setLinks(DynamicObject frm, FrameLink[] links) {
		for (int i = 0; i < links.length; i++) {
			FrameLink link = links[i];
			// FIXME: investigate optimizing this with location caches
			frm.set(link.link(), link.frame());
		}
	}

	protected CloneFrame createFrameCloner() {
		CompilerAsserts.neverPartOfCompilation();
		return CloneFrameNodeGen.create(getSourceSection(), null);
	}

	protected Class<? extends IListTerm<FrameLink>> lookupListClass() {
		CompilerAsserts.neverPartOfCompilation();
		return getContext().getTermRegistry().getListClass(FrameLink.class);
	}

	// protected DynamicObject lookupProtoFrame(ScopeIdentifier scopeident) {
	// CompilerAsserts.neverPartOfCompilation();
	// return getContext().getProtoFrame(scopeident);
	// }

}
