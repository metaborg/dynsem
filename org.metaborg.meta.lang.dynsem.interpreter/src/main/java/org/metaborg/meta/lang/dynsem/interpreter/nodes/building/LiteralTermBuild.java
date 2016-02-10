package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.interpreter.framework.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.frame.FrameDescriptor;
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

		public static IntLiteralTermBuild create(IStrategoAppl t,
				FrameDescriptor fd) {
			return new IntLiteralTermBuild(Integer.parseInt(Tools
					.stringAt(t, 0).stringValue()),
					SourceSectionUtil.fromStrategoTerm(t));
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

	public static final class TrueLiteralTermBuild extends LiteralTermBuild {

		public TrueLiteralTermBuild(SourceSection source) {
			super(source);
		}

		public static TrueLiteralTermBuild create(IStrategoAppl t,
				FrameDescriptor fd) {
			return new TrueLiteralTermBuild(
					SourceSectionUtil.fromStrategoTerm(t));
		}

		@Override
		public Object executeGeneric(VirtualFrame frame) {
			return true;
		}

		@Override
		public boolean executeBoolean(VirtualFrame frame) {
			return true;
		}

	}

	public static final class FalseLiteralTermBuild extends LiteralTermBuild {

		public FalseLiteralTermBuild(SourceSection source) {
			super(source);
		}

		public static FalseLiteralTermBuild create(IStrategoAppl t,
				FrameDescriptor fd) {
			return new FalseLiteralTermBuild(
					SourceSectionUtil.fromStrategoTerm(t));
		}

		@Override
		public Object executeGeneric(VirtualFrame frame) {
			return false;
		}

		@Override
		public boolean executeBoolean(VirtualFrame frame) {
			return false;
		}

	}

}
