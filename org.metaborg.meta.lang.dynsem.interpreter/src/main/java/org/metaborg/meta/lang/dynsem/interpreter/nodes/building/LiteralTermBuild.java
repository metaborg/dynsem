package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public abstract class LiteralTermBuild extends TermBuild {

	public LiteralTermBuild(SourceSection source) {
		super(source);
	}

	public static final class StringLiteralTermBuild extends LiteralTermBuild {

		private final String val;

		public StringLiteralTermBuild(String val, SourceSection source) {
			super(source);
			this.val = val;
		}

		@Override
		public Object executeGeneric(VirtualFrame frame) {
			return val;
		}

		@Override
		public String executeString(VirtualFrame frame) {
			return val;
		}
	}

	public static final class IntLiteralTermBuild extends LiteralTermBuild {

		private final int val;

		public IntLiteralTermBuild(int val, SourceSection source) {
			super(source);
			this.val = val;
		}

		@Override
		public Object executeGeneric(VirtualFrame frame) {
			return val;
		}

		@Override
		public int executeInteger(VirtualFrame frame) {
			return val;
		}

	}

}
