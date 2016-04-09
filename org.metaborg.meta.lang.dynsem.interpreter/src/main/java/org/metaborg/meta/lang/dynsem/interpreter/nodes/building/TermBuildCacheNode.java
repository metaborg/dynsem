package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "wrapped", type = TermBuild.class)
public abstract class TermBuildCacheNode extends TermBuild {

	@Child private TermBuildDependenciesNode dependencies;

	public TermBuildCacheNode(SourceSection source, TermBuildDependenciesNode dependencies) {
		super(source);
		this.dependencies = dependencies;
	}

	public static TermBuild create(TermBuild wrapped) {
		if (wrapped instanceof VarRead || wrapped instanceof ArgRead || wrapped instanceof NativeOpTermBuild) {
			return wrapped;
		} else {
			List<TermBuild> depdendencyList = new ArrayList<>();
			depdendencyList.addAll(NodeUtil.findAllNodeInstances(wrapped, VarRead.class));
			depdendencyList.addAll(NodeUtil.findAllNodeInstances(wrapped, ArgRead.class));
			return TermBuildCacheNodeGen.create(wrapped.getSourceSection(),
					TermBuildDependenciesNode.create(depdendencyList.toArray(new TermBuild[0])), wrapped);
		}
	}

	protected boolean checkGuard(VirtualFrame frame) {
		try {
			dependencies.execute(frame);
			return true;
		} catch (TermBuildUnstableException e) {
			return false;
		}
	}

	// @Specialization(rewriteOn = TermBuildUnstableException.class, limit="1")
	@Specialization(guards = "checkGuard(frame)")
	public Object doCached(VirtualFrame frame, Object term, @Cached("term") Object cachedTerm) {
		return cachedTerm;
	}

	@Specialization(contains = "doCached")
	public Object doEvaluated(VirtualFrame frame, Object term) {
		return term;
	}

}
