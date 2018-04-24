package org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.premises;

import org.metaborg.meta.lang.dynsem.interpreter.DynSemLanguage;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.PremiseFailureException;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.NodeUtil;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "termNode", type = TermBuild.class),
		@NodeChild(value = "firstCase", type = Case2.class, executeWith = "termNode") })
public abstract class CaseMatchPremise extends Premise {

	public CaseMatchPremise(SourceSection source) {
		super(source);
	}

	private final ConditionProfile profile = ConditionProfile.createBinaryProfile();

	@Specialization
	public void executeCases(Object t, boolean success) {
		if (!profile.profile(success)) {
			throw PremiseFailureException.SINGLETON;
		}
	}

	@Override
	@TruffleBoundary
	public String toString() {
		return NodeUtil.printCompactTreeToString(this);
	}

	public static CaseMatchPremise create(DynSemLanguage lang, IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "CaseMatch", 2);
		TermBuild tb = TermBuild.create(Tools.applAt(t, 0), fd);

		return CaseMatchPremiseNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), tb,
				Case2.create(lang, Tools.listAt(t, 1), fd));
	}

}
