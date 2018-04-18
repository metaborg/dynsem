package org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.f.Addr;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "frm", type = TermBuild.class),
		@NodeChild(value = "occurrence", type = TermBuild.class) })
public abstract class Lookup extends NativeOpBuild {

	public Lookup(SourceSection source) {
		super(source);
	}

	@Specialization
	public Addr executeLookup(DynamicObject frm, Occurrence occurrence) {
		throw new IllegalStateException("Lookup not implemented");
	}

	public static Lookup create(SourceSection source, TermBuild frm, TermBuild occurrence) {
		return LookupNodeGen.create(source, frm, occurrence);
	}

}
