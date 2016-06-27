package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
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

		public static StringLiteralTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
			CompilerAsserts.neverPartOfCompilation();
			return new StringLiteralTermBuild(Tools.javaStringAt(t, 0), SourceSectionUtil.fromStrategoTerm(t));
		}

		@Override
		public String executeGeneric(VirtualFrame frame) {
			return executeString(frame);
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

		public static IntLiteralTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
			CompilerAsserts.neverPartOfCompilation();
			return new IntLiteralTermBuild(Integer.parseInt(Tools.javaStringAt(t, 0)),
					SourceSectionUtil.fromStrategoTerm(t));
		}

		@Override
		public Integer executeGeneric(VirtualFrame frame) {
			return executeInteger(frame);
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

		public static TrueLiteralTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
			assert Tools.hasConstructor(t, "True", 0);
			return new TrueLiteralTermBuild(SourceSectionUtil.fromStrategoTerm(t));
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

		public static FalseLiteralTermBuild create(IStrategoAppl t, FrameDescriptor fd) {
			assert Tools.hasConstructor(t, "False", 0);
			return new FalseLiteralTermBuild(SourceSectionUtil.fromStrategoTerm(t));
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
