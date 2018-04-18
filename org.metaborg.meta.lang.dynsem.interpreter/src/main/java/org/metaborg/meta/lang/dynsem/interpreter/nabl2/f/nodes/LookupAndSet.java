package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;
import org.metaborg.meta.lang.dynsem.interpreter.terms.shared.ValSort;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class),
		@NodeChild(value = "occurrence", type = TermBuild.class), @NodeChild(value = "val", type = TermBuild.class) })
public abstract class LookupAndSet extends NativeOpBuild {

	public LookupAndSet(SourceSection source) {
		super(source);
	}

	@Specialization
	public ValSort executeLookup(DynamicObject frm, Occurrence occurrence, ValSort val) {
		throw new IllegalStateException("LookupAndSet not implemented");
	}

	public static LookupAndSet create(SourceSection source, TermBuild frm, TermBuild occurrence, TermBuild val) {
		return LookupAndSetNodeGen.create(source, frm, occurrence, val);
	}

}
