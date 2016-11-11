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
import org.metaborg.meta.lang.dynsem.interpreter.ITermRegistry;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.ITermInstanceChecker;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.MatchPattern;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.matching.PatternMatchFailure;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.terms.ITerm;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

/**
 * Tests for the dispatch mechanism of the interpreter. These tests cover the aspects of rule dispatching such as
 * looking up a rule, applying a rule.
 * 
 * @author vladvergu
 *
 */
public class TestDispatch {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DynSemLanguage lang = mock(DynSemLanguage.class);
		DynSemContext ctx = mock(DynSemContext.class);

		when(lang.getContext()).thenReturn(ctx);
		when(lang.createFindContextNode0()).thenReturn(null);
		when(lang.findContext0(null)).thenReturn(ctx);

		DynSemContext.LANGUAGE = lang;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		reset(DynSemContext.LANGUAGE.getContext());
		reset(DynSemContext.LANGUAGE);
		DynSemContext.LANGUAGE = null;
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = ReductionFailure.class)
	public void testExecuteNoTarget() {
		RuleRegistry registry = new RuleRegistry();
		DynSemContext ctx = DynSemContext.LANGUAGE.getContext();
		assertNotNull(ctx);
		when(ctx.getRuleRegistry()).thenReturn(registry);

		IApplTerm t = mock(IApplTerm.class);

		when(t.getSortClass()).thenReturn((Class) IApplTerm.class);

		RootNode rn = new RootNode(DynSemLanguage.class, getDummySource(), new FrameDescriptor()) {

			@Child private DispatchNode dispatch = DispatchNodeGen.create(getDummySource(), "fred");

			@Override
			public Object execute(VirtualFrame frame) {
				adoptChildren();
				return dispatch.execute(frame, String.class, frame.getArguments());
			}
		};
		Truffle.getRuntime().createCallTarget(rn);

		rn.getCallTarget().call(t);

		reset(ctx);
	}

	private static SourceSection getDummySource() {
		return SourceSection.createUnavailable("unit", "test");
	}

	@Test
	public void testExecuteSingleTarget() throws Exception {
		RuleRegistry registry = new RuleRegistry();
		DynSemContext ctx = DynSemContext.LANGUAGE.getContext();
		assertNotNull(ctx);
		when(ctx.getRuleRegistry()).thenReturn(registry);

		// an object for the sort class
		IApplTerm sortT = mock(IApplTerm.class);

		// an object
		IApplTerm t = mock(IApplTerm.class);

		// it's sort class is the class of the term mocked above
		when(t.getSortClass()).thenReturn((Class) sortT.getClass());

		// a real dummy rule but we spy on it
		DummyRule r = new DummyRule(RuleKind.TERM, "freddie", t.getClass(), true, new RuleResult(null, null));

		JointRuleRoot rr = new JointRuleRoot(getDummySource(), r.getKind(), r.getArrowName(), r.getDispatchClass(),
				new Rule[] { r });

		when(r.getParent()).thenReturn(rr.getJointNode().getUnionNode());

		rr.adoptChildren();

		registry.registerJointRule(r.getArrowName(), r.getDispatchClass(), rr);

		rr.getCallTarget().call(new Object[] { t });

		// the rule should be called exactly once
		assertEquals(1, r.hitcount);
	}

	@Test
	public void testExecuteMultiTarget() throws Exception {
		RuleRegistry registry = new RuleRegistry();
		DynSemContext ctx = DynSemContext.LANGUAGE.getContext();
		assertNotNull(ctx);
		when(ctx.getRuleRegistry()).thenReturn(registry);

		// an object for the sort class
		IApplTerm sortT = mock(IApplTerm.class);

		// an object
		IApplTerm t = mock(IApplTerm.class);

		// it's sort class is the class of the term mocked above
		when(t.getSortClass()).thenReturn((Class) sortT.getClass());

		// a few real dummy rules that we can spy on
		DummyRule r1 = new DummyRule(RuleKind.TERM, "freddie", t.getClass(), false, null);
		DummyRule r2 = new DummyRule(RuleKind.TERM, "freddie", t.getClass(), false, null);
		DummyRule r3 = new DummyRule(RuleKind.TERM, "freddie", t.getClass(), false, null);
		DummyRule r4 = new DummyRule(RuleKind.TERM, "freddie", t.getClass(), true, null);

		JointRuleRoot rr = new JointRuleRoot(getDummySource(), r1.getKind(), r1.getArrowName(), r1.getDispatchClass(),
				new Rule[] { r1, r2, r3, r4 });

		rr.adoptChildren();

		RuleSetNode union = rr.getJointNode().getUnionNode();

		assertEquals(4, union.getRules().size());

		assertEquals(union, r1.getParent());
		assertEquals(union, r2.getParent());
		assertEquals(union, r3.getParent());
		assertEquals(union, r4.getParent());

		registry.registerJointRule(r1.getArrowName(), r1.getDispatchClass(), rr);

		rr.getCallTarget().call(new Object[] { t });

		// all rules should be called exactly once
		assertEquals(1, r1.hitcount);
		assertEquals(1, r2.hitcount);
		assertEquals(1, r3.hitcount);
		assertEquals(1, r4.hitcount);
	}

	@Test
	public void testExecuteFallbackToSortSuccess() throws Exception {
		RuleRegistry registry = new RuleRegistry();
		DynSemContext ctx = DynSemContext.LANGUAGE.getContext();
		assertNotNull(ctx);
		when(ctx.getRuleRegistry()).thenReturn(registry);

		// an object for the sort class
		ITerm sortT = mock(ITerm.class);
		// an object
		IApplTerm t = mock(IApplTerm.class);

		// it's sort class is the class of the term mocked above
		when(t.getSortClass()).thenReturn((Class) sortT.getClass());

		// a few real dummy rules that we can spy on
		DummyRule r1 = new DummyRule(RuleKind.TERM, "freddie", t.getClass(), false, null);

		JointRuleRoot rr1 = new JointRuleRoot(getDummySource(), r1.getKind(), r1.getArrowName(), r1.getDispatchClass(),
				new Rule[] { r1 });
		rr1.adoptChildren();
		registry.registerJointRule(r1.getArrowName(), r1.getDispatchClass(), rr1);

		RuleSetNode union = rr1.getJointNode().getUnionNode();
		assertEquals(1, union.getRules().size());
		assertEquals(union, r1.getParent());

		DummyRule r2 = new DummyRule(RuleKind.TERM, "freddie", sortT.getClass(), true, null);
		JointRuleRoot rr2 = new JointRuleRoot(getDummySource(), r2.getKind(), r2.getArrowName(), r2.getDispatchClass(),
				new Rule[] { r2 });

		rr2.adoptChildren();
		registry.registerJointRule(r2.getArrowName(), r2.getDispatchClass(), rr2);

		RuleSetNode union2 = rr2.getJointNode().getUnionNode();
		assertEquals(1, union2.getRules().size());
		assertEquals(union2, r2.getParent());

		assertEquals(2, registry.ruleCount());
		assertEquals(rr1, registry.lookupRules("freddie", t.getClass()));
		assertEquals(rr2, registry.lookupRules("freddie", t.getSortClass()));

		rr1.getCallTarget().call(new Object[] { t });

		// the failing rule should be called exactly once
		assertEquals(1, r1.getHitcount());
	}

	public static class DummyRule extends Rule {

		private boolean succeed;
		private RuleResult result;
		private int hitcount = 0;

		public DummyRule(RuleKind kind, String arrowName, Class<?> dispatchClass, boolean succeed, RuleResult result) {
			super(getDummySource(), new FrameDescriptor(), kind, arrowName, dispatchClass);
			this.succeed = succeed;
			this.result = result;
		}

		public int getHitcount() {
			return hitcount;
		}

		@Override
		public RuleResult execute(VirtualFrame frame) {
			hitcount++;
			if (succeed) {
				return result;
			} else {
				throw PatternMatchFailure.INSTANCE;
			}
		}

	}

}
