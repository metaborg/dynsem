package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.TrueLiteralTermMatchPatternNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises.CaseMatchPremise.CaseMatchFailure;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

public class TestCase {

	private static SourceSection src;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		src = SourceSection.createUnavailable("test", "unit");
		DynSemContext.LANGUAGE = mock(DynSemLanguage.class);
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
	public void testExecuteCaseOtherwise() {
		VirtualFrame f = mock(VirtualFrame.class);
		Premise p = mock(Premise.class);
		Case c = new Case.CaseOtherwise(src, new Premise[] { p });
		c.execute(f, true);

		verify(p).execute(f);
	}

	@Test
	public void testExecuteCaseMatchSuccess() {
		VirtualFrame f = mock(VirtualFrame.class);
		Premise p = mock(Premise.class);

		Case c = new Case.CasePattern(src, TrueLiteralTermMatchPatternNodeGen.create(src), new Premise[] { p });
		c.execute(f, true);

		verify(p).execute(f);
	}

	@Test(expected = CaseMatchFailure.class)
	public void testExecuteCaseMatchFailure() {
		VirtualFrame f = mock(VirtualFrame.class);
		Premise p = mock(Premise.class);

		Case c = new Case.CasePattern(src, TrueLiteralTermMatchPatternNodeGen.create(src), new Premise[] { p });
		try {
			c.execute(f, false);
		} catch (CaseMatchFailure cmfx) {
			verify(p, never()).execute(f);
			throw cmfx;
		}
	}

}
