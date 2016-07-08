package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.LiteralTermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.FalseLiteralTermMatchPatternNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.IntLiteralTermMatchPatternNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.StringLiteralTermMatchPatternNodeGen;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.TrueLiteralTermMatchPatternNodeGen;
import org.strategoxt.stratego_lib.pattern_match_1_0;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;
import static org.mockito.Mockito.*;

public class TestCaseMatchPremise {

	private static SourceSection src;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		src = SourceSection.createUnavailable("unit", "test");
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

	@Test(expected = PatternMatchFailure.class)
	public void testExecuteNoCases() {
		TermBuild t = new LiteralTermBuild.TrueLiteralTermBuild(src);
		CaseMatchPremise caseP = new CaseMatchPremise(src, t, new Case[0]);
		caseP.execute(mock(VirtualFrame.class));
	}

	@Test
	public void testExecuteTwoPatternCases() {
		TermBuild t = new LiteralTermBuild.TrueLiteralTermBuild(src);
		Premise p1 = mock(Premise.class);
		Case case1 = new Case.CasePattern(src, FalseLiteralTermMatchPatternNodeGen.create(src), new Premise[] { p1 });

		Premise p2 = mock(Premise.class);
		Case case2 = new Case.CasePattern(src, TrueLiteralTermMatchPatternNodeGen.create(src), new Premise[] { p2 });

		CaseMatchPremise caseP = new CaseMatchPremise(src, t, new Case[] { case1, case2 });

		VirtualFrame f = mock(VirtualFrame.class);
		caseP.execute(f);

		verify(p1, never()).execute(f);
		verify(p2, times(1)).execute(f);
	}

	@Test
	public void testExecuteTwoPatternCasesOneOtherwise() {

		TermBuild t = new LiteralTermBuild.TrueLiteralTermBuild(src);
		Premise p1 = mock(Premise.class);
		Case case1 = new Case.CasePattern(src, FalseLiteralTermMatchPatternNodeGen.create(src), new Premise[] { p1 });

		Premise p2 = mock(Premise.class);
		Case case2 = new Case.CasePattern(src, FalseLiteralTermMatchPatternNodeGen.create(src), new Premise[] { p2 });

		Premise p3 = mock(Premise.class);
		Case case3 = new Case.CaseOtherwise(src, new Premise[] { p3 });
		
		CaseMatchPremise caseP = new CaseMatchPremise(src, t, new Case[] { case1, case2, case3 });

		VirtualFrame f = mock(VirtualFrame.class);
		caseP.execute(f);

		verify(p1, never()).execute(f);
		verify(p2, never()).execute(f);
		verify(p3).execute(f);
	}

}
