package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemContext;
import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;

import com.oracle.truffle.api.source.SourceSection;

public class TestRuleRegistry {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void testRuleCount0() {
		RuleRegistry registry = new RuleRegistry();

		assertEquals(0, registry.ruleCount());
	}

	@Test
	public void testRuleCount1() {
		RuleRegistry registry = new RuleRegistry();

		JointRuleRoot jrr1 = new JointRuleRoot(SourceSection.createUnavailable("unit", "test"), RuleKind.TERM, "arrow1",
				String.class, new Rule[] {});
		JointRuleRoot jrr2 = new JointRuleRoot(SourceSection.createUnavailable("unit", "test"), RuleKind.TERM, "arrow1",
				int.class, new Rule[] {});

		registry.registerJointRule("arrow1", String.class, jrr1);
		registry.registerJointRule("arrow1", int.class, jrr2);

		assertEquals(0, registry.ruleCount());
	}

	@Test
	public void testRuleCount2() {
		RuleRegistry registry = new RuleRegistry();

		JointRuleRoot jrr1 = new JointRuleRoot(SourceSection.createUnavailable("unit", "test"), RuleKind.TERM, "arrow1",
				String.class, new Rule[2]);

		JointRuleRoot jrr2 = new JointRuleRoot(SourceSection.createUnavailable("unit", "test"), RuleKind.TERM, "arrow1",
				int.class, new Rule[3]);

		registry.registerJointRule("arrow1", String.class, jrr1);
		registry.registerJointRule("arrow1", int.class, jrr2);

		assertEquals(5, registry.ruleCount());
	}

	@Test
	public void testRegisterJointRule() {
		RuleRegistry registry = new RuleRegistry();

		JointRuleRoot jrr1 = new JointRuleRoot(SourceSection.createUnavailable("unit", "test"), RuleKind.TERM, "arrow1",
				String.class, new Rule[2]);

		registry.registerJointRule("fred", String.class, jrr1);

		assertEquals(jrr1, registry.lookupRules("fred", String.class));
	}

	@Test
	public void testRegisterJointRuleReregister() {
		RuleRegistry registry = new RuleRegistry();

		JointRuleRoot jrr1 = new JointRuleRoot(SourceSection.createUnavailable("unit", "test"), RuleKind.TERM, "fred",
				String.class, new Rule[2]);
		JointRuleRoot jrr2 = new JointRuleRoot(SourceSection.createUnavailable("unit", "test"), RuleKind.TERM, "fred",
				String.class, new Rule[2]);

		registry.registerJointRule("fred", String.class, jrr1);

		registry.registerJointRule("fred", String.class, jrr2);
		assertEquals(jrr2, registry.lookupRules("fred", String.class));

	}

	@Test
	public void testLookupRulesRuleFound() {
		RuleRegistry registry = new RuleRegistry();

		JointRuleRoot jrr1 = new JointRuleRoot(SourceSection.createUnavailable("unit", "test"), RuleKind.TERM, "fred",
				String.class, new Rule[2]);
		registry.registerJointRule("fred", String.class, jrr1);

		assertEquals(jrr1, registry.lookupRules("fred", String.class));
	}

	@Test
	public void testLookupRulesRuleAdHoc() {
		RuleRegistry registry = new RuleRegistry();

		JointRuleRoot jrr = registry.lookupRules("fred", String.class);

		assertNotNull(jrr);

		JointRuleNode jr = jrr.getJointNode();

		assertNotNull(jr);
		assertEquals("fred", jr.getArrowName());
		assertEquals(String.class, jr.getDispatchClass());
		assertEquals(RuleKind.ADHOC, jr.getKind());

		JointRuleRoot jrr2 = registry.lookupRules("fred", String.class);
		assertEquals(jrr, jrr2);

	}

}
