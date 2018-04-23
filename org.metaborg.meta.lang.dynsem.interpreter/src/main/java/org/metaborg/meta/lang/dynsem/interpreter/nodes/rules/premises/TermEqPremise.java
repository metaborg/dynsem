package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IApplTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.github.krukow.clj_lang.PersistentList;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.profiles.ValueProfile;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "left", type = TermBuild.class),
		@NodeChild(value = "right", type = TermBuild.class) })
public abstract class TermEqPremise extends Premise {

	public TermEqPremise(SourceSection source) {
		super(source);
	}

	public static TermEqPremise create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "TermEq", 2);
		TermBuild lhs = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild rhs = TermBuild.create(Tools.applAt(t, 1), fd);
		return TermEqPremiseNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), lhs, rhs);
	}

	@Specialization
	public void doBoolean(boolean left, boolean right, @Cached("createCountingProfile()") ConditionProfile profile) {
		if (profile.profile(left != right)) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	@Specialization
	public void doInt(int right, int left, @Cached("createCountingProfile()") ConditionProfile profile) {
		if (profile.profile(left != right)) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	@Specialization(guards = { "left == cachedLeft", "right == cachedRight" }, limit = "1")
	public void doString(String left, String right, @Cached("left") String cachedLeft,
			@Cached("right") String cachedRight, @Cached("doStringEq(cachedLeft, cachedRight)") boolean isEqual,
			@Cached("createBinaryProfile()") ConditionProfile profile) {
		if (profile.profile(!isEqual)) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	@TruffleBoundary
	protected boolean doStringEq(String s1, String s2) {
		return s1.equals(s2);
	}

	@Specialization(guards = { "left == cachedLeft", "right == cachedRight" })
	public void doITermDirect(IApplTerm left, IApplTerm right, @Cached("left") IApplTerm cachedLeft,
			@Cached("right") IApplTerm cachedRight, @Cached("cachedLeft.equals(cachedRight)") boolean isEqual,
			@Cached("createBinaryProfile()") ConditionProfile profile) {
		if (profile.profile(!isEqual)) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	@Specialization(replaces = "doITermDirect")
	public void doITermIndirect(IApplTerm left, IApplTerm right,
			@Cached("createClassProfile()") ValueProfile leftTypeProfile,
			@Cached("createClassProfile()") ValueProfile rightTypeProfile,
			@Cached("createBinaryProfile()") ConditionProfile profile) {
		if (profile.profile(!leftTypeProfile.profile(left).equals(rightTypeProfile.profile(right)))) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	@SuppressWarnings("rawtypes")
	@Specialization(guards = { "left == cachedLeft", "right == cachedRight" }, limit = "1")
	public void doListDirect(PersistentList left, PersistentList right, @Cached("left") PersistentList cachedLeft,
			@Cached("right") PersistentList cachedRight, @Cached("doListEq(cachedLeft, cachedRight)") boolean isEqual,
			@Cached("createBinaryProfile()") ConditionProfile profile) {
		if (profile.profile(!isEqual)) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	@TruffleBoundary
	@SuppressWarnings("rawtypes")
	protected boolean doListEq(PersistentList l1, PersistentList l2) {
		return l1.equals(l2);
	}

	private final ConditionProfile profile = ConditionProfile.createCountingProfile();

	@Fallback
	@TruffleBoundary
	public void doObject(Object left, Object right) {
		if (profile.profile(!left.equals(right))) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}
}
