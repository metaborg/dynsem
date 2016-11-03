/**
 * 
 */
package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.SourceSectionUtil;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
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

		return DeAssocNodeGen.create(SourceSectionUtil.fromStrategoTerm(t), left, right);
	}

	@Specialization
	@SuppressWarnings("rawtypes")
	public Object doMap(PersistentMap map, Object key) {
		Object res = map.get(key);
		if (res != null) {
			return res;
		}
		throw new IllegalStateException("No map entry for key: " + key);
	}

	@Specialization
	@SuppressWarnings("rawtypes")
	public Object doList(IListTerm list, int idx) {
		return list.get(idx);
	}

	@Override
	public Object executeGeneric(VirtualFrame frame) {
		// TODO Auto-generated method stub
		return null;
	}

}
