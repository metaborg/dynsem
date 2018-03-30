/**
 * 
 */
package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.MapUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "left", type = TermBuild.class),
		@NodeChild(value = "right", type = TermBuild.class) })
public abstract class DeAssoc extends TermBuild {

	public DeAssoc(SourceSection source) {
		super(source);
	}

	public static DeAssoc create(IStrategoAppl t, FrameDescriptor fd) {
		CompilerAsserts.neverPartOfCompilation();
		assert Tools.hasConstructor(t, "DeAssoc", 2);
		TermBuild left = TermBuild.create(Tools.applAt(t, 0), fd);
		TermBuild right = TermBuild.create(Tools.applAt(t, 1), fd);

		return DeAssocNodeGen.create(SourceUtils.dynsemSourceSectionFromATerm(t), left, right);
	}

	private final ConditionProfile profile = ConditionProfile.createBinaryProfile();

	@Specialization
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object doMap(PersistentMap map, Object key) {
		Object res = MapUtils.get(map, key);
		if (profile.profile(res == null)) {
			throw new ReductionFailure("No map entry for key: " + key, InterpreterUtils.createStacktrace());
		}
		return res;
	}

	@Specialization
	@SuppressWarnings("rawtypes")
	public Object doList(IListTerm list, int idx) {
		return list.get(idx);
	}

}
