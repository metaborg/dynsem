package org.metaborg.meta.lang.dynsem.interpreter.nodes.matching;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.TestGenericMatchFactory.Test_D_0_Term_MatchPatternNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

public class TestGenericMatch {

	private static SourceSection src;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		src = SourceSection.createUnavailable("unit", "test");
		DynSemLanguage lang = mock(DynSemLanguage.class);
		DynSemContext.LANGUAGE = lang;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DynSemContext.LANGUAGE = null;
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecuteMatchSuccessOnce() {
		Test_D_0_Term t = new Test_D_0_Term();
		RootNode rd = new RootNode(DynSemContext.LANGUAGE.getClass(), src, new FrameDescriptor()) {

			@Child private MatchPattern patt = Test_D_0_Term_MatchPatternNodeGen.create(src);

			@Override
			public Object execute(VirtualFrame frame) {
				this.adoptChildren();
				patt.executeMatch(frame, t);
				return null;
			}
		};
		VirtualFrame frame = mock(VirtualFrame.class);

		rd.execute(frame);

	}

	@Test
	public void testExecuteMatchSuccessTwice() {
		Test_D_0_Term t = new Test_D_0_Term();
		RootNode rd = new RootNode(DynSemContext.LANGUAGE.getClass(), src, new FrameDescriptor()) {

			@Child private MatchPattern patt = Test_D_0_Term_MatchPatternNodeGen.create(src);

			@Override
			public Object execute(VirtualFrame frame) {
				this.adoptChildren();
				patt.executeMatch(frame, t);
				return null;
			}
		};
		VirtualFrame frame = mock(VirtualFrame.class);

		rd.execute(frame);
		rd.execute(frame);
	}

	@Test(expected = PatternMatchFailure.class)
	public void testExecuteMatchFailOnce() {
		String t = "hello world";
		RootNode rd = new RootNode(DynSemContext.LANGUAGE.getClass(), src, new FrameDescriptor()) {

			@Child private MatchPattern patt = Test_D_0_Term_MatchPatternNodeGen.create(src);

			@Override
			public Object execute(VirtualFrame frame) {
				this.adoptChildren();
				patt.executeMatch(frame, t);
				return null;
			}
		};
		VirtualFrame frame = mock(VirtualFrame.class);

		rd.execute(frame);

	}

	@Test
	public void testExecuteMatchFailTwice() {
		String t = "hello world";
		RootNode rd = new RootNode(DynSemContext.LANGUAGE.getClass(), src, new FrameDescriptor()) {

			@Child private MatchPattern patt = Test_D_0_Term_MatchPatternNodeGen.create(src);

			@Override
			public Object execute(VirtualFrame frame) {
				this.adoptChildren();
				patt.executeMatch(frame, t);
				return null;
			}
		};
		VirtualFrame frame = mock(VirtualFrame.class);

		boolean thrown = false;
		try {
			rd.execute(frame);
		} catch (PatternMatchFailure pmfx) {
			thrown = true;
		}

		assertTrue(thrown);
		thrown = false;
		try {
			rd.execute(frame);
		} catch (PatternMatchFailure pmfx) {
			thrown = true;
		}
		assertTrue(thrown);

	}

	@Test
	public void testExecuteMatchFailPolymorphic() {
		RootNode rd = new RootNode(DynSemContext.LANGUAGE.getClass(), src, new FrameDescriptor()) {

			@Child private MatchPattern patt = Test_D_0_Term_MatchPatternNodeGen.create(src);

			private int runs = 0;

			@Override
			public Object execute(VirtualFrame frame) {
				this.adoptChildren();
				if (runs == 0) {
					runs++;
					patt.executeMatch(frame, "hello world");
				} else {
					patt.executeMatch(frame, new Test_D_0_Term());
				}
				return null;
			}
		};
		VirtualFrame frame = mock(VirtualFrame.class);

		boolean thrown = false;
		try {
			rd.execute(frame);
		} catch (PatternMatchFailure pmfx) {
			thrown = true;
		}

		assertTrue(thrown);

		rd.execute(frame);
	}

	public static class Test_D_0_Term implements IApplTerm {

		@Override
		public int size() {
			return 0;
		}

		@Override
		public ITermInstanceChecker getCheck() {
			return new ITermInstanceChecker() {

				@Override
				public boolean isInstance(Object obj) {
					return obj instanceof Test_D_0_Term;
				}
			};
		}

		@Override
		public Class<?> getSortClass() {
			return IApplTerm.class;
		}

		@Override
		public boolean hasStrategoTerm() {
			return false;
		}

		@Override
		public IStrategoTerm getStrategoTerm() {
			return null;
		}
	}

	public static abstract class Test_D_0_Term_MatchPattern extends MatchPattern {

		public Test_D_0_Term_MatchPattern(SourceSection source) {
			super(source);
		}

		@Specialization
		public void doTyped(VirtualFrame frame, Test_D_0_Term d0) {

		}

		@Fallback
		public void doGeneric(VirtualFrame frame, Object term) {
			if (term instanceof Test_D_0_Term) {
				doTyped(frame, (Test_D_0_Term) term);
			} else {
				throw PatternMatchFailure.INSTANCE;
			}
		}

	}
}
