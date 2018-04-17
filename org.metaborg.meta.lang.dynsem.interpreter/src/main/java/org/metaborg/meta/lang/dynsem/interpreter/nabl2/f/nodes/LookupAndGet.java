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
		@NodeChild(value = "occurrence", type = TermBuild.class) })
public abstract class LookupAndGet extends NativeOpBuild {

	public LookupAndGet(SourceSection source) {
		super(source);
	}

	@Specialization
	public ValSort executeLookup(DynamicObject frm, Occurrence occurrence) {
		throw new RuntimeException("LookupAndGet not implemented");
	}

	public static LookupAndGet create(SourceSection source, TermBuild frm, TermBuild occurrence) {
		return LookupAndGetNodeGen.create(source, frm, occurrence);
	}

}
