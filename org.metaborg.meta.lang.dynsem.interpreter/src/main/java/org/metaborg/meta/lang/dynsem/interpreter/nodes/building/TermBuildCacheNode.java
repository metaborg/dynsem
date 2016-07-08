package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeVisitor;
import com.oracle.truffle.api.source.SourceSection;

@NodeChild(value = "wrapped", type = TermBuild.class)
public abstract class TermBuildCacheNode extends TermBuild {

	@Child private TermBuildDependenciesNode dependencies;

	public TermBuildCacheNode(SourceSection source, TermBuildDependenciesNode dependencies) {
		super(source);
		this.dependencies = dependencies;
	}

	public static TermBuild create(TermBuild wrapped) {
		if (wrapped instanceof VarRead || wrapped instanceof ArgRead) {
			return wrapped;
		}
		List<TermBuild> dependencies = new ArrayList<>();
		try {
			wrapped.accept(new NodeVisitor() {

				@Override
				public boolean visit(Node node) {
					if (node instanceof TermBuild) {
						TermBuild tb = (TermBuild) node;
						if (tb instanceof VarRead || tb instanceof ArgRead) {
							dependencies.add(tb);
						} else if (tb instanceof NativeOpTermBuild || tb instanceof Fresh
								|| tb instanceof SortFunCallBuild) {
							throw TermBuildUnstableException.INSTANCE;
						}
					}
					return true;
				}
			});
		} catch (TermBuildUnstableException tbex) {
			return wrapped;
		}
		return TermBuildCacheNodeGen.create(wrapped.getSourceSection(),
				TermBuildDependenciesNode.create(dependencies.toArray(new TermBuild[0])), wrapped);
	}

	protected boolean checkGuard(VirtualFrame frame) {
		try {
			dependencies.execute(frame);
			return true;
		} catch (TermBuildUnstableException e) {
			return false;
		}
	}

	@Specialization(guards = "checkGuard(frame)")
	public Object doCached(VirtualFrame frame, Object term, @Cached("term") Object cachedTerm) {
		return cachedTerm;
	}

	@Specialization(contains = "doCached")
	public Object doEvaluated(VirtualFrame frame, Object term) {
		return term;
	}

}
