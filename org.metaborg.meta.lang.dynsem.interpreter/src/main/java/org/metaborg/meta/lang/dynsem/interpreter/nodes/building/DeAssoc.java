/**
 * 
 */
package org.metaborg.meta.lang.dynsem.interpreter.nodes.building;

import org.metaborg.meta.lang.dynsem.interpreter.nodes.rules.ReductionFailure;
import org.metaborg.meta.lang.dynsem.interpreter.terms.IListTerm;
import org.metaborg.meta.lang.dynsem.interpreter.utils.InterpreterUtils;
import org.metaborg.meta.lang.dynsem.interpreter.utils.MapUtils;

import com.github.krukow.clj_ds.PersistentMap;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "left", type = TermBuild.class),
		@NodeChild(value = "right", type = TermBuild.class) })
public abstract class DeAssoc extends TermBuild {

	public DeAssoc(SourceSection source) {
		super(source);
	}

	@Specialization
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object doMap(PersistentMap map, Object key) {
		Object res = MapUtils.get(map, key);
		if (res == null) {
			throw new ReductionFailure("No map entry for key: " + key, InterpreterUtils.createStacktrace(), this);
		}
		return res;
	}

	@Specialization
	@SuppressWarnings("rawtypes")
	public Object doList(IListTerm list, int idx) {
		return list.get(idx);
	}

}
