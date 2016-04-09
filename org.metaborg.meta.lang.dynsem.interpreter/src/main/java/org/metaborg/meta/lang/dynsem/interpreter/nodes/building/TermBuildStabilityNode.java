package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.profiles.ConditionProfile;

// TODO rewrite this using the specialization API
public abstract class TermBuildStabilityNode extends Node {

	public static TermBuildStabilityNode create(TermBuild build) {
		return new _Uninitialized(build);
	}

	public abstract void execute(VirtualFrame frame);

	private static class _Uninitialized extends TermBuildStabilityNode {
		@Child private TermBuild build;

		public _Uninitialized(TermBuild build) {
			this.build = build;
		}

		@Override
		public void execute(VirtualFrame frame) {
			replace(new _Stable(build, build.executeGeneric(frame)));
		}
	}

	private static class _Stable extends TermBuildStabilityNode {

		@Child private TermBuild build;
		private final Object value;

		public _Stable(TermBuild build, Object value) {
			this.build = build;
			this.value = value;
		}

		private ConditionProfile condition = ConditionProfile.createBinaryProfile();

		@Override
		public void execute(VirtualFrame frame) {
			if (condition.profile(build.executeGeneric(frame) != value)) {
				throw TermBuildUnstableException.INSTANCE;
			}
		}

	}

}
