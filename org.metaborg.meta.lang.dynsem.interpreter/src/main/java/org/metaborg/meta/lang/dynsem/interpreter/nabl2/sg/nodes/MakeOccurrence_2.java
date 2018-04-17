package org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.nodes;

import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.Occurrence;
import org.metaborg.meta.lang.dynsem.interpreter.nabl2.sg.TermIndex;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.Fresh;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.NativeOpBuild;
import org.metaborg.meta.lang.dynsem.interpreter.nodes.building.TermBuild;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.SourceSection;

@NodeChildren({ @NodeChild(value = "namespace", type = TermBuild.class),
		@NodeChild(value = "name", type = TermBuild.class) })
public abstract class MakeOccurrence_2 extends NativeOpBuild {

	@Child private TermBuild freshGen;

	public MakeOccurrence_2(SourceSection source) {
		super(source);
		this.freshGen = new Fresh(source);
	}

	@Specialization
	public Occurrence execBuild(VirtualFrame frame, String namespace, String name) {
		return new Occurrence(namespace, name, new TermIndex("<phantom>", freshGen.executeInteger(frame)));
	}

	public static NativeOpBuild create(SourceSection source, TermBuild namespace, TermBuild name) {
		return MakeOccurrence_2NodeGen.create(source, namespace, name);
	}

}
