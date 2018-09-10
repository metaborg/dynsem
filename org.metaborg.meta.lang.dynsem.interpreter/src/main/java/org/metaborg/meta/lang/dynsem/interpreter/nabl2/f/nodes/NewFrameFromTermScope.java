package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes.ScopeNodeFactories;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

public abstract class NewFrameFromTermScope extends NativeOpBuild {

	@Child private NewFrame2 newframe;

	public NewFrameFromTermScope(SourceSection source, TermBuild ast, TermBuild links) {
		super(source);
		this.newframe = FrameNodeFactories.createNewFrame2(source, ScopeNodeFactories.createScopeOfTerm(source, ast));
	}

	@Specialization
	public DynamicObject executeNewFrame(VirtualFrame frame) {
		return newframe.executeGeneric(frame);
	}

	public static NewFrameFromTermScope create(SourceSection source, TermBuild ast, TermBuild links) {
		return FrameNodeFactories.createNewFrameFromTermScope(source, ast, links);
	}

}
