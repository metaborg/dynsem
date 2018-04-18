package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.FrameLink;
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
		assert getContext().getTermRegistry().getListClass(FrameLink.class).isInstance(links);

		throw new IllegalStateException("Frame creation not implemented");
	}

	public static NewFrame create(SourceSection source, TermBuild scope, TermBuild links) {
		return NewFrameNodeGen.create(source, scope, links);
	}

}
